package com.bjs.bjsapi.controllers.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bjs.bjsapi.database.repository.UserRepository;
import com.bjs.bjsapi.security.BJSUserPrincipal;

@RestController
@RequestMapping("/api/v2/auth/password")
public class ChangePasswordController {

	private final UserRepository userRepository;
	private final PasswordEncoder encoder;

	public ChangePasswordController(UserRepository userRepository, PasswordEncoder encoder) {
		this.userRepository = userRepository;
		this.encoder = encoder;
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping
	public ResponseEntity<?> changePassword(Authentication authentication, @RequestParam(required = false) String password) {
		if (authentication != null && password != null) {
			final BJSUserPrincipal userPrincipal = AuthenticationHelper.convertToUserPrinciple(authentication);

			userRepository
				.findByUsername(userPrincipal.getUsername())
				.ifPresent(user -> {
					user.setPassword(encoder.encode(password));
					userRepository.save(user);
				});

			return ResponseEntity
				.ok()
				.build();
		}

		return ResponseEntity
			.badRequest()
			.build();
	}

}
