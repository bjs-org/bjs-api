package com.bjs.bjsapi.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/students/{id}")
public class StudentScoreController {

	@GetMapping("/score")
	public Integer calculateScore(@PathVariable("id") Long id) {
		return Math.toIntExact(id);
	}

}
