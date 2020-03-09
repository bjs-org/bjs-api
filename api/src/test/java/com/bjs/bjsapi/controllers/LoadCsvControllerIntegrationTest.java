package com.bjs.bjsapi.controllers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("in-memory-db")
@SpringBootTest
@AutoConfigureMockMvc
public class LoadCsvControllerIntegrationTest {

	@Autowired
	private MockMvc mvc;
	private final Path exampleFile = Paths.get("src/test/resources/sample_student_csv.csv");
	private final Path exampleWrongFile = Paths.get("src/test/resources/sample_student_csv_wrong_format.csv");

	@Test
	@WithMockUser(value = "admin", roles = { "ADMIN" })
	void test_upload_response() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "sample_student_csv.csv", "text/csv", Files.readAllBytes(exampleFile));

		mvc.perform(multipart("/api/v1/classes/upload").file(multipartFile))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("_embedded.classes", hasSize(1)))
			.andExpect(jsonPath("_embedded.classes.[*].className", hasItem("a")))
			.andExpect(jsonPath("_embedded.classes.[*].grade", hasItem("8")))
			.andExpect(jsonPath("_embedded.classes.[*].students.[*].firstName", hasItems("Liam Sky", "Ayk", "Jalen")))
			.andExpect(jsonPath("_embedded.classes.[*].students.[*].lastName", hasItems("Heß", "Borstelmann", "Buscemi")))
			.andExpect(jsonPath("_embedded.classes.[*].students.[*].female", hasItems(false, false, false)))
			.andExpect(jsonPath("_embedded.classes.[*].students.[*].birthDay", hasItems("2001-08-15", "2002-03-28", "2001-11-24")));

		mvc.perform(get("/api/v1/classes"))
			.andExpect(jsonPath("_embedded.classes.[*].className", hasItem("a")))
			.andExpect(jsonPath("_embedded.classes.[*].grade", hasItem("8")));

		mvc.perform(get("/api/v1/students"))
			.andExpect(jsonPath("_embedded.students.[*].firstName", hasItems("Liam Sky", "Ayk", "Jalen")))
			.andExpect(jsonPath("_embedded.students.[*].lastName", hasItems("Heß", "Borstelmann", "Buscemi")))
			.andExpect(jsonPath("_embedded.students.[*].female", hasItems(false, false, false)))
			.andExpect(jsonPath("_embedded.students.[*].birthDay", hasItems("2001-08-15", "2002-03-28", "2001-11-24")));
	}

	@Test
	@WithMockUser(value = "admin", roles = { "ADMIN" })
	void test_upload_response_wrong_format() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "sample_student_csv.csv", "text/csv", Files.readAllBytes(exampleWrongFile));

		mvc.perform(multipart("/api/v1/classes/upload").file(multipartFile))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

}
