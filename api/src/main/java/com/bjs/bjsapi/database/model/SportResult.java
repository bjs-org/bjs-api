package com.bjs.bjsapi.database.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.rest.core.annotation.RestResource;

import com.bjs.bjsapi.database.model.enums.DisciplineType;

@Entity
@Table(name = "sport_results")
public class SportResult {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@RestResource
	@JoinColumn
	private Student student;

	@Column(nullable = false)
	private Float result;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DisciplineType discipline;

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Float getResult() {
		return result;
	}

	public void setResult(Float result) {
		this.result = result;
	}

	public DisciplineType getDiscipline() {
		return discipline;
	}

	public void setDiscipline(DisciplineType discipline) {
		this.discipline = discipline;
	}

	public Long getId() {
		return id;
	}

	public SportResult() {
	}

	public SportResult(Student student, Float result, DisciplineType discipline) {
		this.student = student;
		this.result = result;
		this.discipline = discipline;
	}

	@Override
	public String toString() {
		return String.format("SportResult{id=%d, student=%s, result=%s, discipline=%s}", id, student, result, discipline);
	}

}
