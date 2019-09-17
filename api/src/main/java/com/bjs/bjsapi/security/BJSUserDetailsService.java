package com.bjs.bjsapi.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.repository.UserRepository;

@Service
public class BJSUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public BJSUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findByUsername(username);
		if (!user.isPresent()) {
			throw new UsernameNotFoundException(username);
		}
		return new BJSUserPrincipal(user.get());
	}

}
