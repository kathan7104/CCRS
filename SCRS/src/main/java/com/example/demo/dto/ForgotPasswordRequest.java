package com.example.demo.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public class ForgotPasswordRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    private String email;
    public String getEmail() {
        // 1. Send the result back to the screen
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
