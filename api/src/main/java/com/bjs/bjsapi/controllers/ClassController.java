package com.bjs.bjsapi.controllers;

import static org.springframework.http.ResponseEntity.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.repository.ClassRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2/classes")
public class ClassController {

	private final ClassRepository classRepository;

	public ClassController(ClassRepository classRepository) {
		this.classRepository = classRepository;
	}

	@GetMapping("")
	public ResponseEntity<?> getSchoolClasses() {
		final List<Class> all = classRepository.findAll();
		return ok(new ClassesResponse(all));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getSchoolClass(@PathVariable Long id) {
		return classRepository
			.findById(id)
			.map(ResponseEntity::ok)
			.orElse(notFound()
				.build());
	}

	@Data
	@RequiredArgsConstructor
	@AllArgsConstructor
	public static class ClassesResponse {

		public List<Class> classes;

	}

}


