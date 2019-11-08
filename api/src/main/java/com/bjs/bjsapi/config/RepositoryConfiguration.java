package com.bjs.bjsapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bjs.bjsapi.database.repository.UserRepository;
import com.bjs.bjsapi.security.UserRepositoryEventHandler;

@Configuration
public class RepositoryConfiguration {

	@Bean
	public UserRepositoryEventHandler userRepositoryEventHandler(PasswordEncoder passwordEncoder, UserRepository userRepository) {
		return new UserRepositoryEventHandler(userRepository, passwordEncoder);
	}

}
