package com.example.demo.dto;
import jakarta.validation.constraints.NotBlank;
public class LoginRequest {
    @NotBlank(message = "Email or mobile is required")
    private String username; // email or mobile
    @NotBlank(message = "Password is required")
    private String password;
    public String getUsername() {
        // 1. Send the result back to the screen
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        // 1. Send the result back to the screen
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
