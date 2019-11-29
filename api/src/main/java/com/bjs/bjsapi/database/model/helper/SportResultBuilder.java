package com.bjs.bjsapi.database.model.helper;

import com.bjs.bjsapi.database.model.SportResult;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.model.enums.DisciplineType;

public class SportResultBuilder {

	private Student student;
	private Float result;
	private DisciplineType discipline;

	public SportResultBuilder setStudent(Student student) {
		this.student = student;
		return this;
	}

	public SportResultBuilder setResult(Float result) {
		this.result = result;
		return this;
	}

	public SportResultBuilder setDiscipline(DisciplineType discipline) {
		this.discipline = discipline;
		return this;
	}

	public SportResult createSportResult() {
		return new SportResult(student, result, discipline);
	}

}