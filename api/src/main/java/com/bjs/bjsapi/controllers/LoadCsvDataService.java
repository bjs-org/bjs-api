package com.bjs.bjsapi.controllers;

import java.util.List;

import org.springframework.stereotype.Service;

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

	public void loadCsv(List<String> lines) {
	}

}
