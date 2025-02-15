package ch.guytomoki.challenge.signingRequest;

import ch.guytomoki.challenge.document.DocumentRequestDto;
import ch.guytomoki.challenge.document.DocumentRespDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static ch.guytomoki.challenge.RestPaths.SIGNING_REQUEST_PATH;

@RestController
@RequestMapping(SIGNING_REQUEST_PATH)
public class SigningRequestController {

	private final ISigningRequestService signingRequestService;

	public SigningRequestController(ISigningRequestService signingRequestService) {
		this.signingRequestService = signingRequestService;
	}


	@GetMapping
	public ResponseEntity<SigningRequestRespDto> getSigningRequest(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(signingRequestService.retrieveAndMapSigningRequest(userDetails));
	}

	@PostMapping()
	public ResponseEntity<SigningRequestRespDto> create(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(signingRequestService.create(userDetails));
	}

	@PostMapping("sign")
	public ResponseEntity<SigningRequestRespDto> sign(@AuthenticationPrincipal UserDetails userDetails) {
		SigningRequestRespDto signingRequestRespDto = signingRequestService.sign(userDetails);

		return ResponseEntity.ok()
			.body(signingRequestRespDto);
	}
}
