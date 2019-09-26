package com.bjs.bjsapi.controllers;

import java.security.Principal;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.security.BJSUserPrincipal;

@RestController
@RequestMapping("/")
class UserInfoController {

	@GetMapping("principal")
	public User principal(final Principal principal) {
		final UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
		final BJSUserPrincipal userDetails = (BJSUserPrincipal) token.getPrincipal();
		return userDetails.getUser();
	}

}