package ch.guytomoki.challenge.jwt;

import ch.guytomoki.challenge.jwt.user.EUserRole;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {

	@NotBlank
	@Size(min = 1, max = 255)
	private String firstname;

	@NotBlank
	@Size(min = 1, max = 255)
	private String lastname;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	@Size(min = 6, max = 255)
	private String password;

	@NotNull
	private EUserRole role;
}