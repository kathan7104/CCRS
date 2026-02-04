// Package declaration: groups related classes in a namespace.
package com.example.demo.dto;

// Import statement: brings a class into scope by name.
import jakarta.validation.constraints.*;

// Comment: explains code for readers.
/**
 // Comment: explains code for readers.
 * DTO for user registration form data.
 // Comment: explains code for readers.
 * Validated on submission.
 // Comment: explains code for readers.
 */
// Class declaration: defines a new type.
public class RegisterRequest {

    // Annotation: adds metadata used by frameworks/tools.
    @NotBlank(message = "Full name is required")
    // Annotation: adds metadata used by frameworks/tools.
    @Size(min = 2, max = 100)
    // Field declaration: defines a member variable.
    private String fullName;

    // Annotation: adds metadata used by frameworks/tools.
    @NotBlank(message = "Email is required")
    // Annotation: adds metadata used by frameworks/tools.
    @Email(message = "Valid email is required")
    // Field declaration: defines a member variable.
    private String email;

    // Annotation: adds metadata used by frameworks/tools.
    @NotBlank(message = "Mobile number is required")
    // Annotation: adds metadata used by frameworks/tools.
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile must be exactly 10 digits")
    // Field declaration: defines a member variable.
    private String mobileNumber;

    // Annotation: adds metadata used by frameworks/tools.
    @NotBlank(message = "Password is required")
    // Annotation: adds metadata used by frameworks/tools.
    @Size(min = 8, message = "Password must be at least 8 characters")
    // Annotation: adds metadata used by frameworks/tools.
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
             // Statement: message = "Password must contain uppercase, lowercase and a digit")
             message = "Password must contain uppercase, lowercase and a digit")
    // Field declaration: defines a member variable.
    private String password;

    // Annotation: adds metadata used by frameworks/tools.
    @NotBlank(message = "Confirm password is required")
    // Field declaration: defines a member variable.
    private String confirmPassword;

    // Opens a method/constructor/block.
    public String getFullName() {
        // Return: sends a value back to the caller.
        return fullName;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setFullName(String fullName) {
        // Uses current object (this) to access a field or method.
        this.fullName = fullName;
    // Closes the current code block.
    }

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

    // Opens a method/constructor/block.
    public String getMobileNumber() {
        // Return: sends a value back to the caller.
        return mobileNumber;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setMobileNumber(String mobileNumber) {
        // Uses current object (this) to access a field or method.
        this.mobileNumber = mobileNumber;
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

    // Opens a method/constructor/block.
    public String getConfirmPassword() {
        // Return: sends a value back to the caller.
        return confirmPassword;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setConfirmPassword(String confirmPassword) {
        // Uses current object (this) to access a field or method.
        this.confirmPassword = confirmPassword;
    // Closes the current code block.
    }
// Closes the current code block.
}
