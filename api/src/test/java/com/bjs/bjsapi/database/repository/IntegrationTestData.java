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
import com.bjs.bjsapi.database.model.helper.ClassBuilder;
import com.bjs.bjsapi.database.model.helper.SportResultBuilder;
import com.bjs.bjsapi.database.model.helper.StudentBuilder;
import com.bjs.bjsapi.database.model.helper.UserBuilder;
import com.bjs.bjsapi.database.model.helper.UserPrivilegeBuilder;

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
			user = userRepository.save(new UserBuilder()
				.setUsername("abcd")
				.setPassword(encoder.encode("password"))
				.createUser());

			admin = userRepository.save(new UserBuilder()
				.setUsername("administrator")
				.setPassword(encoder.encode("admin"))
				.setAdministrator(true)
				.createUser());

			return true;
		} else {
			return false;
		}
	}

	public boolean setupClasses() {
		if (setupUsers() && classRepository != null && userPrivilegeRepository != null) {
			accessibleClass = classRepository.save(new ClassBuilder()
				.setClassName("a")
				.setGrade("8")
				.setClassTeacherName("ABC")
				.createClass());

			inaccessibleClass = classRepository.save(new ClassBuilder()
				.setClassName("b")
				.setGrade("8")
				.setClassTeacherName("ABC")
				.createClass());

			accessClassPrivilege = userPrivilegeRepository.save(new UserPrivilegeBuilder()
				.setAccessibleClass(accessibleClass)
				.setUser(user)
				.createUserPrivilege());

			return true;
		} else {
			return false;
		}
	}

	public boolean setupStudents() {
		if (setupClasses() && studentRepository != null) {
			accessibleStudent = studentRepository.save(new StudentBuilder()
				.setFirstName("Liam")
				.setLastName("Heß")
				.setSchoolClass(accessibleClass)
				.setBirthDay(Date.valueOf(LocalDate.of(2002, 3, 28)))
				.setFemale(true)
				.createStudent());

			inaccessibleStudent = studentRepository.save(new StudentBuilder()
				.setFirstName("Jalen")
				.setLastName("Buscemi")
				.setSchoolClass(inaccessibleClass)
				.setBirthDay(Date.valueOf(LocalDate.of(2002, 3, 28)))
				.setFemale(true)
				.createStudent());

			return true;
		} else {
			return false;
		}
	}

	public boolean setupSportResults() {
		if (setupStudents() && sportResultRepository != null) {
			accessibleStudentsResult = sportResultRepository.save(new SportResultBuilder().setStudent(accessibleStudent).setDiscipline(DisciplineType.RUN_50).setResult(6.6F).createSportResult());
			inaccessibleStudentsResult = sportResultRepository.save(new SportResultBuilder().setStudent(inaccessibleStudent).setDiscipline(DisciplineType.RUN_50).setResult(6.6F).createSportResult());

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
