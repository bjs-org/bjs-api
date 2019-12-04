package com.bjs.bjsapi.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanDefinitions {

	@Bean
	public Clock clock() {
		return Clock.systemDefaultZone();
	}

}
