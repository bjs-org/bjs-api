package com.bjs.bjsapi.helper;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

public class SecurityHelper {

	public static void runAs(String username, String password, String... roles) {
		Assert.notNull(username, "Username should not be null");
		Assert.notNull(password, "Password should not be null");

		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, password, AuthorityUtils.createAuthorityList(roles)));
	}

	public static void reset() {
		SecurityContextHolder.clearContext();
	}

}
