package ch.guytomoki.challenge.signingRequest;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@Service
public class SigningRequestService implements ISigningRequestService {

	private final SigningRequestRepository signingRequestRepository;
	private final SigningRequestMapper signingRequestMapper;

	public SigningRequestService(SigningRequestRepository signingRequestRepository, SigningRequestMapper signingRequestMapper) {
		this.signingRequestRepository = signingRequestRepository;
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

		return signingRequestMapper.toRespDto(signingRequest);
	}
}
