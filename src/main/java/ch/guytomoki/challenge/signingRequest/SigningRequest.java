package ch.guytomoki.challenge.signingRequest;

import ch.guytomoki.challenge.document.Document;
import ch.guytomoki.challenge.jwt.user.User;
import jakarta.persistence.*;

import java.time.*;
import java.util.List;

@Entity
@Table(name = "signing_requests")
public class SigningRequest {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false)
	private boolean isSigned;

	@Column(name = "signDate")
	private Instant signDate;

	@Column
	private Instant submissionDate;

	@Transient
	private Long submissionDays;

	@ManyToOne
	private User user;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "signingRequest")
	private List<Document> documents;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean getIsSigned() {
		return isSigned;
	}

	public void setIsSigned(boolean signed) {
		isSigned = signed;
	}

	public Instant getSignDate() {
		return signDate;
	}

	public void setSignDate(Instant signDate) {
		this.signDate = signDate;
	}

	public Instant getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Instant submissionDate) {
		this.submissionDate = submissionDate;
	}

	public Long getSubmissionDays() {
		return Duration.between(this.submissionDate, LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()).toDays();
	}

	public void setSubmissionDays(Long submissionDays) {
		this.submissionDays = submissionDays;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
}
