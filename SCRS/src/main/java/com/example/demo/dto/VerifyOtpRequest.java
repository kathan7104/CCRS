/*
 * File: src/main/java/com/example/demo/dto/VerifyOtpRequest.java
 * Role: DTO
 * MVC Fit: Data Transfer Object used between layers or views.
 * Connects To: Controller/Service use it to carry data
 */

package com.example.demo.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// Class Summary: DTO class that is a data carrier between layers or views.
public class VerifyOtpRequest {
    @NotBlank(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
// Field: stores otp for this class.
    private String otp;
    @NotBlank(message = "Identifier (email or mobile) is required")
// Field: stores identifier for this class.
    private String identifier;
// Field: stores otpType for this class.
    private String otpType; // "EMAIL", "MOBILE", "FORGOT_PASSWORD"
// Method: performs a focused unit of work in this class.
    public String getOtp() {
        // 1. Send the result back to the screen
        return otp;
    }
// Method: performs a focused unit of work in this class.
    public void setOtp(String otp) {
        this.otp = otp;
    }
// Method: performs a focused unit of work in this class.
    public String getIdentifier() {
        // 1. Send the result back to the screen
        return identifier;
    }
// Method: performs a focused unit of work in this class.
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
// Method: performs a focused unit of work in this class.
    public String getOtpType() {
        // 1. Send the result back to the screen
        return otpType;
    }
// Method: performs a focused unit of work in this class.
    public void setOtpType(String otpType) {
        this.otpType = otpType;
    }
}
