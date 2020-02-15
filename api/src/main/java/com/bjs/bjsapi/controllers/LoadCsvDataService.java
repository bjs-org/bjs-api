package com.bjs.bjsapi.controllers;

import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedReader;

import com.bjs.bjsapi.database.model.helper.StudentBuilder;
import com.sun.xml.internal.ws.addressing.WsaTubeHelper;
import org.springframework.stereotype.Service;

import com.bjs.bjsapi.database.repository.ClassRepository;
import com.bjs.bjsapi.database.repository.StudentRepository;

@Service
public class LoadCsvDataService {

	private final StudentRepository studentRepository;
	private final ClassRepository classRepository;

	private final BufferedReader bufferedReader;
	private final FileReader fileReader;
	private final StudentBuilder studentBuilder;

	public LoadCsvDataService(StudentBuilder studentBuilder, StudentRepository studentRepository, ClassRepository classRepository, BufferedReader bufferedReader, FileReader fileReader) {
		this.studentRepository = studentRepository;
		this.classRepository = classRepository;
		this.bufferedReader = bufferedReader;
		this.fileReader = fileReader;
		this.studentBuilder = studentBuilder;
	}

	public void loadCsv(List<String> lines) {
		List<String> csvData = lines;
		int i = 1;
		while (!csvData.get(i).isEmpty()){
			String[] csvLine = csvData.get(i).split(";");
			createStudent(csvLine);
			i = i+1;
		}

	}

	private void createStudent(String[] studentData){
		studentBuilder.setFirstName(studentData[0]);
		studentBuilder.setLastName(studentData[1]);
		studentBuilder.setBirthDay(studentData[2]);
		studentBuilder.setFirstName(studentData[3]);
		studentBuilder.createStudent();
	}
}
