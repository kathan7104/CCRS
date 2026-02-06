package com.example.demo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


/**
 * Spring Boot application entry point.
 */
@SpringBootApplication
@EnableAsync
public class ScrsApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ScrsApplication.class, args);
	}
}
