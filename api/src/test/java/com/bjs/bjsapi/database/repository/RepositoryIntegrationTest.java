package com.bjs.bjsapi.database.repository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.helper.ValidationFiles;
import com.bjs.bjsapi.security.BJSUserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
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

	@Before
	public void setUp() throws Exception {
		setupTestUsers();
	}

	@After
	public void tearDown() throws Exception {
		clearDB();
	}

	private void setupTestUsers() {
		admin = new User("testAdmin");
		admin.setAdministrator(true);
		admin.setPassword("admin");

		user = new User("testUser");
		user.setPassword("user");

		userRepository.save(user);
		userRepository.save(admin);
	}

	private void clearDB() {
		userPrivilegeRepository.deleteAll();
		studentRepository.deleteAll();
		classRepository.deleteAll();
		userRepository.deleteAll();
	}

	protected RequestPostProcessor asAdmin() {
		return user(new BJSUserPrincipal(admin));
	}

	protected RequestPostProcessor asUser() {
		return user(new BJSUserPrincipal(user));
	}

	protected String mask(String response, Object... objects) {
		return ValidationFiles.mask(mask(response), objects);
	}

	protected String mask(String response) {
		return ValidationFiles.mask(response, "http://[\\w.-]+(:\\d+)?(/[\\w.-]+)+(\\?([\\w-]+=[\\w/-]+(&)?)+)?");
	}

}

