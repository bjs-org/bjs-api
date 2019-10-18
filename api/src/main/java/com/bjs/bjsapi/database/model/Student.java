package com.bjs.bjsapi.database.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.rest.core.annotation.RestResource;

@Entity
@Table(name = "students")
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private String firstName;

	@Column(nullable = false)
	private String lastName;

	@Temporal(TemporalType.DATE)
	@Column(nullable = false)
	private Date birthDay;

	@Column(nullable = false)
	private Boolean female;

	@ManyToOne
	@RestResource(path = "class", rel = "class")
	@JoinColumn(nullable = false)
	private Class schoolClass;

	public Boolean getFemale() {
		return female;
	}

	public void setFemale(Boolean female) {
		this.female = female;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}

	public Class getSchoolClass() {
		return schoolClass;
	}

	public void setSchoolClass(Class schoolClass) {
		this.schoolClass = schoolClass;
	}

	public Long getId() {
		return id;
	}

	public Student() {
	}

	public Student(String firstName, String lastName, Date birthDay, Class schoolClass, Boolean female) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDay = birthDay;
		this.schoolClass = schoolClass;
		this.female = female;
	}



}
