package ch.guytomoki.challenge.jwt.token;

import ch.guytomoki.challenge.jwt.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tokens")
public class Token {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	@Column(unique = true)
	public String token;

	@Enumerated(EnumType.STRING)
	public ETokenType tokenType = ETokenType.BEARER;

	public boolean revoked;

	public boolean expired;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	public User user;
}
