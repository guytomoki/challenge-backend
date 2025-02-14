package ch.guytomoki.challenge.signingRequest;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

public interface ISigningRequestService {

	SigningRequest retrieveSigningRequest(UserDetails userDetails);

	SigningRequestRespDto retrieveAndMapSigningRequest(UserDetails userDetails);

	SigningRequestRespDto sign(UserDetails userDetails) throws ResponseStatusException;
}
