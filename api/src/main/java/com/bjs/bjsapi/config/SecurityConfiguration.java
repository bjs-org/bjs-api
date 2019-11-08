package com.bjs.bjsapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bjs.bjsapi.security.BJSUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private final BJSUserDetailsService userDetailsService;

	public SecurityConfiguration(BJSUserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider(BJSUserDetailsService userDetailsService) {
		final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider(userDetailsService));
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic()
			.and()
			.authorizeRequests()
			.mvcMatchers("/api/v1/**").authenticated()
			.mvcMatchers(HttpMethod.POST, "/api/v1/classes").hasRole("ADMIN")
			.mvcMatchers(HttpMethod.DELETE, "/api/v1/classes/**").hasRole("ADMIN")
			.mvcMatchers(HttpMethod.PATCH, "/api/v1/classes/**").hasRole("ADMIN")
			.and()
			.csrf().disable()
			.sessionManagement().disable()
			.rememberMe().disable();
	}
}
