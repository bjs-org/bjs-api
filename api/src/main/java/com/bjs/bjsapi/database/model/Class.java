package com.bjs.bjsapi.database.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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

	public Long getId() {
		return id;
	}

	public Class() {
	}

	public Class(String grade, String className, String classTeacherName) {
		this.grade = grade;
		this.className = className;
		this.classTeacherName = classTeacherName;
	}

	public Class(String grade, String className, String classTeacherName, Long id) {
		this.grade = grade;
		this.className = className;
		this.classTeacherName = classTeacherName;
		this.id = id;
	}

	@Override
	public String toString() {
		return String.format("Class{id=%d, grade='%s', className='%s', classTeacherName='%s'}", id, grade, className, classTeacherName);
	}

}
