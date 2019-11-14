package com.bjs.bjsapi.database.repository;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.model.helper.UserBuilder;
import com.bjs.bjsapi.helper.SecurityHelper;
import com.bjs.bjsapi.helper.ValidationFiles;
import com.bjs.bjsapi.security.BJSUserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;


@ActiveProfiles("in-memory-db")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public abstract class RepositoryIntegrationTest {

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

	protected User user;
	protected User admin;

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@BeforeEach
	public void setUp() throws Exception {
		setupTestUsers();
	}

	@AfterEach
	public void tearDown() throws Exception {
		clearDB();
	}

	@Test
	public void test_initializeCorrectly() {
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
		SecurityHelper.runAs("admin", "admin", "ROLE_ADMIN", "ROLE_USER");

		admin = new UserBuilder().setUsername("testAdmin").createUser();
		admin.setAdministrator(true);
		admin.setPassword("admin");

		user = new UserBuilder().setUsername("testUser").createUser();
		user.setPassword("user");

		userRepository.save(user);
		userRepository.save(admin);

		SecurityHelper.reset();
	}

	private void clearDB() {
		userPrivilegeRepository.deleteAll();
		studentRepository.deleteAll();
		classRepository.deleteAll();
		userRepository.deleteAll();
	}

	RequestPostProcessor asAdmin() {
		return user(new BJSUserPrincipal(admin));
	}

	RequestPostProcessor asUser() {
		return user(new BJSUserPrincipal(user));
	}

	String mask(String response, Object... objects) {
		return ValidationFiles.mask(mask(response), objects);
	}

	String mask(String response) {
		return ValidationFiles.mask(response, "http://[\\w.-]+(:\\d+)?(/[\\w.-]+)+(\\?([\\w-]+=[\\w/-]+(&)?)+)?");
	}

}
