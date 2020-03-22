package com.bjs.bjsapi.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.model.UserPrivilege;
import com.bjs.bjsapi.database.repository.UserPrivilegeRepository;
import com.bjs.bjsapi.database.repository.UserRepository;

@RepositoryRestController
@RequestMapping("/users")
public class UserRepositoryRestController {

	private final UserRepository userRepository;
	private final UserPrivilegeRepository userPrivilegeRepository;

	public UserRepositoryRestController(UserRepository userRepository, UserPrivilegeRepository userPrivilegeRepository) {
		this.userRepository = userRepository;
		this.userPrivilegeRepository = userPrivilegeRepository;
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		final Optional<User> optionalUser = userRepository.findById(id);
		return optionalUser
			.map(user -> {
				final List<UserPrivilege> userPrivileges = user.getUserPrivileges();
				userPrivileges.forEach(userPrivilegeRepository::delete);
				userRepository.delete(user);

				return ResponseEntity.noContent().build();
			})
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

}
