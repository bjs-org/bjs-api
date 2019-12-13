package com.bjs.bjsapi.database.repository;

import static com.bjs.bjsapi.security.helper.RunWithAuthentication.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.bjs.bjsapi.config.BeanDefinitions;
import com.bjs.bjsapi.controllers.StudentCalculationService;
import com.bjs.bjsapi.helper.CalculationInformationService;
import com.bjs.bjsapi.helper.ClassificationInformationService;
import com.bjs.bjsapi.security.BJSUserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("in-memory-db")
@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Import({ CalculationInformationService.class, StudentCalculationService.class, ClassificationInformationService.class, BeanDefinitions.class, IntegrationTestData.class })
abstract class RepositoryIntegrationTest {

	@Autowired
	protected IntegrationTestData testData;

	@Autowired
	protected MockMvc mvc;

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	protected ClassRepository classRepository;

	@Autowired
	protected StudentRepository studentRepository;

	@Autowired
	protected UserPrivilegeRepository userPrivilegeRepository;

	@Autowired
	protected SportResultRepository sportResultRepository;

	@Autowired
	protected ObjectMapper objectMapper;

	static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() {
		clearDB();
	}

	@Test
	void test_initializeCorrectly() {
		assertThat(mvc).isNotNull();
		assertThat(classRepository).isNotNull();
		assertThat(studentRepository).isNotNull();
		assertThat(sportResultRepository).isNotNull();
		assertThat(userRepository).isNotNull();
		assertThat(userPrivilegeRepository).isNotNull();

		assertThat(testData.admin).isNotNull();
		assertThat(testData.user).isNotNull();
	}

	private void clearDB() {
		runAsAdmin(() -> {
			userPrivilegeRepository.deleteAll();
			sportResultRepository.deleteAll();
			studentRepository.deleteAll();
			classRepository.deleteAll();
			userRepository.deleteAll();
		});
	}

	RequestPostProcessor asAdmin() {
		return user(new BJSUserPrincipal(testData.admin));
	}

	RequestPostProcessor asUser() {
		return user(new BJSUserPrincipal(testData.user));
	}

}

