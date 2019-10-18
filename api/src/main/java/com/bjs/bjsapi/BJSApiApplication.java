package com.bjs.bjsapi;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bjs.bjsapi.config.CalculationInformationConfig;
import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.repository.UserRepository;

@SpringBootApplication
@EnableConfigurationProperties(CalculationInformationConfig.class)
public class BJSApiApplication {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public BJSApiApplication(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public @PostConstruct
	void init() {
		if (!userRepository.findByUsername("admin").isPresent()) {
			User admin = new User("admin");
			admin.setAdministrator(true);
			admin.setPassword(passwordEncoder.encode("admin"));
			userRepository.save(admin);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(BJSApiApplication.class, args);
	}

}
