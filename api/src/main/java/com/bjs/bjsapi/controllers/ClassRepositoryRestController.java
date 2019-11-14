package com.bjs.bjsapi.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.repository.ClassRepository;
import com.bjs.bjsapi.database.repository.StudentRepository;

@RepositoryRestController
@RequestMapping("/classes")
public class ClassRepositoryRestController {

	private final ClassRepository classRepository;
	private final StudentRepository studentRepository;

	public ClassRepositoryRestController(ClassRepository classRepository, StudentRepository studentRepository) {
		this.classRepository = classRepository;
		this.studentRepository = studentRepository;
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Optional<List<Student>> students = classRepository.findById(id)
			.map(studentRepository::findAllBySchoolClass);

		if (students.isPresent()) {
			students
				.get()
				.forEach(studentRepository::delete);

			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}

}
