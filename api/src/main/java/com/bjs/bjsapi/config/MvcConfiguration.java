package com.bjs.bjsapi.config;

import static org.springframework.hateoas.config.EnableHypermediaSupport.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableHypermediaSupport(type = HypermediaType.HAL)
@EnableWebMvc
public class MvcConfiguration {

	@Bean
	public WebMvcConfigurer webMvcConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry
					.addMapping("/api/v1/**")
					.allowedOrigins("http://localhost:63342", "http://localhost", "https://bjs-org.github.io")
					.allowCredentials(true);
			}
		};
	}

}
