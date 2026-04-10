/*
 * File: src/test/java/com/example/demo/TestSupportConfig.java
 * Role: Test
 * MVC Fit: Automated tests for application behavior.
 * Connects To: Verifies layers in isolation or integration
 */

package com.example.demo;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

// Class Summary: Test class that verifies application behavior.
@TestConfiguration
public class TestSupportConfig {

// @Bean tells Spring to manage the returned object as a bean.
    @Bean
// Test method: verifies behavior with assertions and test setup.
    public JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl();
    }
}
