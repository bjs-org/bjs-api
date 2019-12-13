package com.bjs.bjsapi.database.model;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import com.bjs.bjsapi.database.model.enums.StudentPaper;

@Projection(name = "calculation", types = Student.class)
public interface StudentCalculationView {

	@Value("#{@studentCalculationService.calculateScore(target)}")
	Integer getScore();

	@Value("#{@studentCalculationService.classifyScore(target)}")
	StudentPaper getClassification();

	Date getBirthDay();

	String getFirstName();

	String getLastName();

	Boolean getFemale();

}
