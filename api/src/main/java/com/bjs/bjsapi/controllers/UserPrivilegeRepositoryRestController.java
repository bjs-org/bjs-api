package com.bjs.bjsapi.controllers;

import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bjs.bjsapi.database.model.UserPrivilege;
import com.bjs.bjsapi.database.model.assemblers.UserPrivilegeAssembler;
import com.bjs.bjsapi.database.repository.UserPrivilegeRepository;

@BasePathAwareController
@ExposesResourceFor(UserPrivilege.class)
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("user_privileges")
public class UserPrivilegeRepositoryRestController {

	private final UserPrivilegeRepository userPrivilegeRepository;
	private final UserPrivilegeAssembler resourceAssembler;

	public UserPrivilegeRepositoryRestController(UserPrivilegeRepository userPrivilegeRepository, UserPrivilegeAssembler resourceAssembler) {
		this.userPrivilegeRepository = userPrivilegeRepository;
		this.resourceAssembler = resourceAssembler;
	}

	@GetMapping("")
	public @ResponseBody
	ResponseEntity<?> findAll() {
		return ResponseEntity.ok(resourceAssembler.toCollectionModel(userPrivilegeRepository.findAll()));
	}

	@GetMapping("{id}")
	public @ResponseBody
	ResponseEntity<?> findById(@PathVariable Long id) {
		return userPrivilegeRepository.findById(id)
			.map(resourceAssembler::toModel)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

}
