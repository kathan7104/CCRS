// Package declaration: groups related classes in a namespace.
package com.example.demo.service;

// Import statement: brings a class into scope by name.
import com.example.demo.entity.OtpVerification;
// Import statement: brings a class into scope by name.
import org.slf4j.Logger;
// Import statement: brings a class into scope by name.
import org.slf4j.LoggerFactory;
// Import statement: brings a class into scope by name.
import org.springframework.beans.factory.annotation.Value;
// Import statement: brings a class into scope by name.
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// Import statement: brings a class into scope by name.
import org.springframework.stereotype.Component;

// Import statement: brings a class into scope by name.
import java.io.IOException;
// Import statement: brings a class into scope by name.
import java.nio.charset.StandardCharsets;
// Import statement: brings a class into scope by name.
import java.nio.file.Files;
// Import statement: brings a class into scope by name.
import java.nio.file.Path;
// Import statement: brings a class into scope by name.
import java.nio.file.Paths;
// Import statement: brings a class into scope by name.
import java.nio.file.StandardOpenOption;
// Import statement: brings a class into scope by name.
import java.time.LocalDateTime;

// Annotation: adds metadata used by frameworks/tools.
@Component
// Annotation: adds metadata used by frameworks/tools.
@ConditionalOnProperty(name = "ccrs.sms.provider", havingValue = "mock", matchIfMissing = true)
// Class declaration: defines a new type.
public class MockSmsSender implements SmsSender {

    // Method or constructor declaration.
    private static final Logger log = LoggerFactory.getLogger(MockSmsSender.class);

    // Annotation: adds metadata used by frameworks/tools.
    @Value("${ccrs.sms.mock-file:logs/sms-otp.log}")
    // Field declaration: defines a member variable.
    private String mockFilePath;

    // Annotation: adds metadata used by frameworks/tools.
    @Override
    // Opens a method/constructor/block.
    public void send(String mobile, String otp, OtpVerification.OtpType type, int validMinutes) {
        // Statement: String line = LocalDateTime.now() + " | " + mobile + " | " + type
        String line = LocalDateTime.now() + " | " + mobile + " | " + type
                // Statement: + " | OTP: " + otp + " | valid: " + validMinutes + "m";
                + " | OTP: " + otp + " | valid: " + validMinutes + "m";
        // Opens a new code block.
        try {
            // Statement: Path path = Paths.get(mockFilePath);
            Path path = Paths.get(mockFilePath);
            // Statement: Path parent = path.getParent();
            Path parent = path.getParent();
            // Opens a method/constructor/block.
            if (parent != null) {
                // Statement: Files.createDirectories(parent);
                Files.createDirectories(parent);
            // Closes the current code block.
            }
            // Statement: Files.writeString(path, line + System.lineSeparator(), StandardCharsets.UTF_8,
            Files.writeString(path, line + System.lineSeparator(), StandardCharsets.UTF_8,
                    // Statement: StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            // Statement: log.info("Mock SMS OTP written to {}", path.toAbsolutePath());
            log.info("Mock SMS OTP written to {}", path.toAbsolutePath());
        // Opens a method/constructor/block.
        } catch (IOException e) {
            // Statement: log.warn("Could not write mock SMS OTP to {}: {}", mockFilePath, e.getMessage...
            log.warn("Could not write mock SMS OTP to {}: {}", mockFilePath, e.getMessage());
        // Closes the current code block.
        }
    // Closes the current code block.
    }
// Closes the current code block.
}
