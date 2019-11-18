package com.bjs.bjsapi.security.helper;

import java.util.function.Supplier;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class RunWithAuthentication {

	public static <T> T runAs(Authentication authentication, Supplier<T> supplier) {
		Authentication originalAuthentication = SecurityContextHolder.getContext().getAuthentication();
		SecurityContextHolder.getContext().setAuthentication(authentication);

		T returnValue = supplier.get();

		SecurityContextHolder.getContext().setAuthentication(originalAuthentication);
		return returnValue;
	}

	public static void runAs(Authentication authentication, Runnable runnable) {
		Authentication originalAuthentication = SecurityContextHolder.getContext().getAuthentication();
		SecurityContextHolder.getContext().setAuthentication(authentication);

		runnable.run();

		SecurityContextHolder.getContext().setAuthentication(originalAuthentication);
	}

}