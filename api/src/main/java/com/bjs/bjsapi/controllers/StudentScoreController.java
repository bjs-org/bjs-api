package com.bjs.bjsapi.controllers;

import java.util.Collections;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bjs.bjsapi.database.model.SportResult;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.repository.SportResultRepository;
import com.bjs.bjsapi.database.repository.StudentRepository;
import com.bjs.bjsapi.helper.CalculationInformationService;

@RestController
@RequestMapping("/api/v1/students/{id}")
public class StudentScoreController {

	private final StudentRepository studentRepository;
	private final SportResultRepository sportResultRepository;
	private final CalculationInformationService calculationInformationService;

	public StudentScoreController(StudentRepository studentRepository, SportResultRepository sportResultRepository, CalculationInformationService calculationInformationService) {
		this.studentRepository = studentRepository;
		this.sportResultRepository = sportResultRepository;
		this.calculationInformationService = calculationInformationService;
	}

	@GetMapping("/score")
	public ResponseEntity<?> returnScore(@PathVariable("id") Long id) {
		return studentRepository.findById(id)
			.map(this::calculateScore)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	Integer calculateScore(Student student) {
		return sportResultRepository.findByStudent(student)
			.stream()
			.map(sportResult -> calculatePoints(sportResult, student.getFemale()))
			.sorted(Collections.reverseOrder())
			.mapToInt(value -> value)
			.limit(3)
			.sum();
	}

	private int calculatePoints(SportResult sportResult, Boolean female) {
		if (sportResult.getDiscipline().isRUN()) {
			return calculateRunningPoints(sportResult, female);
		} else {
			return calculateNotRunningPoints(sportResult, female);
		}
	}

	private int calculateNotRunningPoints(SportResult sportResult, Boolean female) {
		// P = ( √M - a ) / c

		final double measurement = sportResult.getResult();

		final double a = calculationInformationService.getAValue(female, sportResult.getDiscipline());
		final double c = calculationInformationService.getCValue(female, sportResult.getDiscipline());

		final double points = (Math.sqrt(measurement) - a) / c;
		return (int) Math.floor(points);
	}

	private int calculateRunningPoints(SportResult sportResult, Boolean female) {
		// P = ( D : (M + Z) ) - a) / c

		final double distance = sportResult.getDiscipline().getDistance();
		final double measurement = sportResult.getResult();

		final double extra = calculateExtra(sportResult.getDiscipline().getDistance());

		final double a = calculationInformationService.getAValue(female, sportResult.getDiscipline());
		final double c = calculationInformationService.getCValue(female, sportResult.getDiscipline());

		final double points = ((distance / (measurement + extra)) - a) / c;
		return (int) Math.floor(points);
	}

	private double calculateExtra(int distance) {
		double extra = 0.0;
		if (distance <= 300) {
			extra = 0.24;
		} else if (distance <= 400) {
			extra = 0.14;
		}

		return extra;
	}

}