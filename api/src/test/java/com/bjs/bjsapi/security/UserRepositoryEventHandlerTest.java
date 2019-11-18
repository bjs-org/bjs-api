package com.bjs.bjsapi.security;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.model.helper.UserBuilder;
import com.bjs.bjsapi.database.repository.UserRepository;

class UserRepositoryEventHandlerTest {

	@Mock
	private UserRepository userRepository;
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	private UserRepositoryEventHandler userRepositoryEventHandler;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		userRepositoryEventHandler = new UserRepositoryEventHandler(userRepository, passwordEncoder);
	}

	@Test
	void test_beforeCreate() {
		String password = "password";

		User user = new UserBuilder().setUsername("testUser").createUser();
		user.setPassword(password);
		userRepositoryEventHandler.onBeforeCreate(user);

		assertThat(passwordEncoder.matches(password, user.getPassword())).isTrue();
	}

	@Test
	void test_beforeSave_passwordChange() {
		String newPassword = "password";
		String username = "user";

		User oldUser = new UserBuilder().setUsername(username).createUser();
		oldUser.setPassword(passwordEncoder.encode("old password"));

		doReturn(Optional.of(oldUser)).when(userRepository).findById(any());

		User newUser = new UserBuilder().setUsername(username).createUser();
		newUser.setPassword(newPassword);

		userRepositoryEventHandler.onBeforeSave(newUser);

		assertThat(passwordEncoder.matches(newPassword, newUser.getPassword())).isTrue();
	}

	@Test
	void test_beforeSave_notPasswordChange() {
		String password = "password";
		String username = "user";

		String encodedPassword = passwordEncoder.encode(password);

		User oldUser = new UserBuilder().setUsername(username).createUser();
		oldUser.setPassword(encodedPassword);

		doReturn(Optional.of(oldUser)).when(userRepository).findById(any());

		User newUser = new UserBuilder().setUsername(username).createUser();
		newUser.setPassword(encodedPassword);
		newUser.setAdministrator(true);

		userRepositoryEventHandler.onBeforeSave(newUser);
		assertThat(passwordEncoder.matches(password, newUser.getPassword())).isTrue();
	}

}