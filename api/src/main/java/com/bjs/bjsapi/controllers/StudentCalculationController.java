package com.bjs.bjsapi.controllers;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bjs.bjsapi.database.repository.StudentRepository;

@RestController
@RequestMapping("/api/v1/students/{id}")
public class StudentCalculationController {

	private final StudentCalculationService studentCalculationService;
	private final StudentRepository studentRepository;

	public StudentCalculationController(StudentCalculationService studentCalculationService, StudentRepository studentRepository) {
		this.studentCalculationService = studentCalculationService;
		this.studentRepository = studentRepository;
	}

	@GetMapping("/score")
	public ResponseEntity<?> score(@PathVariable("id") Long id) {
		return studentRepository.findById(id)
			.map(studentCalculationService::calculateScore)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/classification")
	public ResponseEntity<?> classification(@PathVariable("id") Long id) {
		return studentRepository.findById(id)
			.map(studentCalculationService::classifyScore)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

}
