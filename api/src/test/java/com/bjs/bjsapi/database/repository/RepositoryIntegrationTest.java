package com.bjs.bjsapi.database.repository;

import static com.bjs.bjsapi.security.helper.RunWithAuthentication.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import java.time.Clock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.model.helper.UserBuilder;
import com.bjs.bjsapi.security.BJSUserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("in-memory-db")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
abstract class RepositoryIntegrationTest {

	@MockBean
	private Clock clock;

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

	User user;
	private User admin;

	static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@BeforeEach
	void setUp() throws Exception {
		setupTestUsers();
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

		assertThat(user).isNotNull();
		assertThat(admin).isNotNull();
	}

	private void setupTestUsers() {
		runAsAdmin(() -> {
			admin = new UserBuilder().setUsername("testAdmin").createUser();
			admin.setAdministrator(true);
			admin.setPassword("admin");

			user = new UserBuilder().setUsername("testUser").createUser();
			user.setPassword("user");

			userRepository.save(user);
			userRepository.save(admin);
		});
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
		return user(new BJSUserPrincipal(admin));
	}

	RequestPostProcessor asUser() {
		return user(new BJSUserPrincipal(user));
	}

}

