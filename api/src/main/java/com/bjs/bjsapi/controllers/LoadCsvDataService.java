package com.bjs.bjsapi.controllers;

import static java.util.stream.Collectors.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.model.helper.ClassBuilder;
import com.bjs.bjsapi.database.model.helper.StudentBuilder;
import com.bjs.bjsapi.database.repository.ClassRepository;
import com.bjs.bjsapi.database.repository.StudentRepository;

@Service
public class LoadCsvDataService {

	private final StudentRepository studentRepository;
	private final ClassRepository classRepository;

	public LoadCsvDataService(StudentRepository studentRepository, ClassRepository classRepository) {
		this.studentRepository = studentRepository;
		this.classRepository = classRepository;
	}

	private List<String> splitLine(String line) {
		return Arrays.stream(line.split(";"))
			.map(s -> s.replaceAll("\"", ""))
			.collect(Collectors.toList());
	}

	public void loadCsv(List<String> lines) {
		lines.remove(0);

		final List<List<String>> csv = lines.stream()
			.map(this::splitLine)
			.collect(toList());

		csv.stream()
			.map(this::parseClass)
			.distinct()
			.forEach(classRepository::save);

		csv.stream()
			.map(this::parseStudent)
			.distinct()
			.forEach(studentRepository::save);
	}

	private Class parseClass(List<String> line) {
		final String classInformation = line.get(2);
		return new ClassBuilder()
			.setGrade(classInformation.substring(0, 1))
			.setClassName(classInformation.substring(1))
			.createClass();
	}

	private Student parseStudent(List<String> line) {
		return new StudentBuilder()
			.setFirstName(line.get(1))
			.setLastName(line.get(0))
			.setBirthDay(Date.valueOf(LocalDate.parse(line.get(3), DateTimeFormatter.ofPattern("dd.MM.uuuu"))))
			.setFemale(line.get(4).equals("w"))
			.createStudent();
	}

}
