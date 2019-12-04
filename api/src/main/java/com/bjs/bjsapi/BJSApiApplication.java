package com.bjs.bjsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.bjs.bjsapi.config.ApiConfiguration;

@SpringBootApplication
@EnableConfigurationProperties(ApiConfiguration.class)
public class BJSApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BJSApiApplication.class, args);
	}

}
