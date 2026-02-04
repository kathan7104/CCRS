// Package declaration: groups related classes in a namespace.
package com.example.demo.dto;

// Import statement: brings a class into scope by name.
import jakarta.validation.constraints.NotBlank;
// Import statement: brings a class into scope by name.
import jakarta.validation.constraints.Size;

// Comment: explains code for readers.
/**
 // Comment: explains code for readers.
 * DTO for submitting an OTP and its identifier (email or mobile) and type.
 // Comment: explains code for readers.
 */
// Class declaration: defines a new type.
public class VerifyOtpRequest {

    // Annotation: adds metadata used by frameworks/tools.
    @NotBlank(message = "OTP is required")
    // Annotation: adds metadata used by frameworks/tools.
    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
    // Field declaration: defines a member variable.
    private String otp;

    // Annotation: adds metadata used by frameworks/tools.
    @NotBlank(message = "Identifier (email or mobile) is required")
    // Field declaration: defines a member variable.
    private String identifier;

    // Field declaration: defines a member variable.
    private String otpType; // "EMAIL", "MOBILE", "FORGOT_PASSWORD"

    // Opens a method/constructor/block.
    public String getOtp() {
        // Return: sends a value back to the caller.
        return otp;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setOtp(String otp) {
        // Uses current object (this) to access a field or method.
        this.otp = otp;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public String getIdentifier() {
        // Return: sends a value back to the caller.
        return identifier;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setIdentifier(String identifier) {
        // Uses current object (this) to access a field or method.
        this.identifier = identifier;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public String getOtpType() {
        // Return: sends a value back to the caller.
        return otpType;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setOtpType(String otpType) {
        // Uses current object (this) to access a field or method.
        this.otpType = otpType;
    // Closes the current code block.
    }
// Closes the current code block.
}
