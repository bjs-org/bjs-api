package com.bjs.bjsapi.security;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.repository.UserRepository;

@RepositoryEventHandler
public class UserRepositoryEventHandler {

	@PersistenceContext
	private EntityManager entityManager;

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserRepositoryEventHandler(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@HandleBeforeCreate
	protected void onBeforeCreate(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
	}

	@HandleBeforeSave
	protected void onBeforeSave(User user) {

		if (entityManager != null) {
			entityManager.detach(user);
		}


		Optional<User> optionalUserInDB = userRepository.findById(user.getId());

		if (optionalUserInDB.isPresent()) {
			// Only change user
			User userInDB = optionalUserInDB.get();

			if (user.getPassword() != null && !userInDB.getPassword().equals(user.getPassword())) {
				user.setPassword(passwordEncoder.encode(user.getPassword()));
			}
		}
	}

}
