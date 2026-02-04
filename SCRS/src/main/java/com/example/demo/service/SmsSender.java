// Package declaration: groups related classes in a namespace.
package com.example.demo.service;

// Import statement: brings a class into scope by name.
import com.example.demo.entity.OtpVerification;

// Interface declaration: defines a contract of methods.
public interface SmsSender {
    // Statement: void send(String mobile, String otp, OtpVerification.OtpType type, int validM...
    void send(String mobile, String otp, OtpVerification.OtpType type, int validMinutes);
// Closes the current code block.
}
