package com.bjs.bjsapi.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;

import com.bjs.bjsapi.security.PermissionEvaluatorManager;
import com.bjs.bjsapi.security.TargetedPermissionEvaluator;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

	@Bean
	public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
		return new SecurityEvaluationContextExtension();
	}

	@Bean
	public MethodSecurityExpressionHandler methodSecurityExpressionHandler(PermissionEvaluator permissionEvaluator) {
		DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
		handler.setPermissionEvaluator(permissionEvaluator);
		return handler;
	}

	@Bean
	@Primary
	public PermissionEvaluator permissionEvaluator(List<TargetedPermissionEvaluator> targetedPermissionEvaluatorList) {
		Map<String, PermissionEvaluator> map = new HashMap<>();

		for (TargetedPermissionEvaluator permissionEvaluator : targetedPermissionEvaluatorList) {
			map.put(permissionEvaluator.getTargetType(), permissionEvaluator);
		}

		return new PermissionEvaluatorManager(map);
	}

}
