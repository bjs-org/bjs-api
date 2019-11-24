package com.bjs.bjsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.bjs.bjsapi.config.CalculationInformationConfig;

@SpringBootApplication
@EnableConfigurationProperties(CalculationInformationConfig.class)
public class BJSApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BJSApiApplication.class, args);
	}

}
