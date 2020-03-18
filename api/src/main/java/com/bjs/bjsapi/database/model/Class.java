package com.bjs.bjsapi.database.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
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

	private boolean classClosed;

	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "schoolClass")
	private List<Student> students;

	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "accessibleClass")
	private List<UserPrivilege> privileges;

	public void setClosed(boolean closed){
		classClosed = closed;
	}
}
