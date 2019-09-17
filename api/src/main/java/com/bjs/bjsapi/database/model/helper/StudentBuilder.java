package com.bjs.bjsapi.database.model.helper;

import java.util.Date;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.Student;

public class StudentBuilder {

	private String firstName;
	private String lastName;
	private Date birthDay;
	private Class schoolClass;
	private Boolean female;

	public StudentBuilder setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public StudentBuilder setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public StudentBuilder setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
		return this;
	}

	public StudentBuilder setSchoolClass(Class schoolClass) {
		this.schoolClass = schoolClass;
		return this;
	}

	public StudentBuilder setFemale(Boolean female) {
		this.female = female;
		return this;
	}

	public Student createStudent() {
		return new Student(firstName, lastName, birthDay, schoolClass, female);
	}

}