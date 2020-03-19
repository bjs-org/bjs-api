package com.bjs.bjsapi.controllers;

import static com.bjs.bjsapi.security.helper.RunWithAuthentication.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.bjs.bjsapi.database.repository.IntegrationTestData;
import com.bjs.bjsapi.database.repository.UserRepository;
import com.bjs.bjsapi.security.BJSUserPrincipal;

@ActiveProfiles("in-memory-db")
@SpringBootTest
@AutoConfigureMockMvc
@Import(IntegrationTestData.class)
class UserInfoControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private IntegrationTestData testData;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		runAsAdmin(() -> {
			testData.setupUsers();
		});
	}

	@AfterEach
	void tearDown() {
		runAsAdmin(() -> userRepository.deleteAll());
	}

	@Test
	void test_get_admin() throws Exception {
		mvc.perform(get("/api/v1/auth")
			.with(asAdmin()))
			.andExpect(jsonPath("administrator", is(true)))
			.andExpect(jsonPath("username", is("administrator")))
			.andExpect(jsonPath("enabled", is(true)));
	}

	@Test
	void test_get_user() throws Exception {
		mvc.perform(get("/api/v1/auth")
			.with(asUser()))
			.andExpect(jsonPath("administrator", is(false)))
			.andExpect(jsonPath("username", is("abcd")))
			.andExpect(jsonPath("enabled", is(true)));
	}

	RequestPostProcessor asAdmin() {
		return user(new BJSUserPrincipal(testData.admin));
	}

	RequestPostProcessor asUser() {
		return user(new BJSUserPrincipal(testData.user));
	}

}