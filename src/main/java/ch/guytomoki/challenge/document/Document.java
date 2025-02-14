package ch.guytomoki.challenge.document;

import ch.guytomoki.challenge.signingRequest.SigningRequest;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

@Entity
@Table(name = "documents")
public class Document {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false)
	private boolean isConfirmed;

	@Column(nullable = false)
	private String fileName;

	@Lob
	@JdbcTypeCode(Types.BINARY)
	@Column(name = "file_content", columnDefinition = "BYTEA")
	private byte[] fileContent;

	@ManyToOne
	private SigningRequest signingRequest;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean getIsConfirmed() {
		return isConfirmed;
	}

	public void setIsConfirmed(boolean confirmed) {
		isConfirmed = confirmed;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	public SigningRequest getSigningRequest() {
		return signingRequest;
	}

	public void setSigningRequest(SigningRequest signingRequest) {
		this.signingRequest = signingRequest;
	}
}
