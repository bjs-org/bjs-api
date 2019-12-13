package com.bjs.bjsapi.config;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bjs.bjsapi.controllers.StudentCalculationController;
import com.bjs.bjsapi.database.model.Student;
import com.bjs.bjsapi.database.repository.UserRepository;
import com.bjs.bjsapi.security.UserRepositoryEventHandler;

@Configuration
public class RepositoryConfiguration {

	@Bean
	public UserRepositoryEventHandler userRepositoryEventHandler(PasswordEncoder passwordEncoder, UserRepository userRepository) {
		return new UserRepositoryEventHandler(userRepository, passwordEncoder);
	}

	@Bean
	public RepresentationModelProcessor<EntityModel<Student>> studentProcessor() {
		return new RepresentationModelProcessor<EntityModel<Student>>() {
			@Override
			public EntityModel<Student> process(EntityModel<Student> model) {
				final Student student = model.getContent();
				if (student != null) {
					model.add(linkTo(methodOn(StudentCalculationController.class).score(student.getId())).withRel("score"));
					model.add(linkTo(methodOn(StudentCalculationController.class).classification(student.getId())).withRel("classification"));
				}
				return model;
			}
		};
	}

}
