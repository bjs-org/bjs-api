package com.bjs.bjsapi.database.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.rest.core.annotation.RestResource;

import com.bjs.bjsapi.database.model.enums.DisciplineType;

@Entity
@Table("sport_results")
public class SportResult {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToOne
	@JoinColumn(nullable = false)
	@RestResource
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

	@Override
	public String toString() {
		return String.format("SportResult{id=%d, student=%s, result=%s, discipline=%s}", id, student, result, discipline);
	}

}
