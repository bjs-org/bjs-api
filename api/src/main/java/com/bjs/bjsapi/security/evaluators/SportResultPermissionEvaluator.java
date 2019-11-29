package com.bjs.bjsapi.security.evaluators;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.bjs.bjsapi.database.model.SportResult;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.repository.SportResultRepository;

@Service
public class SportResultPermissionEvaluator {

	private final StudentPermissionEvaluator studentPermissionEvaluator;
	private final SportResultRepository sportResultRepository;

	public SportResultPermissionEvaluator(StudentPermissionEvaluator studentPermissionEvaluator, SportResultRepository sportResultRepository) {
		this.studentPermissionEvaluator = studentPermissionEvaluator;
		this.sportResultRepository = sportResultRepository;
	}

	public boolean hasPermission(Authentication authentication, SportResult sportResult, String permission) {
		if (authentication == null || permission == null || sportResult == null) {
			return false;
		} else {
			Student student = sportResult.getStudent();
			return studentPermissionEvaluator.hasPermission(authentication, student, permission);
		}
	}

}
