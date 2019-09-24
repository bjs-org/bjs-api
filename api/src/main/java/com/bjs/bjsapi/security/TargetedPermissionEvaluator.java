package com.bjs.bjsapi.security;

import org.springframework.security.access.PermissionEvaluator;

public interface TargetedPermissionEvaluator extends PermissionEvaluator {

	String getTargetType();

}
