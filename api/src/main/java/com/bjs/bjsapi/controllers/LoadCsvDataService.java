package com.bjs.bjsapi.controllers;

import static java.util.stream.Collectors.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

	private static final String GRADE_CLASSNAME_REGEX = "^(?<grade>\\d+)(?<className>\\w)$";
	private static final String DEFAULT_FORMAT = "<lastname>;<firstname>;<grade><className>;<birthday>;<gender(m or w)>";

	private final StudentRepository studentRepository;
	private final ClassRepository classRepository;

	public LoadCsvDataService(StudentRepository studentRepository, ClassRepository classRepository) {
		this.studentRepository = studentRepository;
		this.classRepository = classRepository;
	}

	public List<Class> loadCsv(List<String> lines) {
		if (lines.size() > 0) {
			validateFormat(lines.remove(0));

			return lines.stream()
				.map(this::splitLine)
				.collect(Collectors.groupingBy(this::parseClass, Collectors.mapping(this::parseStudent, toList())))
				.entrySet()
				.stream()
				.peek(classListEntry -> classListEntry.getValue().forEach(student -> student.setSchoolClass(classListEntry.getKey())))
				.peek(classListEntry -> classListEntry.setValue(classListEntry.getValue().stream().distinct().collect(toList())))
				.peek(classListEntry -> classListEntry.getKey().setStudents(classListEntry.getValue()))
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	public List<Class> loadAndSaveCsv(List<String> lines) {
		final List<Class> classStudentMap = loadCsv(lines);
		save(classStudentMap);
		return classStudentMap;
	}

	private void save(List<Class> classes) {
		classes
			.stream()
			.peek(classRepository::save)
			.map(Class::getStudents)
			.flatMap(Collection::stream)
			.forEach(studentRepository::save);
	}

	private List<String> splitLine(String line) {
		return Arrays.stream(line.split(";"))
			.map(s -> s.replaceAll("\"", ""))
			.collect(Collectors.toList());
	}

	private void validateFormat(String header) throws IllegalArgumentException {
		final List<String> columns = splitLine(header);

		if (columns.size() != 5)
			throw new IllegalArgumentException(String.format("Could not parse format, expected 5 columns in the format \"%s\" but got %d", DEFAULT_FORMAT, columns.size()));
	}

	private Class parseClass(List<String> line) {
		final String classInformation = line.get(2);

		final Pattern pattern = Pattern.compile(GRADE_CLASSNAME_REGEX);
		final Matcher matcher = pattern.matcher(classInformation);
		if (matcher.matches()) {
			return new ClassBuilder()
				.setGrade(matcher.group("grade"))
				.setClassName(matcher.group("className"))
				.createClass();
		} else {
			return new ClassBuilder()
				.setGrade(classInformation)
				.setClassName("")
				.createClass();
		}
	}

	private Student parseStudent(List<String> line) throws IllegalArgumentException {
		try {
			return new StudentBuilder()
				.setFirstName(line.get(1))
				.setLastName(line.get(0))
				.setBirthDay(Date.valueOf(LocalDate.parse(line.get(3), DateTimeFormatter.ofPattern("dd.MM.uuuu"))))
				.setFemale(line.get(4).equals("w"))
				.createStudent();
		} catch (Exception e) {
			throw new IllegalArgumentException(String.format("Could not parse line \"%s\", it does not matches the format \"%s\"", line.toString(), DEFAULT_FORMAT));
		}
	}

}
