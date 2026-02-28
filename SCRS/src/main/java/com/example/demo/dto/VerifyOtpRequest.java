package com.example.demo.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public class VerifyOtpRequest {
    @NotBlank(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
    private String otp;
    @NotBlank(message = "Identifier (email or mobile) is required")
    private String identifier;
    private String otpType; // "EMAIL", "MOBILE", "FORGOT_PASSWORD"
    public String getOtp() {
        // 1. Send the result back to the screen
        return otp;
    }
    public void setOtp(String otp) {
        this.otp = otp;
    }
    public String getIdentifier() {
        // 1. Send the result back to the screen
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    public String getOtpType() {
        // 1. Send the result back to the screen
        return otpType;
    }
    public void setOtpType(String otpType) {
        this.otpType = otpType;
    }
}
