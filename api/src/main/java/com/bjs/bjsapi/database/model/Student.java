package com.bjs.bjsapi.database.model;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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

import com.fasterxml.jackson.annotation.JsonBackReference;

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
	@JoinColumn(nullable = false)
	@JsonBackReference
	private Class schoolClass;

	@OneToMany(mappedBy = "student")
	private List<SportResult> sportResults;

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

	public List<SportResult> getSportResults() {
		return sportResults;
	}

	public void setSportResults(List<SportResult> sportResults) {
		this.sportResults = sportResults;
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

	public Student(Long id, String firstName, String lastName, Date birthDay, Class schoolClass, Boolean female) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDay = birthDay;
		this.schoolClass = schoolClass;
		this.female = female;
	}

	@Override
	public String toString() {
		return String.format("Student{id=%d, firstName='%s', lastName='%s', birthDay=%s, female=%s, schoolClass=%s}", id, firstName, lastName, birthDay, female, schoolClass);
	}

	public int getAgeByYear(Clock clock) {
		final int birthYear = Instant.ofEpochMilli(getBirthDay().getTime()).atZone(ZoneId.systemDefault()).toLocalDate().getYear();
		final int currentYear = Instant.now(clock).atZone(ZoneId.systemDefault()).toLocalDate().getYear();
		return Math.abs(birthYear - currentYear);
	}

}
