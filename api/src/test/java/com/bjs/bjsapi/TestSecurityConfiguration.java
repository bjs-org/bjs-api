package com.bjs.bjsapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.bjs.bjsapi.database.repository.UserRepository;
import com.bjs.bjsapi.security.BJSUserDetailsService;

@TestConfiguration
public class TestSecurityConfiguration {

	@Autowired
	public UserRepository userRepository;

	@Bean
	public BJSUserDetailsService bjsUserDetailsService() {
		return new BJSUserDetailsService(userRepository);
	}

}
