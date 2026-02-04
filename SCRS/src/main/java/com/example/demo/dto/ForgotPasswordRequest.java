// Package declaration: groups related classes in a namespace.
package com.example.demo.dto;

// Import statement: brings a class into scope by name.
import jakarta.validation.constraints.Email;
// Import statement: brings a class into scope by name.
import jakarta.validation.constraints.NotBlank;

// Comment: explains code for readers.
/**
 // Comment: explains code for readers.
 * DTO for requesting a forgot-password OTP by email.
 // Comment: explains code for readers.
 */
// Class declaration: defines a new type.
public class ForgotPasswordRequest {

    // Annotation: adds metadata used by frameworks/tools.
    @NotBlank(message = "Email is required")
    // Annotation: adds metadata used by frameworks/tools.
    @Email(message = "Valid email is required")
    // Field declaration: defines a member variable.
    private String email;

    // Opens a method/constructor/block.
    public String getEmail() {
        // Return: sends a value back to the caller.
        return email;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setEmail(String email) {
        // Uses current object (this) to access a field or method.
        this.email = email;
    // Closes the current code block.
    }
// Closes the current code block.
}
