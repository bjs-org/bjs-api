package com.bjs.bjsapi.security;

import org.springframework.data.repository.support.QueryMethodParameterConversionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdvice {

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<String> accessDeniedHttpMessageNoReadableException(Exception e) throws Exception {
		return checkForAccessDenied(e);
	}

	@ExceptionHandler(QueryMethodParameterConversionException.class)
	public ResponseEntity<String> accessDeniedQueryMethodException(Exception e) throws Exception {
		return checkForAccessDenied(e);
	}

	private ResponseEntity<String> checkForAccessDenied(Exception e) throws Exception {
		Throwable cause = e.getCause();
		if (cause != null) {
			Throwable nestedCause = cause.getCause();
			if (AccessDeniedException.class.isAssignableFrom(nestedCause.getClass()) || AccessDeniedException.class.isAssignableFrom(nestedCause.getCause().getClass())) {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
		}
		throw e;
	}
}