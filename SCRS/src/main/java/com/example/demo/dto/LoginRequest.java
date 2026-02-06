package com.example.demo.dto;
import jakarta.validation.constraints.NotBlank;


/**
 * DTO for Login request payload.
 */
public class LoginRequest {
    @NotBlank(message = "Email or mobile is required")
    private String username; // email or mobile
    @NotBlank(message = "Password is required")
    private String password;
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
