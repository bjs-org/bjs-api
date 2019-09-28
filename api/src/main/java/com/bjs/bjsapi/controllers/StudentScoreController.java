package com.bjs.bjsapi.controllers;

import com.bjs.bjsapi.database.model.SportResult;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.model.enums.DisciplineType;
import com.bjs.bjsapi.database.repository.SportResultRepository;
import com.bjs.bjsapi.database.repository.StudentRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/students/{id}")
public class StudentScoreController {
    private StudentRepository studentRepository;
    private SportResultRepository sportResultRepository;

    public StudentScoreController(StudentRepository studentRepository, SportResultRepository sportResultRepository) {
        this.studentRepository = studentRepository;
        this.sportResultRepository = sportResultRepository;

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
                    double a = 3.64800;
                    double c = 0.00660;
                    double scoreRun = d/(m+z)-a;
                    scoreResult = scoreResult + (int) Math.floor(scoreRun);
                }
                else {
                    double m = Math.sqrt(sportResult.getResult());
                    double a = 0.88070;
                    double c = 0.00068;
                    double scoreOther = (m-a)/c;
                    scoreResult= scoreResult + (int) Math.floor(scoreOther);
                    }
                }
            }
        return null ;
        }

    }


