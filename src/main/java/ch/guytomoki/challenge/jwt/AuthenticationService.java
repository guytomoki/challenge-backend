package ch.guytomoki.challenge.jwt;

import ch.guytomoki.challenge.jwt.token.Token;
import ch.guytomoki.challenge.jwt.user.User;
import ch.guytomoki.challenge.jwt.token.ETokenType;
import ch.guytomoki.challenge.jwt.token.TokenRepository;
import ch.guytomoki.challenge.jwt.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	private final UserRepository repository;
	private final TokenRepository tokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	public AuthenticationResponseDto register(RegisterRequestDto request) {
		if (repository.findByEmail(request.getEmail()).isPresent()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "A user is already registered for this email.");
		}

		var user = User.builder()
			.firstname(request.getFirstname())
			.lastname(request.getLastname())
			.email(request.getEmail())
			.password(passwordEncoder.encode(request.getPassword()))
			.role(request.getRole())
			.build();
		var savedUser = repository.save(user);
		var jwtToken = jwtService.generateToken(user);
		var refreshToken = jwtService.generateRefreshToken(user);
		saveUserToken(savedUser, jwtToken);
		return AuthenticationResponseDto.builder()
			.accessToken(jwtToken)
			.refreshToken(refreshToken)
			.build();
	}

	public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
		authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(
				request.getEmail(),
				request.getPassword()
			)
		);
		var user = repository.findByEmail(request.getEmail())
			.orElseThrow();
		var jwtToken = jwtService.generateToken(user);
		var refreshToken = jwtService.generateRefreshToken(user);
		revokeAllUserTokens(user);
		saveUserToken(user, jwtToken);
		return AuthenticationResponseDto.builder()
			.accessToken(jwtToken)
			.refreshToken(refreshToken)
			.build();
	}

	private void saveUserToken(User user, String jwtToken) {
		var token = Token.builder()
			.user(user)
			.token(jwtToken)
			.tokenType(ETokenType.BEARER)
			.expired(false)
			.revoked(false)
			.build();
		tokenRepository.save(token);
	}

	private void revokeAllUserTokens(User user) {
		var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
		if (validUserTokens.isEmpty())
			return;
		validUserTokens.forEach(token -> {
			token.setExpired(true);
			token.setRevoked(true);
		});
		tokenRepository.saveAll(validUserTokens);
	}

	public AuthenticationResponseDto refreshToken(HttpServletRequest request) {
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String refreshToken;
		final String username;
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}

		refreshToken = authHeader.substring(7);
		username = jwtService.extractUsername(refreshToken);
		if (username != null) {
			User user = repository.findByEmail(username)
				.orElseThrow((() -> new NoSuchElementException(String.format("No user found for email '%s'.", username))));
			if (jwtService.isTokenValid(refreshToken, user)) {
				String newAccessToken = jwtService.generateToken(user);
				revokeAllUserTokens(user);
				saveUserToken(user, newAccessToken);
				return AuthenticationResponseDto.builder()
					.accessToken(newAccessToken)
					.refreshToken(refreshToken)
					.build();
			}
		}
		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
	}

}
