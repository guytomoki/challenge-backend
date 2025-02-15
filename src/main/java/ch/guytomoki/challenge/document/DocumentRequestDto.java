package ch.guytomoki.challenge.document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DocumentRequestDto(
	@NotBlank
	@Size(min = 5, max = 255)
	String fileName,

	@NotBlank
	String fileContent) {
}
