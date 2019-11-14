package com.bjs.bjsapi.config;

import static org.springframework.hateoas.config.EnableHypermediaSupport.*;

import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableHypermediaSupport(type = HypermediaType.HAL)
@EnableWebMvc
public class MvcConfiguration {

}
