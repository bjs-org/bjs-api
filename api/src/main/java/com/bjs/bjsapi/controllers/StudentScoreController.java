package com.bjs.bjsapi.controllers;

import com.bjs.bjsapi.database.model.SportResult;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.repository.SportResultRepository;
import com.bjs.bjsapi.database.repository.StudentRepository;
import com.bjs.bjsapi.helper.CalculationInformationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Date;

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

    public int studentAge(Student student) {
        Date actual = new Date();
        int studentAge = (int) student.getBirthDay().getYear();
        int actualYear = (int) actual.getYear();
        studentAge = actualYear - studentAge + 1900;
        return studentAge;
    }

    public String classification(Student student) {
        int score = calculateScore(student);
        if (student.getFemale() == true) {
            int victoryScore = 475;
            int honorScore = 625;
            for (int i = 8; i <= 18; i++) {
                if (i == studentAge(student)) {
                    if (score >= honorScore) {
                        return "Ehrenurkunde";
                    } else if (score >= victoryScore) {
                        return "Siegerurkunde";
                    } else {
                        return "Teilnehmerurkunde";
                    }
                } else if (studentAge(student) >= 18) {
                    if (score >= 1150) {
                        return "Ehrenurkunde";
                    } else if (score >= 950) {
                        return "Siegerurkunde";
                    } else {
                        return "Teilnehmerurkunde";
                    }
                }
                if (i == 12) {
                    victoryScore = victoryScore + 50;
                    honorScore = honorScore + 50;
                } else if (i >= 13 && i < 18) {
                    victoryScore = victoryScore + 25;
                    honorScore = honorScore + 25;
                } else {
                    victoryScore = victoryScore + 75;
                    honorScore = honorScore + 75;
                }
            }
        }
        if (student.getFemale() == false) {
            int victoryScore = 450;
            int honorScore = 575;
            for (int i = 8; i <= 18; i++) {
                if (i == studentAge(student)) {
                    if (score >= honorScore) {
                        return "Ehrenurkunde";
                    } else if (score >= victoryScore) {
                        return "Siegerurkunde";
                    } else {
                        return "Teilnehmerurkunde";
                    }
                } else if (studentAge(student) >= 18) {
                    if (score >= 1550) {
                        return "Ehrenurkunde";
                    } else if (score >= 1275) {
                        return "Siegerurkunde";
                    } else {
                        return "Teilnehmerurkunde";
                    }
                }
                if (i <= 15 && i != 13) {
                    victoryScore = victoryScore + 75;
                    honorScore = honorScore + 100;
                } else if (i == 13 || i >= 16) {
                    victoryScore = victoryScore + 75;
                    honorScore = honorScore + 75;
                }
            }

        }
        return null;
    }


}

