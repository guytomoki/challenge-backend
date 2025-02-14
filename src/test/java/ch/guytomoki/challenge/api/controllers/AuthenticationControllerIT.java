package ch.guytomoki.challenge.api.controllers;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthenticationControllerIT {

	private final MockMvc mockMvc;
	private final DataSource dataSource;

	@Autowired
	private AuthenticationControllerIT(MockMvc mockMvc, DataSource dataSource, DataSource dataSource1) {
		this.mockMvc = mockMvc;
		this.dataSource = dataSource1;
	}

	@BeforeEach
	void resetDatabase() {
		Flyway flyway = Flyway.configure()
			.dataSource(dataSource)
			.cleanDisabled(false)
			.load();
		flyway.clean();
		flyway.migrate();
	}

	@Test
	void shouldAuthenticateExistingUser() throws Exception {
		String body = """
            {
                "email": "guytomoki@gmail.com",
                "password": "123123"
            }
        """;

		this.mockMvc.perform(post("/api/auth/authenticate").contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.access_token").exists());
	}

	@Test
	void shouldNotAuthenticateUserWithWrongEmail() throws Exception {
		String body = """
            {
                "email": "unknow@test.com",
                "password": "123123"
            }
        """;

		this.mockMvc.perform(post("/api/auth/authenticate").contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldNotAuthenticateUserWithEmailMissing() throws Exception {
		String body = """
            {
                "password": "123123"
            }
        """;

		this.mockMvc.perform(post("/api/auth/authenticate").contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldNotAuthenticateUserWithWrongPassword() throws Exception {
		String body = """
            {
                "email": "guytomoki@gmail.com",
                "password": "wrongpass"
            }
        """;

		this.mockMvc.perform(post("/api/auth/authenticate").contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldNotAuthenticateUserWithPasswordMissing() throws Exception {
		String body = """
            {
                "email": "guytomoki@gmail.com"
            }
        """;

		this.mockMvc.perform(post("/api/auth/authenticate").contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().isUnauthorized());
	}
}
