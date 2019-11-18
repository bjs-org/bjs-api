package com.bjs.bjsapi.security.helper;

import java.util.function.Supplier;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

public class RunWithAuthentication {

	private static Supplier<Authentication> adminAuthenticationSupplier = () -> toAuthentication("admin", "admin", "ROLE_ADMIN", "ROLE_USER");

	public static <T> T runAsAdmin(Supplier<T> supplier) {
		return runWith(adminAuthenticationSupplier.get(), supplier);
	}

	public static <T> T runWith(Authentication authentication, Supplier<T> supplier) {
		Authentication originalAuthentication = SecurityContextHolder.getContext().getAuthentication();
		SecurityContextHolder.getContext().setAuthentication(authentication);

		T returnValue = supplier.get();

		SecurityContextHolder.getContext().setAuthentication(originalAuthentication);
		return returnValue;
	}

	public static void runAsAdmin(Runnable runnable) {
		runWith(adminAuthenticationSupplier.get(), runnable);
	}

	public static void runWith(Authentication authentication, Runnable runnable) {
		Authentication originalAuthentication = SecurityContextHolder.getContext().getAuthentication();
		SecurityContextHolder.getContext().setAuthentication(authentication);

		runnable.run();

		SecurityContextHolder.getContext().setAuthentication(originalAuthentication);
	}

	static Authentication toAuthentication(String username, String password, String... roles) {
		return new UsernamePasswordAuthenticationToken(username, password, AuthorityUtils.createAuthorityList(roles));
	}

}