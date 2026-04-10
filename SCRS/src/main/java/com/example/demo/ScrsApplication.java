/*
 * File: src/main/java/com/example/demo/ScrsApplication.java
 * Role: Application
 * MVC Fit: Application entry point or shared utility.
 * Connects To: Bootstraps Spring and shared components
 */

package com.example.demo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
// Class Summary: Application class that is an application entry point or shared utility.
// @SpringBootApplication enables auto-configuration, component scanning, and configuration support.
@SpringBootApplication
// @EnableAsync allows @Async methods to run in background threads.
@EnableAsync
public class ScrsApplication {
// Method: performs a focused unit of work in this class.
	public static void main(String[] args) {
		SpringApplication.run(ScrsApplication.class, args);
	}
}
