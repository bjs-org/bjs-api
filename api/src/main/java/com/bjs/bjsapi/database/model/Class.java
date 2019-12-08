package com.bjs.bjsapi.database.model;

import javax.persistence.*;

@Entity
@Table(name = "classes")
public class Class {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private String grade;

	@Column(nullable = false)
	private String className;

	private String classTeacherName;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassTeacherName() {
		return classTeacherName;
	}

	public void setClassTeacherName(String classTeacherName) {
		this.classTeacherName = classTeacherName;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public Class() {
	}

	public Class(String className) {
		this.className = className;
	}

	public Class(String grade, String className, String classTeacherName) {
		this.grade = grade;
		this.className = className;
		this.classTeacherName = classTeacherName;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String toString() {
		return String.format("Class{id=%d, grade='%s', className='%s', classTeacherName='%s'}", id, grade, className, classTeacherName);
	}
}
