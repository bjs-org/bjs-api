package com.bjs.bjsapi.database;

import static com.bjs.bjsapi.security.helper.RunWithAuthentication.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.repository.UserRepository;

@Service
public class Initializer implements CommandLineRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public Initializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {
		runAsAdmin(() -> {
			if (!userRepository.findByUsername("admin").isPresent()) {
				User admin = User.builder().username("admin").build();
				admin.setAdministrator(true);
				admin.setPassword(passwordEncoder.encode("admin"));
				userRepository.save(admin);
			}
		});
	}

}
