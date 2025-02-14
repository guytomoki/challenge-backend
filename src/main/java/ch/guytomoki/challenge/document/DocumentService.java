package ch.guytomoki.challenge.document;

import ch.guytomoki.challenge.signingRequest.SigningRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@Service
public class DocumentService implements IDocumentService {

	private final DocumentRepository documentRepository;
	private final DocumentMapper documentMapper;

	public DocumentService(DocumentRepository documentRepository, DocumentMapper documentMapper) {
		this.documentRepository = documentRepository;
		this.documentMapper = documentMapper;
	}

	@Override
	public Document retrieveDocument(Long documentId) {
		return documentRepository.findById(documentId).orElseThrow(() -> new NoSuchElementException(String.format("No document found for id '%s'.", documentId)));
	}

	@Override
	@Transactional
	public DocumentRespDto create(DocumentRequestDto documentRequestDto, SigningRequest signingRequest) {
		Document document = documentMapper.toEntity(documentRequestDto);
		document.setSigningRequest(signingRequest);
		documentRepository.save(document);
		return documentMapper.toRespDto(document);
	}

	@Override
	public DocumentRespDto retrieveAndMapDocument(Long documentId) {
		return documentMapper.toRespDto(retrieveDocument(documentId));
	}

	@Override
	@Transactional
	public DocumentRespDto confirmDocument(Long documentId) {
		Document document = retrieveDocument(documentId);

		if (document.getIsConfirmed()) {
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Document has already been confirmed.");
		}

		document.setIsConfirmed(true);

		return documentMapper.toRespDto(document);
	}

}
