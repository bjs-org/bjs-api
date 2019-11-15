package com.bjs.bjsapi.controllers;

import java.util.List;
import java.util.Optional;

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
	public Integer calculateScore(@PathVariable("id") Long id) {
		int scoreResult = 0;
		Optional<Student> optionalStudent = studentRepository.findById(id);
		if (optionalStudent.isPresent()) {
			Student student = optionalStudent.get();
			List<SportResult> sportResults = sportResultRepository.findByStudent(student);
			for (SportResult sportResult : sportResults) {
				if (sportResult.getDiscipline().isRUN()) {
					double distance = sportResult.getDiscipline().getDistance();
					double measurement = sportResult.getResult();
					double extra = getExtra(sportResult.getDiscipline().getDistance());
					double a = calculationInformationService.getAValue(student.getFemale(), sportResult.getDiscipline());
					double c = calculationInformationService.getCValue(student.getFemale(), sportResult.getDiscipline());
					double scoreRun = (distance / (measurement + extra) - a) / c;
					scoreResult = scoreResult + (int) Math.floor(scoreRun);
				} else {
					double measurement = Math.sqrt(sportResult.getResult());
					double a = calculationInformationService.getAValue(student.getFemale(), sportResult.getDiscipline());
					double c = calculationInformationService.getCValue(student.getFemale(), sportResult.getDiscipline());
					double scoreOther = (measurement - a) / c;
					scoreResult = scoreResult + (int) Math.floor(scoreOther);
				}
			}

			return scoreResult;
		}

		return null;
	}

	private double getExtra(int distance) {
		double extra = 0.0;
		if(distance <= 300) {
			extra = 0.24;
		}
		else if(distance > 300 && distance <= 400) {
			extra = 0.14;
		}

		return extra;
	}

}