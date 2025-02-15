package ch.guytomoki.challenge.signingRequest;

import ch.guytomoki.challenge.jwt.user.User;
import ch.guytomoki.challenge.jwt.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class SigningRequestService implements ISigningRequestService {

	private final SigningRequestRepository signingRequestRepository;
	private final UserRepository userRepository;
	private final SigningRequestMapper signingRequestMapper;

	public SigningRequestService(SigningRequestRepository signingRequestRepository, UserRepository userRepository, SigningRequestMapper signingRequestMapper) {
		this.signingRequestRepository = signingRequestRepository;
		this.userRepository = userRepository;
		this.signingRequestMapper = signingRequestMapper;
	}

	@Override
	public SigningRequest retrieveSigningRequest(UserDetails userDetails) {
		SigningRequest signingRequest = signingRequestRepository.findFirstByUserEmailOrderById(userDetails.getUsername())
			.orElseThrow((() -> new NoSuchElementException(String.format("No signingRequest found for user '%s'.", userDetails.getUsername()))));
		return signingRequest;
	}

	@Override
	public SigningRequestRespDto retrieveAndMapSigningRequest(UserDetails userDetails) {
		SigningRequest signingRequest = retrieveSigningRequest(userDetails);
		return signingRequestMapper.toRespDto(signingRequest);
	}

	@Override
	public SigningRequestRespDto create(UserDetails userDetails) {
		User user = userRepository.findByEmail(userDetails.getUsername())
			.orElseThrow((() -> new NoSuchElementException(String.format("No user found for email '%s'.", userDetails.getUsername()))));
		Optional<SigningRequest> signingRequestOptional = signingRequestRepository.findFirstByUserEmailOrderById(userDetails.getUsername());
		if(signingRequestOptional.isPresent()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "A signing request already exist for this user.");
		}
		SigningRequest signingRequest = new SigningRequest();
		signingRequest.setIsSigned(false);
		signingRequest.setSubmissionDate(Instant.now());
		signingRequest.setUser(user);
		signingRequestRepository.save(signingRequest);
		return signingRequestMapper.toRespDto(signingRequest);
	}

	@Override
	@Transactional
	public SigningRequestRespDto sign(UserDetails userDetails) throws ResponseStatusException {
		SigningRequest signingRequest = retrieveSigningRequest(userDetails);

		boolean anyNotConfirmed = signingRequest.getDocuments().stream().anyMatch(document->!document.getIsConfirmed());

		if (signingRequest.getIsSigned()) {
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Signing request has already been signed.");
		}
		if (anyNotConfirmed) {
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "All documents should be confirmed before signing.");
		}

		signingRequest.setIsSigned(true);
		signingRequest.setSignDate(Instant.now());

		return signingRequestMapper.toRespDto(signingRequest);
	}
}
