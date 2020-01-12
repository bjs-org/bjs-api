package com.bjs.bjsapi.database.model.helper;

import com.bjs.bjsapi.database.model.Class;

public class ClassBuilder {

	private String className;
	private String classTeacherName;

	public ClassBuilder setClassName(String className) {
		this.className = className;
		return this;
	}

	public ClassBuilder setClassTeacherName(String classTeacherName) {
		this.classTeacherName = classTeacherName;
		return this;
	}

	public Class createClass() {
		return new Class(className, classTeacherName);
	}

}