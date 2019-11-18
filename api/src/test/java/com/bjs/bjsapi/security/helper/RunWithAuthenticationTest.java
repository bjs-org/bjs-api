package com.bjs.bjsapi.security.helper;

import static com.bjs.bjsapi.security.helper.RunWithAuthentication.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.security.Principal;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

class RunWithAuthenticationTest {

	@FunctionalInterface
	private interface ValidationObjectRunnable {

		void validate();

	}

	;

	@FunctionalInterface
	private interface ValidationObjectSupplier<T> {

		T validate();

	}

	;

	@Test
	void test_runWithAuthentication_supplier() {
		Authentication original = mock(Authentication.class);
		Authentication inserted = mock(Authentication.class);

		String correctString = "Correct";
		SecurityContextHolder.getContext().setAuthentication(original);

		ValidationObjectSupplier object = () -> {
			assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(inserted);
			return correctString;
		};

		assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(original);
		assertThat(runWith(inserted, object::validate)).isEqualTo(correctString);
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(original);
	}

	@Test
	void test_runWithAuthentication_callback() {
		Authentication original = mock(Authentication.class);
		Authentication inserted = mock(Authentication.class);

		SecurityContextHolder.getContext().setAuthentication(original);

		ValidationObjectRunnable object = () -> {
			assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(inserted);
		};

		assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(original);
		runWith(inserted, object::validate);
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(original);
	}

	@Test
	void test_toAuthentication() {
		String role_user = "ROLE_USER";
		String role_admin = "ROLE_ADMIN";
		String password = "password";
		String user = "user";

		Authentication authentication = toAuthentication(user, password, role_admin, role_user);

		assertThat(authentication).extracting(Principal::getName).isEqualTo(user);
		assertThat(authentication).extracting(Authentication::getCredentials).isEqualTo(password);
		assertThat(authentication.getAuthorities()).extracting(GrantedAuthority::getAuthority).contains(role_admin, role_user);
	}

}