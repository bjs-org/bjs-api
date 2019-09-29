package com.bjs.bjsapi.config;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;

import com.bjs.bjsapi.controllers.StudentScoreController;
import com.bjs.bjsapi.database.model.Student;

@Configuration
public class RepositoryConfig {

	@SuppressWarnings("Convert2Lambda")
	@Bean
	public ResourceProcessor<Resource<Student>> personProcessor() {

		return new ResourceProcessor<Resource<Student>>() {
			@Override
			public Resource<Student> process(Resource<Student> resource) {
				Student content = resource.getContent();
				if (content != null) {
					Long id = content.getId();
					resource.add(linkTo(StudentScoreController.class, id).slash("score").withRel("score"));
				}

				return resource;
			}
		};
	}

}
