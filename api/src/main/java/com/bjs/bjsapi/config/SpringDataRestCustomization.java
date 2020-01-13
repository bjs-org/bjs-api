package com.bjs.bjsapi.config;

import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class SpringDataRestCustomization implements RepositoryRestConfigurer {

	public final WebMvcConfigurer webMvcConfigurer;

	public SpringDataRestCustomization(WebMvcConfigurer webMvcConfigurer) {
		this.webMvcConfigurer = webMvcConfigurer;
	}

	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
		webMvcConfigurer.addCorsMappings(config.getCorsRegistry());
	}

}