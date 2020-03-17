package com.bjs.bjsapi.controllers;

import java.util.Optional;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.repository.ClassRepository;
import com.bjs.bjsapi.database.repository.UserPrivilegeRepository;

@RepositoryRestController
@RequestMapping("/classes")
public class ClassRepositoryRestController {

	private final ClassRepository classRepository;
	private final UserPrivilegeRepository userPrivilegeRepository;
	private final StudentRepositoryRestController studentRepositoryRestController;

	public ClassRepositoryRestController(ClassRepository classRepository, UserPrivilegeRepository userPrivilegeRepository, StudentRepositoryRestController studentRepositoryRestController) {
		this.classRepository = classRepository;
		this.userPrivilegeRepository = userPrivilegeRepository;
		this.studentRepositoryRestController = studentRepositoryRestController;
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		final Optional<Class> optionalClass = classRepository.findById(id);

		if (optionalClass.isPresent()) {
			final Class schoolClass = optionalClass.get();

			schoolClass.getStudents().stream().map(Student::getId).forEach(studentRepositoryRestController::deleteStudent);
			schoolClass.getPrivileges().forEach(userPrivilegeRepository::delete);

			classRepository.delete(schoolClass);

			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}

}
