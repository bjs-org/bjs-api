package com.bjs.bjsapi.database.model.helper;

import com.bjs.bjsapi.database.model.Class;

public class ClassBuilder {

	private String className;
	private String classTeacherName;
	private String grade;

	public ClassBuilder setClassName(String className) {
		this.className = className;
		return this;
	}

	public ClassBuilder setClassTeacherName(String classTeacherName) {
		this.classTeacherName = classTeacherName;
		return this;
	}

	public Class createClass() {
		return new Class(grade, className, classTeacherName);
	}


	public ClassBuilder setGrade(String grade) {
		this.grade = grade;
		return this;
	}
}