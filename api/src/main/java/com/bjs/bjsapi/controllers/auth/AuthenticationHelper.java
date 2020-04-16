package com.bjs.bjsapi.controllers.auth;

import org.springframework.security.core.Authentication;

import com.bjs.bjsapi.security.BJSUserPrincipal;

public class AuthenticationHelper {

	public static BJSUserPrincipal convertToUserPrinciple(Authentication authentication) {
		final Object principal = authentication.getPrincipal();
		if (!(principal instanceof BJSUserPrincipal)) {
			throw new IllegalArgumentException("Authentication is not an instance of User Principle");
		} else {
			return (BJSUserPrincipal) principal;
		}
	}

}
