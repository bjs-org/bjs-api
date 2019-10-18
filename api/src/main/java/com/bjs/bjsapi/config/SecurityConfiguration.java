package com.bjs.bjsapi.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;

import com.bjs.bjsapi.security.BJSUserDetailsService;
import com.bjs.bjsapi.security.PermissionEvaluatorManager;
import com.bjs.bjsapi.security.TargetedPermissionEvaluator;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private final BJSUserDetailsService userDetailsService;
	private final List<TargetedPermissionEvaluator> targetedPermissionEvaluatorList;

	public SecurityConfiguration(BJSUserDetailsService userDetailsService, List<TargetedPermissionEvaluator> targetedPermissionEvaluatorList) {
		this.userDetailsService = userDetailsService;
		this.targetedPermissionEvaluatorList = targetedPermissionEvaluatorList;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Bean
	public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
		return new SecurityEvaluationContextExtension();
	}

	@Bean
	public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
		DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
		handler.setPermissionEvaluator(permissionEvaluator());
		return handler;
	}

	@Bean
	@Primary
	public PermissionEvaluator permissionEvaluator() {
		Map<String, PermissionEvaluator> map = new HashMap<>();

		for (TargetedPermissionEvaluator permissionEvaluator : targetedPermissionEvaluatorList) {
			map.put(permissionEvaluator.getTargetType(), permissionEvaluator);
		}

		return new PermissionEvaluatorManager(map);
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
