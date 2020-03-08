package com.bjs.bjsapi.controllers;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.bjs.bjsapi.TestSecurityConfiguration;
import com.bjs.bjsapi.database.repository.UserRepository;

@WebMvcTest
@Import(TestSecurityConfiguration.class)
class LoadCsvControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private LoadCsvDataService loadCsvDataService;

	@MockBean
	private UserRepository userRepository;

	@Captor
	private ArgumentCaptor<List<String>> csvCaptor;
	private final Path exampleFile = Paths.get("src/test/resources/sample_student_csv.csv");

	@Test
	@WithMockUser("admin")
	void test_upload_get() throws Exception {
		mvc.perform(get("/api/v1/classes/upload/")).andExpect(status().isMethodNotAllowed());
	}

	@Test
	@WithMockUser()
	void test_upload_authentication() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "sample_student_csv.csv", "text/csv", Files.readAllBytes(exampleFile));

		mvc.perform(multipart("/api/v1/classes/upload").file(multipartFile))
			.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(value = "admin", roles = { "ADMIN" })
	void test_upload() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "sample_student_csv.csv", "text/csv", Files.readAllBytes(exampleFile));

		mvc.perform(multipart("/api/v1/classes/upload").file(multipartFile))
			.andExpect(status().isOk());

		verify(loadCsvDataService).loadAndSaveCsv(csvCaptor.capture());

		final List<String> lines = csvCaptor.getValue();

		assertThat(lines).containsAll(Files.readAllLines(exampleFile));
	}

}
