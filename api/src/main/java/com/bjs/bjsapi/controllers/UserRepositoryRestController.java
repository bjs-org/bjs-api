package com.bjs.bjsapi.controllers;

import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.model.assemblers.UserAssembler;
import com.bjs.bjsapi.database.repository.UserRepository;

@BasePathAwareController
@ExposesResourceFor(User.class)
public class UserRepositoryRestController {

	private final UserRepository userRepository;
	private final UserAssembler userAssembler;

	public UserRepositoryRestController(UserRepository userRepository, UserAssembler userAssembler) {
		this.userRepository = userRepository;
		this.userAssembler = userAssembler;
	}

	@GetMapping("users/search/findByUsername")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public @ResponseBody
	ResponseEntity<?> findByUsername(@RequestParam String username) {
		return userRepository.findByUsername(username)
			.map(userAssembler::toModel)
			.map(ResponseEntity::ok)
			.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
}

