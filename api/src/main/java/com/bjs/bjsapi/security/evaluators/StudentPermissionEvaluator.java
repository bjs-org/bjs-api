package com.bjs.bjsapi.security.evaluators;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.Student;

@Service
public class StudentPermissionEvaluator {

	private final ClassPermissionEvaluator classPermissionEvaluator;

	public StudentPermissionEvaluator(ClassPermissionEvaluator classPermissionEvaluator) {
		this.classPermissionEvaluator = classPermissionEvaluator;
	}

	public boolean hasPermission(Authentication authentication, Student student, String permission) {
		if (authentication == null || permission == null || student == null) {
			return false;
		} else {
			Class schoolClass = student.getSchoolClass();
			return classPermissionEvaluator.hasPermission(authentication, schoolClass, permission);
		}
	}

}
