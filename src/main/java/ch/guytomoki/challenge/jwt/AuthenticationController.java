package ch.guytomoki.challenge.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static ch.guytomoki.challenge.RestPaths.AUTH_PATH;

@RestController
@RequestMapping(AUTH_PATH)
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService service;

	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponseDto> register(
		@RequestBody RegisterRequestDto request
	) {
		return ResponseEntity.ok(service.register(request));
	}
	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponseDto> authenticate(
		@RequestBody AuthenticationRequestDto request
	) {
		return ResponseEntity.ok(service.authenticate(request));
	}

//	@PostMapping("/refresh-token")
//	public void refreshToken(
//		HttpServletRequest request,
//		HttpServletResponse response
//	) throws IOException {
//		service.refreshToken(request, response);
//	}


}
