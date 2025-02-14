package ch.guytomoki.challenge.repositories;

import ch.guytomoki.challenge.jwt.user.User;
import ch.guytomoki.challenge.jwt.user.EUserRole;
import ch.guytomoki.challenge.jwt.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("unit-test")
class UserRepositoryTest {

	@Autowired
	private UserRepository repository;

	private User user;

	@BeforeEach
	public void setUp() {
		user = new User();
		user.setFirstname("John");
		user.setLastname("Doe");
		user.setEmail("john.doe@test.com");
		user.setPassword("123456");
		user.setRole(EUserRole.USER);
		repository.save(user);
	}

	@AfterEach
	public void tearDown() {
		repository.deleteAll();
	}

	@Test
	void shouldFindUserByEmail() {
		Optional<User> userOptional = repository.findByEmail("john.doe@test.com");
		assertThat(userOptional).isPresent();
		assertThat(userOptional.get().getFirstname()).isEqualTo("John");
		assertThat(userOptional.get().getLastname()).isEqualTo("Doe");
		assertThat(userOptional.get().getPassword()).isEqualTo("123456");
		assertThat(userOptional.get().getRole()).isEqualTo(EUserRole.USER);
	}

	@Test
	void shouldNotFindUserByNonExistantEmail() {
		Optional<User> userOptional = repository.findByEmail("unknown@test.com");
		assertThat(userOptional).isNotPresent();
	}
}