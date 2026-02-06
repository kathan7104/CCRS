package com.example.demo.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


/**
 * DTO for Forgot Password request payload.
 */
public class ForgotPasswordRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    private String email;
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
