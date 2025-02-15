package ch.guytomoki.challenge;

import io.jsonwebtoken.io.DeserializationException;
import jakarta.validation.ConstraintViolationException;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;


@ControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


	// On JPA entities constraint validation
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Object> handleException(ConstraintViolationException exception) {
		return buildResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleException(IllegalArgumentException exception) {
		return buildResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Object> handleException(BadRequestException exception) {
		return buildResponseEntity(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<Object> handleException(NoSuchElementException exception) {
		return buildResponseEntity(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<Object> handleException(BadCredentialsException exception) {
		return buildResponseEntity(HttpStatus.UNAUTHORIZED, exception.getMessage());
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Object> handleException(ResponseStatusException exception) {
		return buildResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY, exception.getReason());
	}

	@ExceptionHandler(UnsupportedOperationException.class)
	public ResponseEntity<Object> handleException(UnsupportedOperationException exception) {
		return buildResponseEntity(HttpStatus.METHOD_NOT_ALLOWED, exception.getMessage());
	}

	@ExceptionHandler(DeserializationException.class)
	public ResponseEntity<Object> handleException(DeserializationException exception) {
		return buildResponseEntity(HttpStatus.FORBIDDEN, exception.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException exception) {
		Map<String, String> errors = new HashMap<>();
		exception.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return buildResponseEntity(HttpStatus.BAD_REQUEST, "Unvalid fields", errors);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleException(Exception exception) {
		return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error");
	}

	private ResponseEntity<Object> buildResponseEntity(HttpStatus status, String message) {
		return buildResponseEntity(status, message, null);
	}

	private ResponseEntity<Object> buildResponseEntity(HttpStatus status, String message, Map<String, String> errors) {
		log.warn(String.format("Returned exception: %s, %s", status, message));
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", status.value());
		body.put("message", message);
		if (errors != null) {
			body.put("errors", errors);
		}
		return new ResponseEntity<>(body, status);
	}
}
