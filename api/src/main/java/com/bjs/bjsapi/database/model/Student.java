package com.bjs.bjsapi.database.model;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
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

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
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
	@EqualsAndHashCode.Exclude
	private Class schoolClass;

	@OneToMany(mappedBy = "student")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private List<SportResult> sportResults;

	public int getAgeByYear(Clock clock) {
		final int birthYear = Instant.ofEpochMilli(getBirthDay().getTime()).atZone(ZoneId.systemDefault()).toLocalDate().getYear();
		final int currentYear = Instant.now(clock).atZone(ZoneId.systemDefault()).toLocalDate().getYear();
		return Math.abs(birthYear - currentYear);
	}

}
