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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SigningRequestControllerIT {

	private final MockMvc mockMvc;
	private final DataSource dataSource;

	@Autowired
	private SigningRequestControllerIT(MockMvc mockMvc, DataSource dataSource) {
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
	void shouldReturnSigningRequest() throws Exception {
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

		this.mockMvc.perform(get("/api/signingRequest")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.isSigned").value(false))
			.andExpect(jsonPath("$.submissionDays").value(5))
			.andExpect(jsonPath("$.documents").isArray())
			.andExpect(jsonPath("$.documents", hasSize(4)));

	}

	@Test
	void shouldNotReturnSigningRequestIfAuthorizationHeaderMissing() throws Exception {
		this.mockMvc.perform(get("/api/signingRequest"))
			.andExpect(status().isForbidden());
	}

	@Test
	void shouldNotSignSigningRequestIfAnyDocumentNotConfirmed() throws Exception {
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

		this.mockMvc.perform(get("/api/signingRequest")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.isSigned").value(false))
			.andExpect(jsonPath("$.submissionDays").value(5))
			.andExpect(jsonPath("$.documents").isArray())
			.andExpect(jsonPath("$.documents", hasSize(4)))
			.andExpect(jsonPath("$.documents[0].isConfirmed").value(false));

		this.mockMvc.perform(post("/api/signingRequest/sign")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.message").value("All documents should be confirmed before signing."));
	}

	@Test
	void shouldSignSigningRequestIfAllDocumentsConfirmed() throws Exception {
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

		this.mockMvc.perform(get("/api/signingRequest")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.isSigned").value(false))
			.andExpect(jsonPath("$.submissionDays").value(5))
			.andExpect(jsonPath("$.documents").isArray())
			.andExpect(jsonPath("$.documents", hasSize(4)))
			.andExpect(jsonPath("$.documents[0].isConfirmed").value(false));

		this.mockMvc.perform(post("/api/document/1/confirm")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.isConfirmed").value(true));

		this.mockMvc.perform(post("/api/document/2/confirm")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(2))
			.andExpect(jsonPath("$.isConfirmed").value(true));

		this.mockMvc.perform(post("/api/document/3/confirm")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(3))
			.andExpect(jsonPath("$.isConfirmed").value(true));

		this.mockMvc.perform(post("/api/document/4/confirm")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(4))
			.andExpect(jsonPath("$.isConfirmed").value(true));

		this.mockMvc.perform(post("/api/signingRequest/sign")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSigned").value(true));
	}

	@Test
	void shouldNotSignSigningRequestIfAlreadySigned() throws Exception {
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

		this.mockMvc.perform(get("/api/signingRequest")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.isSigned").value(false))
			.andExpect(jsonPath("$.submissionDays").value(5))
			.andExpect(jsonPath("$.documents").isArray())
			.andExpect(jsonPath("$.documents", hasSize(4)))
			.andExpect(jsonPath("$.documents[0].isConfirmed").value(false));

		this.mockMvc.perform(post("/api/document/1/confirm")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.isConfirmed").value(true));

		this.mockMvc.perform(post("/api/document/2/confirm")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(2))
			.andExpect(jsonPath("$.isConfirmed").value(true));

		this.mockMvc.perform(post("/api/document/3/confirm")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(3))
			.andExpect(jsonPath("$.isConfirmed").value(true));

		this.mockMvc.perform(post("/api/document/4/confirm")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(4))
			.andExpect(jsonPath("$.isConfirmed").value(true));

		this.mockMvc.perform(post("/api/signingRequest/sign")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSigned").value(true));

		this.mockMvc.perform(post("/api/signingRequest/sign")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.message").value("Signing request has already been signed."));
	}

}

