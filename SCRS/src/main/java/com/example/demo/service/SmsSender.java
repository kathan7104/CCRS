package com.example.demo.service;
import com.example.demo.entity.OtpVerification;
public interface SmsSender {
    void send(String mobile, String otp, OtpVerification.OtpType type, int validMinutes);
}
