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
		Optional<Student> optionalStudent = studentRepository.findById(id);
		if (optionalStudent.isPresent()) {
			int scoreResult = 0;
			Student student = optionalStudent.get();
			List<SportResult> sportResults = sportResultRepository.findByStudent(student);
			for (SportResult sportResult : sportResults) {
				if (sportResult.getDiscipline().isRUN()) {
					double d = sportResult.getDiscipline().getDistance();
					double m = sportResult.getResult();
					double z = 0.0;
					double a = calculationInformationService.getAValue(student.getFemale(), sportResult.getDiscipline());
					double c = calculationInformationService.getCValue(student.getFemale(), sportResult.getDiscipline());
					double scoreRun = d / (m + z) - a;
					scoreResult = scoreResult + (int) Math.floor(scoreRun);
				} else {
					double m = Math.sqrt(sportResult.getResult());
					double a = calculationInformationService.getAValue(student.getFemale(), sportResult.getDiscipline());
					double c = calculationInformationService.getCValue(student.getFemale(), sportResult.getDiscipline());
					double scoreOther = (m - a) / c;
					scoreResult = scoreResult + (int) Math.floor(scoreOther);
				}
			}
		}

		return null;
	}

}


