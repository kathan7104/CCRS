package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main application class for the SCRS project.
 * This is the Spring Boot entry point that starts the application.
 */
@SpringBootApplication
@EnableAsync
public class ScrsApplication {

	/**
	 * Start the Spring Boot application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(ScrsApplication.class, args);
	}

}
