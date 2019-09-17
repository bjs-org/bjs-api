package com.bjs.bjsapi.controllers;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.repository.UserRepository;

@RepositoryRestController
@ExposesResourceFor(User.class)
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UserRepositoryRestController {

	private final UserRepository userRepository;
	private final ResourceAssembler<User, Resource<User>> userResourceAssembler;

	public UserRepositoryRestController(UserRepository userRepository, ResourceAssembler<User, Resource<User>> userResourceAssembler) {
		this.userRepository = userRepository;
		this.userResourceAssembler = userResourceAssembler;
	}

	/*@GetMapping
	public ResponseEntity<?> findAll() {
		List<Resource<User>> collect = userRepository.findAll()
			.stream()
			.map(userResourceAssembler::toResource)
			.collect(Collectors.toList());

		Resources<Resource<User>> resources = new Resources<>(collect, linkTo(methodOn(UserRepositoryRestController.class).findAll()).withSelfRel());

		return ResponseEntity.ok(resources);
	}

	@GetMapping("{id}")
	public ResponseEntity<?> findById(@PathVariable Long id) {
		return userRepository.findById(id)
			.map(userResourceAssembler::toResource)
			.map(ResponseEntity::ok)
			.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}*/

	@GetMapping("search/findByUsername")
	public ResponseEntity<?> findByUsername(@RequestParam String username) {
		return userRepository.findByUsername(username)
			.map(userResourceAssembler::toResource)
			.map(ResponseEntity::ok)
			.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
}
