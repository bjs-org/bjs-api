package com.bjs.bjsapi.database.repository;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bjs.bjsapi.database.model.Class;
import com.bjs.bjsapi.database.model.SportResult;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.model.User;
import com.bjs.bjsapi.database.model.UserPrivilege;
import com.bjs.bjsapi.database.model.enums.DisciplineType;

@TestConfiguration
public class IntegrationTestData {

	@Autowired
	private PasswordEncoder encoder;

	@Autowired(required = false)
	private UserRepository userRepository;

	@Autowired(required = false)
	private SportResultRepository sportResultRepository;

	@Autowired(required = false)
	private StudentRepository studentRepository;

	@Autowired(required = false)
	private ClassRepository classRepository;

	@Autowired(required = false)
	private UserPrivilegeRepository userPrivilegeRepository;

	public User user;
	public User admin;

	public Class accessibleClass;
	public Class inaccessibleClass;

	public UserPrivilege accessClassPrivilege;

	public Student accessibleStudent;
	public Student inaccessibleStudent;

	public SportResult accessibleStudentsResult;
	public SportResult inaccessibleStudentsResult;

	public boolean setupUsers() {
		if (userRepository != null) {
			user = userRepository.save(User.builder()
				.username("abcd")
				.password(encoder.encode("password"))
				.build());

			admin = userRepository.save(User.builder()
				.username("administrator")
				.password(encoder.encode("admin"))
				.administrator(true)
				.build());

			return true;
		} else {
			return false;
		}
	}

	public boolean setupClasses() {
		if (setupUsers() && classRepository != null && userPrivilegeRepository != null) {
			accessibleClass = classRepository.save(Class.builder()
				.className("a")
				.grade("8")
				.classTeacherName("ABC")
				.build());

			inaccessibleClass = classRepository.save(Class.builder()
				.className("b")
				.grade("8")
				.classTeacherName("ABC")
				.build());

			accessClassPrivilege = userPrivilegeRepository.save(UserPrivilege.builder()
				.accessibleClass(accessibleClass)
				.user(user)
				.build());

			return true;
		} else {
			return false;
		}
	}

	public boolean setupStudents() {
		if (setupClasses() && studentRepository != null) {
			accessibleStudent = studentRepository.save(Student.builder()
				.firstName("Liam")
				.lastName("Heß")
				.schoolClass(accessibleClass)
				.birthDay(Date.valueOf(LocalDate.of(2002, 3, 28)))
				.female(true)
				.build());

			inaccessibleStudent = studentRepository.save(Student.builder()
				.firstName("Jalen")
				.lastName("Buscemi")
				.schoolClass(inaccessibleClass)
				.birthDay(Date.valueOf(LocalDate.of(2002, 3, 28)))
				.female(true)
				.build());

			return true;
		} else {
			return false;
		}
	}

	public boolean setupSportResults() {
		if (setupStudents() && sportResultRepository != null) {
			accessibleStudentsResult = sportResultRepository.save(SportResult.builder().student(accessibleStudent).discipline(DisciplineType.RUN_50).result(6.6F).build());
			inaccessibleStudentsResult = sportResultRepository.save(SportResult.builder().student(inaccessibleStudent).discipline(DisciplineType.RUN_50).result(6.6F).build());

			return true;
		} else {
			return false;
		}
	}

	public static String giveNewClass(String newClassName, String newClassTeacherName, String newGrade) {
		final String template = "{\n" +
			"  \"className\": \"%s\",\n" +
			"  \"classTeacherName\": \"%s\",\n" +
			"  \"grade\": \"%s\"\n" +
			"}";

		return String.format(template, newClassName, newClassTeacherName, newGrade);
	}

	static String giveNewStudent(String firstName, String lastName, String schoolClass, String female, String birthDay) {
		final String template = "{\n" +
			"  \"firstName\": \"%s\",\n" +
			"  \"lastName\": \"%s\",\n" +
			"  \"female\": %s,\n" +
			"  \"birthDay\": \"%s\",\n" +
			"  \"schoolClass\": \"%s\"\n" +
			"}";

		return String.format(template, firstName, lastName, female, birthDay, schoolClass);
	}

	static String giveNewStudent(Long schoolClassID) {
		return giveNewStudent("Simon", "Schwarz", String.format("/api/v1/classes/%s", schoolClassID), "false", "2002-01-27");
	}

	static String givenNewSportResult(Long studentID) {
		String sportResult = "{\n" +
			"  \"result\":6.6,\n" +
			"  \"discipline\":\"RUN_100\",\n" +
			"  \"student\":\"/%d\"\n" +
			"}";

		return String.format(sportResult, studentID);
	}

	static String givenNewUser() {
		//language=JSON
		String template = "{\n" +
			"  \"username\": \"%s\",\n" +
			"  \"password\": \"%s\"\n" +
			"}";

		return String.format(template, "jfskk", "idontknown");
	}

}
