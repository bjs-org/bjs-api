package com.bjs.bjsapi.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.SportResult;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.model.enums.DisciplineType;
import com.bjs.bjsapi.database.repository.ClassRepository;

@Service
public class ExportClassService {

	private final ClassRepository classRepository;
	private final StudentCalculationService studentCalculationService;

	public ExportClassService(ClassRepository classRepository, StudentCalculationService studentCalculationService) {
		this.classRepository = classRepository;
		this.studentCalculationService = studentCalculationService;
	}

	public List<String> exportClass(Long id) {
		final Optional<Class> optionalClass = classRepository.findById(id);
		if (optionalClass.isPresent()) {
			List<String> result = new ArrayList<>();
			final Class schoolClass = optionalClass.get();

			result.addAll(createHeader(schoolClass));
			result.addAll(createTable(schoolClass));

			return result;
		}
		return Collections.singletonList("");
	}

	private List<String> createHeader(Class schoolClass) {
		return Arrays.asList("", "");
	}

	private List<String> createTable(Class schoolClass) {
		final List<String> result = new ArrayList<>();
		List<DisciplineType> disciplines = parseDiscipline(schoolClass);

		result.add(createTableHeader(disciplines));
		result.addAll(schoolClass
			.getStudents()
			.stream()
			.map(student -> toLine(student, disciplines))
			.collect(Collectors.toList()));

		return result;
	}

	private String toLine(Student student, List<DisciplineType> disciplines) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(String.format("%s %s;%s;%d;%s;", student.getFirstName(), student.getLastName(), student.getFemale() ? "w" : "m", studentCalculationService.calculateScore(student), studentCalculationService.classifyScore(student).toString()));

		disciplines.forEach(disciplineType -> {
			stringBuilder.append(student.getSportResults().stream().filter(sportResult -> sportResult.getDiscipline() == disciplineType).findAny()
				.map(SportResult::getResult)
				.map(Object::toString)
				.map(s -> s.replaceAll("\\.", ","))
				.orElse(""));

			stringBuilder.append(";");
		});

		return stringBuilder.toString();
	}

	private List<DisciplineType> parseDiscipline(Class schoolClass) {
		return schoolClass.getStudents()
			.stream()
			.flatMap(student -> student.getSportResults().stream().map(SportResult::getDiscipline))
			.distinct()
			.sorted()
			.collect(Collectors.toList());
	}

	private String createTableHeader(List<DisciplineType> disciplineTypes) {
		return "";
	}

}
