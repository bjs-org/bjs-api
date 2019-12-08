package com.bjs.bjsapi.controllers;

import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.repository.ClassRepository;
import com.bjs.bjsapi.database.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/best_students")
public class TopStudentsController {
    private final StudentRestController studentRestController;
    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;

    public TopStudentsController(StudentRestController studentRestController, StudentRepository studentRepository, ClassRepository classRepository) {
        this.studentRestController = studentRestController;
        this.studentRepository = studentRepository;
        this.classRepository = classRepository;
    }

    @GetMapping("/{grade}")
    public ResponseEntity<?> topStudentsMapping(@PathVariable String grade, @RequestParam Boolean female) {
        if (female == null) {
            // TODO
        } else {
            return ResponseEntity.ok(topStudents(grade, female));
        }
        return ResponseEntity.badRequest().build();
    }


    public List<Student> topStudents(String grade, boolean female) {
        return classRepository.findByGrade(grade)
                .stream()
                .map(studentRepository::findAllBySchoolClass)
                .flatMap(Collection::stream)
                .filter(student -> genderMatchers(student, female))
                .sorted(Collections.reverseOrder(Comparator.comparingInt(studentRestController::calculateScore)))
                .limit(3)
                .collect(Collectors.toList());
    }

    private boolean genderMatchers(Student student, boolean female) {
        return student.getFemale() == female;
    }
}
