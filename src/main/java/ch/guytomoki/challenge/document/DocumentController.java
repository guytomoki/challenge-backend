package ch.guytomoki.challenge.document;

import ch.guytomoki.challenge.jwt.RegisterRequestDto;
import ch.guytomoki.challenge.signingRequest.ISigningRequestService;
import ch.guytomoki.challenge.signingRequest.SigningRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

import static ch.guytomoki.challenge.RestPaths.DOCUMENT_PATH;

@RestController
@RequestMapping(DOCUMENT_PATH)
public class DocumentController {

	private final IDocumentService documentService;
	private final ISigningRequestService signingRequestService;

	public DocumentController(IDocumentService documentService, ISigningRequestService signingRequestService) {
		this.documentService = documentService;
		this.signingRequestService = signingRequestService;
	}


	@PostMapping()
	public ResponseEntity<DocumentRespDto> create(@AuthenticationPrincipal UserDetails userDetails,
												  @Valid @RequestBody DocumentRequestDto documentRequestDto) {
		SigningRequest signingRequest = signingRequestService.retrieveSigningRequest(userDetails);
		DocumentRespDto documentRespDto = documentService.create(documentRequestDto, signingRequest);

		return ResponseEntity.ok(documentRespDto);
	}

	@GetMapping("{id}")
	public ResponseEntity<DocumentRespDto> getDocument(@PathVariable("id") Long id) {
		DocumentRespDto documentRespDto = documentService.retrieveAndMapDocument(id);
		return ResponseEntity.ok(documentRespDto);
	}

	@GetMapping("{id}/content")
	public ResponseEntity<String> getPdfFile(@PathVariable("id") Long id) {
		Document document = documentService.retrieveDocument(id);

		String pdfContent = Base64.getEncoder().encodeToString(document.getFileContent());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		headers.setContentDispositionFormData("inline", document.getFileName());
		return ResponseEntity.ok()
			.headers(headers)
			.body(pdfContent);
	}


	@PostMapping("{id}/confirm")
	public ResponseEntity<DocumentRespDto> confirmDocument(@PathVariable("id") Long id) {
		DocumentRespDto documentRespDto = documentService.confirmDocument(id);

		return ResponseEntity.ok(documentRespDto);
	}
}
