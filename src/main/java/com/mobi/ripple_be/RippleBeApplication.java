package com.mobi.ripple_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.mobi.ripple_be.configuration")
public class RippleBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(RippleBeApplication.class, args);
	}

}
