//package ch.guytomoki.challenge.configs;
//
//import ch.guytomoki.challenge.document.Document;
//import ch.guytomoki.challenge.signingRequest.SigningRequest;
//import ch.guytomoki.challenge.jwt.user.User;
//import ch.guytomoki.challenge.jwt.user.EUserRole;
//import ch.guytomoki.challenge.document.DocumentRepository;
//import ch.guytomoki.challenge.signingRequest.SigningRequestRepository;
//import ch.guytomoki.challenge.jwt.user.UserRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//
//@Configuration
//public class DemoDataConfig {
//
//	private static final String pdfPath = "pdfs/";
//	private static final String file1Name = "Form W-8BEN.pdf";
//	private static final String file2Name = "1195- Identity declaration.pdf";
//	private static final String file3Name = "CGA.pdf";
//	private static final String file4Name = "Terms and conditions with special mention for US citizens.pdf";
//	private static final String demoUserFirstName = "Guy";
//	private static final String demoUserLastName = "Benoit";
//	private static final String demoUserEmail = "guytomoki@gmail.com";
//	private static final String demoUserPassword = "$2a$10$XxRjEh.Tp2iD03Mb/eDAy.YiEUOl6r23fc4PQzSoB2ZWl1qt4UWh.";
//
//	private final UserRepository userRepository;
//	private final SigningRequestRepository signingRequestRepository;
//	private final DocumentRepository documentRepository;
//
//	public DemoDataConfig(UserRepository userRepository, SigningRequestRepository signingRequestRepository, DocumentRepository documentRepository) {
//		this.userRepository = userRepository;
//		this.signingRequestRepository = signingRequestRepository;
//		this.documentRepository = documentRepository;
//	}
//
//	@Bean
//	CommandLineRunner commandLineRunner() {
//		return (args) -> {
//
//			User user = User.builder()
//			.firstname(demoUserFirstName)
//			.lastname(demoUserLastName)
//			.email(demoUserEmail)
//			.password(demoUserPassword)
//			.role(EUserRole.USER).build();
//			// Save test data
//			userRepository.save(user);
//
//			SigningRequest signingRequest = new SigningRequest();
//			signingRequest.setIsSigned(false);
//			signingRequest.setUser(user);
//			signingRequest.setSubmissionDate(LocalDateTime.now().minusDays(5).atZone(ZoneId.systemDefault()).toInstant());
//			signingRequestRepository.save(signingRequest);
//
//			readFileAndSaveDocument(file1Name, signingRequest);
//			readFileAndSaveDocument(file2Name, signingRequest);
//			readFileAndSaveDocument(file3Name, signingRequest);
//			readFileAndSaveDocument(file4Name, signingRequest);
//		};
//	}
//
//	private void readFileAndSaveDocument(String fileName, SigningRequest signingRequest) throws IOException {
//		Resource resource1 = new ClassPathResource(new StringBuilder(pdfPath).append(fileName).toString());
//		byte[] pdfFile1 = Files.readAllBytes(resource1.getFile().toPath());
//
//		// Create and save Document entities
//		Document document1 = new Document();
//		document1.setFileName(fileName);
//		document1.setFileContent(pdfFile1);
//		document1.setIsConfirmed(false);
//		document1.setSigningRequest(signingRequest);
//		documentRepository.save(document1);
//	}
//}
