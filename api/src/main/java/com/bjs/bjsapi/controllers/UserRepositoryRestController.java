package com.bjs.bjsapi.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.repository.UserRepository;

@RepositoryRestController
@ExposesResourceFor(User.class)
@RequestMapping("/api/v1/users")
public class UserRepositoryRestController {

	private final UserRepository userRepository;
	private final ResourceAssembler<User, Resource<User>> userResourceAssembler;

	public UserRepositoryRestController(UserRepository userRepository, ResourceAssembler<User, Resource<User>> userResourceAssembler) {
		this.userRepository = userRepository;
		this.userResourceAssembler = userResourceAssembler;
	}

	@GetMapping
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> findAll() {
		List<Resource<User>> collect = userRepository.findAll()
			.stream()
			.map(userResourceAssembler::toResource)
			.collect(Collectors.toList());

		Resources<Resource<User>> resources = new Resources<>(collect, linkTo(methodOn(UserRepositoryRestController.class).findAll()).withSelfRel());

		return ResponseEntity.ok(resources);
	}

	@GetMapping("{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> findById(@PathVariable Long id) {
		return userRepository.findById(id)
			.map(userResourceAssembler::toResource)
			.map(ResponseEntity::ok)
			.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

}
