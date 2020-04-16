package com.bjs.bjsapi.controllers.auth;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import com.bjs.bjsapi.controllers.auth.AuthenticationController;
import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.security.BJSUserDetailsService;
import com.bjs.bjsapi.security.BJSUserPrincipal;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerInfoTest {

	@MockBean
	private BJSUserDetailsService bjsUserDetailsService;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		User user = User
			.builder()
			.username("administrator")
			.administrator(true)
			.build();

		BJSUserPrincipal principal = new BJSUserPrincipal(user);
		when(bjsUserDetailsService.loadUserByUsername("administrator")).thenReturn(principal);
	}

	@Test
	void isAccessible() throws Exception {
		mockMvc
			.perform(get("/api/v2/auth"))
			.andExpect(status().is(not(404)));
	}

	@Test
	@WithAnonymousUser
	void requiresAuthentication() throws Exception {
		mockMvc
			.perform(get("/api/v2/auth"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void returnsAuthenticationInformation() throws Exception {

		User admin = User
			.builder()
			.username("administrator")
			.administrator(true)
			.build();
		BJSUserPrincipal userPrincipal = new BJSUserPrincipal(admin);

		mockMvc
			.perform(
				get("/api/v2/auth")
					.with(user(userPrincipal))
			)
			.andExpect(status().isOk())
			.andExpect(content().json("{\n" +
				"  \"username\": \"administrator\",\n" +
				"  \"administrator\": true\n" +
				"}"));
	}

}