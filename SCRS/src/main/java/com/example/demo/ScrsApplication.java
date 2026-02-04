// Package declaration: groups related classes in a namespace.
package com.example.demo;

// Import statement: brings a class into scope by name.
import org.springframework.boot.SpringApplication;
// Import statement: brings a class into scope by name.
import org.springframework.boot.autoconfigure.SpringBootApplication;
// Import statement: brings a class into scope by name.
import org.springframework.scheduling.annotation.EnableAsync;

// Comment: explains code for readers.
/**
 // Comment: explains code for readers.
 * Main application class for the SCRS project.
 // Comment: explains code for readers.
 * This is the Spring Boot entry point that starts the application.
 // Comment: explains code for readers.
 */
// Annotation: adds metadata used by frameworks/tools.
@SpringBootApplication
// Annotation: adds metadata used by frameworks/tools.
@EnableAsync
// Class declaration: defines a new type.
public class ScrsApplication {

	// Comment: explains code for readers.
	/**
	 // Comment: explains code for readers.
	 * Start the Spring Boot application.
	 // Comment: explains code for readers.
	 */
	// Opens a method/constructor/block.
	public static void main(String[] args) {
		// Statement: SpringApplication.run(ScrsApplication.class, args);
		SpringApplication.run(ScrsApplication.class, args);
	// Closes the current code block.
	}

// Closes the current code block.
}
