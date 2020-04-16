package com.bjs.bjsapi.controllers.auth;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bjs.bjsapi.security.BJSUserPrincipal;

@RestController
@RequestMapping("/api/v2/auth")
public class AuthenticationController {

	@PreAuthorize("isAuthenticated()")
	@GetMapping
	public ResponseEntity<?> getAuthenticationInformation(Authentication authentication) {
		return Optional
			.of(authentication)
			.map(AuthenticationHelper::convertToUserPrinciple)
			.map(BJSUserPrincipal::toUserInfo)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity
				.badRequest()
				.build());
	}

}
