package ch.guytomoki.challenge.signingRequest;

import ch.guytomoki.challenge.document.DocumentRespDto;

import java.util.List;

public record SigningRequestRespDto(Long id, boolean isSigned, Long submissionDays, List<DocumentRespDto> documents) {
}
