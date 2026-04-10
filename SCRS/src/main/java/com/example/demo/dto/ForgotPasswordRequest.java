/*
 * File: src/main/java/com/example/demo/dto/ForgotPasswordRequest.java
 * Role: DTO
 * MVC Fit: Data Transfer Object used between layers or views.
 * Connects To: Controller/Service use it to carry data
 */

package com.example.demo.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
// Class Summary: DTO class that is a data carrier between layers or views.
public class ForgotPasswordRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
// Field: stores email for this class.
    private String email;
// Method: performs a focused unit of work in this class.
    public String getEmail() {
        // 1. Send the result back to the screen
        return email;
    }
// Method: performs a focused unit of work in this class.
    public void setEmail(String email) {
        this.email = email;
    }
}
