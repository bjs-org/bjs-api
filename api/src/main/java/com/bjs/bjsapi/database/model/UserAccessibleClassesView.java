package com.bjs.bjsapi.database.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "accessibleClasses", types = { User.class })
public interface UserAccessibleClassesView {

	String getUsername();

	Boolean getAdministrator();

	Boolean getEnabled();

	@Value("#{@userAccessibleClassService.getAccessibleClassesByUser(target)}")
	List<ClassExcerpt> getAccessibleClasses();

}
