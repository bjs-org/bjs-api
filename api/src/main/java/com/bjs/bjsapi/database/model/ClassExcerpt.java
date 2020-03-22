package com.bjs.bjsapi.database.model;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "classExcerpt", types = { Class.class })
public interface ClassExcerpt {

	String getGrade();

	String getClassName();

	String getClassTeacherName();
}
