/*
 * File: src/main/java/com/example/demo/dto/LoginRequest.java
 * Role: DTO
 * MVC Fit: Data Transfer Object used between layers or views.
 * Connects To: Controller/Service use it to carry data
 */

package com.example.demo.dto;
import jakarta.validation.constraints.NotBlank;
// Class Summary: DTO class that is a data carrier between layers or views.
public class LoginRequest {
    @NotBlank(message = "Email or mobile is required")
// Field: stores username for this class.
    private String username; // email or mobile
    @NotBlank(message = "Password is required")
// Field: stores password for this class.
    private String password;
// Method: performs a focused unit of work in this class.
    public String getUsername() {
        // 1. Send the result back to the screen
        return username;
    }
// Method: performs a focused unit of work in this class.
    public void setUsername(String username) {
        this.username = username;
    }
// Method: performs a focused unit of work in this class.
    public String getPassword() {
        // 1. Send the result back to the screen
        return password;
    }
// Method: performs a focused unit of work in this class.
    public void setPassword(String password) {
        this.password = password;
    }
}
