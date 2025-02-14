package ch.guytomoki.challenge.repositories;

import ch.guytomoki.challenge.signingRequest.SigningRequest;
import ch.guytomoki.challenge.jwt.user.User;
import ch.guytomoki.challenge.jwt.user.EUserRole;
import ch.guytomoki.challenge.jwt.user.UserRepository;
import ch.guytomoki.challenge.signingRequest.SigningRequestRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("unit-test")
class SigningRequestRepositoryTest {

	@Autowired
	private SigningRequestRepository signingRequestRepository;
	@Autowired
	private UserRepository userRepository;

	private SigningRequest signingRequest;

	@BeforeEach
	public void setUp() {
		User user = new User();
		user.setFirstname("John");
		user.setLastname("Doe");
		user.setEmail("john.doe@test.com");
		user.setPassword("123456");
		user.setRole(EUserRole.USER);
		userRepository.save(user);
		signingRequest = new SigningRequest();
		signingRequest.setIsSigned(false);
		LocalDateTime localDateTime = LocalDateTime.of(2025, 1, 1, 0, 0);
		signingRequest.setSubmissionDate(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		signingRequest.setUser(user);
		signingRequestRepository.save(signingRequest);
	}

	@AfterEach
	public void tearDown() {
		signingRequestRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	void shouldFindSigningRequestByUserEmail() {
		Optional<SigningRequest> signingRequestOptional = signingRequestRepository.findFirstByUserEmailOrderById("john.doe@test.com");
		assertThat(signingRequestOptional).isPresent();
		assertThat(signingRequestOptional.get().getUser()).isNotNull();
		assertThat(signingRequestOptional.get().getUser().getEmail()).isEqualTo("john.doe@test.com");
		assertThat(signingRequestOptional.get().getIsSigned()).isEqualTo(false);
		LocalDateTime localDateTime = LocalDateTime.of(2025, 1, 1, 0, 0);
		assertThat(signingRequestOptional.get().getSubmissionDate()).isEqualTo(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	@Test
	void shouldNotFindSigningRequestByNonExistantUserEmail() {
		Optional<SigningRequest> signingRequestOptional = signingRequestRepository.findFirstByUserEmailOrderById("unknown@test.com");
		assertThat(signingRequestOptional).isNotPresent();
	}
}