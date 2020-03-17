package com.bjs.bjsapi.controllers;

import java.util.Optional;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.repository.SportResultRepository;
import com.bjs.bjsapi.database.repository.StudentRepository;

@RepositoryRestController
@RequestMapping("/students")
public class StudentRepositoryRestController {

	private final StudentRepository studentRepository;
	private final SportResultRepository sportResultRepository;

	public StudentRepositoryRestController(StudentRepository studentRepository, SportResultRepository sportResultRepository) {
		this.studentRepository = studentRepository;
		this.sportResultRepository = sportResultRepository;
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
		final Optional<Student> optionalStudent = studentRepository.findById(id);
		if (optionalStudent.isPresent()) {
			final Student student = optionalStudent.get();

			student.getSportResults().forEach(sportResultRepository::delete);
			studentRepository.delete(student);

			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}

}
