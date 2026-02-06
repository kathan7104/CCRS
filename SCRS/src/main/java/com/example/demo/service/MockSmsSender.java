package com.example.demo.service;
import com.example.demo.entity.OtpVerification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
@Component
@ConditionalOnProperty(name = "ccrs.sms.provider", havingValue = "mock", matchIfMissing = true)
public class MockSmsSender implements SmsSender {
    private static final Logger log = LoggerFactory.getLogger(MockSmsSender.class);
    @Value("${ccrs.sms.mock-file:logs/sms-otp.log}")
    private String mockFilePath;
    @Override
    public void send(String mobile, String otp, OtpVerification.OtpType type, int validMinutes) {
        String line = LocalDateTime.now() + " | " + mobile + " | " + type
                + " | OTP: " + otp + " | valid: " + validMinutes + "m";
        try {
            Path path = Paths.get(mockFilePath);
            Path parent = path.getParent();
            // 1. Check a rule -> decide what to do next
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(path, line + System.lineSeparator(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            // 2. Note: write a log so we can track it
            log.info("Mock SMS OTP written to {}", path.toAbsolutePath());
        } catch (IOException e) {
            // 3. Note: write a log so we can track it
            log.warn("Could not write mock SMS OTP to {}: {}", mockFilePath, e.getMessage());
        }
    }
}
