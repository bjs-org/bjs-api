package com.bjs.bjsapi.security;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.repository.UserRepository;

@Component
public class UserRepositoryEventHandler extends AbstractRepositoryEventListener<User> {

	@PersistenceContext
	private EntityManager entityManager;

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserRepositoryEventHandler(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	protected void onBeforeCreate(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
	}

	@Override
	protected void onBeforeSave(User user) {

		if (entityManager != null) {
			entityManager.detach(user);
		}

		Optional<User> optionalUserInDB = userRepository.findById(user.getId());

		if (optionalUserInDB.isPresent()) {
			// Only change user
			User userInDB = optionalUserInDB.get();

			if (user.getPassword() != null && !userInDB.getPassword().equals(user.getPassword()) && !passwordEncoder.matches(user.getPassword(), userInDB.getPassword())) {
				user.setPassword(passwordEncoder.encode(user.getPassword()));
			}
		}
	}

}
