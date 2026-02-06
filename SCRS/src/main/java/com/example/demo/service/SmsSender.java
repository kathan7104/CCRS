package com.example.demo.service;
import com.example.demo.entity.OtpVerification;

/**
 * Business logic for SMS Sender.
 */
public interface SmsSender {
    void send(String mobile, String otp, OtpVerification.OtpType type, int validMinutes);
}
