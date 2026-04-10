/*
 * File: src/main/java/com/example/demo/service/SmsSender.java
 * Role: Service
 * MVC Fit: Contains business logic used by controllers.
 * Connects To: Controller calls Service, Service calls Repository
 */

package com.example.demo.service;
import com.example.demo.entity.OtpVerification;
// Class Summary: Service class that contains business logic used by controllers.
public interface SmsSender {
    void send(String mobile, String otp, OtpVerification.OtpType type, int validMinutes);
}
