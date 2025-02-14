package ch.guytomoki.challenge.api.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.sql.DataSource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DocumentControllerIT {

	private final MockMvc mockMvc;
	private final DataSource dataSource;

	@Autowired
	private DocumentControllerIT(MockMvc mockMvc, DataSource dataSource) {
		this.mockMvc = mockMvc;
		this.dataSource = dataSource;
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
	void shouldReturnDocument() throws Exception {
		String body = """
            {
                "email": "guytomoki@gmail.com",
                "password": "123123"
            }
        """;

		MvcResult authResult = this.mockMvc.perform(post("/api/auth/authenticate").contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.access_token").exists())
			.andReturn();

		String jsonResponse = authResult.getResponse().getContentAsString();
		JsonNode jsonNode = new ObjectMapper().readTree(jsonResponse);
		String token = jsonNode.get("access_token").asText();

		this.mockMvc.perform(get("/api/document/1")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.isConfirmed").value(false));

	}

	@Test
	void shouldNotReturnDocumentIfAuthorizationHeaderMissing() throws Exception {
		this.mockMvc.perform(get("/api/document/1"))
			.andExpect(status().isForbidden());
	}

	@Test
	void shouldConfirmDocumentIfNotYetConfirmed() throws Exception {
		String body = """
            {
                "email": "guytomoki@gmail.com",
                "password": "123123"
            }
        """;

		MvcResult authResult = this.mockMvc.perform(post("/api/auth/authenticate").contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.access_token").exists())
			.andReturn();

		String jsonResponse = authResult.getResponse().getContentAsString();
		JsonNode jsonNode = new ObjectMapper().readTree(jsonResponse);
		String token = jsonNode.get("access_token").asText();

		this.mockMvc.perform(post("/api/document/1/confirm")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.isConfirmed").value(true));
	}

	@Test
	void shouldNotConfirmDocumentIfAlreadyConfirmed() throws Exception {
		String body = """
            {
                "email": "guytomoki@gmail.com",
                "password": "123123"
            }
        """;

		MvcResult authResult = this.mockMvc.perform(post("/api/auth/authenticate").contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.access_token").exists())
			.andReturn();

		String jsonResponse = authResult.getResponse().getContentAsString();
		JsonNode jsonNode = new ObjectMapper().readTree(jsonResponse);
		String token = jsonNode.get("access_token").asText();

		this.mockMvc.perform(post("/api/document/1/confirm")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.isConfirmed").value(true));

		this.mockMvc.perform(post("/api/document/1/confirm")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.message").value("Document has already been confirmed."));
	}
}
