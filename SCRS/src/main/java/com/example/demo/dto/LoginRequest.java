// Package declaration: groups related classes in a namespace.
package com.example.demo.dto;

// Import statement: brings a class into scope by name.
import jakarta.validation.constraints.NotBlank;

// Comment: explains code for readers.
/**
 // Comment: explains code for readers.
 * DTO for login form. `username` can be email or mobile number.
 // Comment: explains code for readers.
 */
// Class declaration: defines a new type.
public class LoginRequest {

    // Annotation: adds metadata used by frameworks/tools.
    @NotBlank(message = "Email or mobile is required")
    // Field declaration: defines a member variable.
    private String username; // email or mobile

    // Annotation: adds metadata used by frameworks/tools.
    @NotBlank(message = "Password is required")
    // Field declaration: defines a member variable.
    private String password;

    // Opens a method/constructor/block.
    public String getUsername() {
        // Return: sends a value back to the caller.
        return username;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setUsername(String username) {
        // Uses current object (this) to access a field or method.
        this.username = username;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public String getPassword() {
        // Return: sends a value back to the caller.
        return password;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setPassword(String password) {
        // Uses current object (this) to access a field or method.
        this.password = password;
    // Closes the current code block.
    }
// Closes the current code block.
}
