package com.bjs.bjsapi.controllers;

import java.time.Clock;
import java.util.Collections;

import org.springframework.stereotype.Service;

import com.bjs.bjsapi.database.model.SportResult;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.model.enums.DisciplineType;
import com.bjs.bjsapi.database.model.enums.StudentPaper;
import com.bjs.bjsapi.helper.CalculationInformationService;
import com.bjs.bjsapi.helper.ClassificationInformationService;

@Service
public class StudentCalculationService {

	private final CalculationInformationService calculationInformationService;
	private final ClassificationInformationService classificationInformationService;
	private final Clock clock;

	public StudentCalculationService(CalculationInformationService calculationInformationService, ClassificationInformationService classificationInformationService, Clock clock) {
		this.calculationInformationService = calculationInformationService;
		this.classificationInformationService = classificationInformationService;
		this.clock = clock;
	}

	public Integer calculateScore(Student student) {
		return student.getSportResults()
			.stream()
			.map(sportResult -> calculatePoints(sportResult, student.getFemale()))
			.sorted(Collections.reverseOrder())
			.mapToInt(value -> value)
			.limit(3)
			.sum();
	}

	public StudentPaper classifyScore(Student student) {
		int score = calculateScore(student);

		int honor = classificationInformationService.getHonorValue(student.getFemale(), student.getAgeByYear(clock));
		int victory = classificationInformationService.getVictoryValue(student.getFemale(), student.getAgeByYear(clock));

		if (score >= honor) {
			return StudentPaper.HONOR;
		} else if (score >= victory) {
			return StudentPaper.VICTORY;
		} else {
			return StudentPaper.PARTICIPANT;
		}
	}

	private int calculatePoints(SportResult sportResult, Boolean female) {
		if (sportResult.getDiscipline().isRUN()) {
			return calculateRunningPoints(sportResult.getResult(), sportResult.getDiscipline(), female);
		} else {
			return calculateNotRunningPoints(sportResult.getResult(), sportResult.getDiscipline(), female);
		}
	}

	private int calculateNotRunningPoints(Float measurement, DisciplineType discipline, Boolean female) {
		// P = ( √M - a ) / c
		final double a = calculationInformationService.getAValue(female, discipline);
		final double c = calculationInformationService.getCValue(female, discipline);

		final double points = (Math.sqrt(measurement) - a) / c;
		return (int) Math.floor(points);
	}

	private int calculateRunningPoints(Float measurement, DisciplineType discipline, Boolean female) {
		// P = ( D : (M + Z) ) - a) / c
		final double extra = calculateExtra(discipline.getDistance());

		final double a = calculationInformationService.getAValue(female, discipline);
		final double c = calculationInformationService.getCValue(female, discipline);

		final double points = ((discipline.getDistance() / (measurement + extra)) - a) / c;
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