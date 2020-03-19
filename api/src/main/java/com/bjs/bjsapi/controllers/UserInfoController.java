package com.bjs.bjsapi.controllers;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.security.BJSUserDetailsService;
import com.bjs.bjsapi.security.BJSUserPrincipal;
import com.bjs.bjsapi.security.UserInfo;

@RestController
@RequestMapping("/api/v1")
class UserInfoController {

	@GetMapping("/auth")
	public ResponseEntity<?> principal(Principal principal) {
		if (principal instanceof UsernamePasswordAuthenticationToken) {
			final UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;

			if (token.getPrincipal() != null && token.getPrincipal() instanceof BJSUserPrincipal) {
				final BJSUserPrincipal bjsUserPrincipal = (BJSUserPrincipal) token.getPrincipal();

				return ResponseEntity.ok(bjsUserPrincipal.toUserInfo());
			}

		}

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}