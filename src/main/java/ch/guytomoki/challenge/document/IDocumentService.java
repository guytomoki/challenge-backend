package ch.guytomoki.challenge.document;

import ch.guytomoki.challenge.signingRequest.SigningRequest;

public interface IDocumentService {

	Document retrieveDocument(Long documentId);

	DocumentRespDto create(DocumentRequestDto documentRequestDto, SigningRequest signingRequest);

	DocumentRespDto retrieveAndMapDocument(Long documentId);

	DocumentRespDto confirmDocument(Long documentId);
}
