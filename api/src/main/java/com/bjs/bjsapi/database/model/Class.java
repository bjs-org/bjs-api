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

	@Column(nullable = false, unique = true)
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

	public Class() {
	}

	public Class(String className) {
		this.className = className;
	}

	public Class(String className, String classTeacherName) {
		this.className = className;
		this.classTeacherName = classTeacherName;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String toString() {
		return String.format("Class{id=%d, className='%s', classTeacherName='%s'}", id, className, classTeacherName);
	}

}
