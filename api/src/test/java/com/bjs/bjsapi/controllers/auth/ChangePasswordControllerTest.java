package com.bjs.bjsapi.controllers.auth;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.bjs.bjsapi.controllers.auth.AuthenticationController;
import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.repository.UserRepository;
import com.bjs.bjsapi.security.BJSUserDetailsService;
import com.bjs.bjsapi.security.BJSUserPrincipal;

@WebMvcTest(ChangePasswordController.class)
class ChangePasswordControllerTest {

	@MockBean
	private UserRepository userRepository;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private PasswordEncoder encoder;

	@MockBean
	private BJSUserDetailsService bjsUserDetailsService;

	@Test
	void changePasswordUnauthenticated() throws Exception {
		mvc
			.perform(post("/api/v2/auth/password")
				.with(anonymous()))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void changePasswordSuccessfully() throws Exception {
		final User testUser = User
			.builder()
			.password(encoder.encode("password"))
			.build();

		final BJSUserPrincipal userPrincipal = new BJSUserPrincipal(testUser);

		when(userRepository.findByUsername(any())).thenReturn(Optional.of(testUser));

		mvc
			.perform(post("/api/v2/auth/password")
				.with(user(userPrincipal))
				.param("password","new_password")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		assertThat(encoder.matches("new_password", testUser.getPassword())).isTrue();
		verify(userRepository).save(testUser);
	}

}