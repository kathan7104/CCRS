// Package declaration: groups related classes in a namespace.
package com.example.demo.dto;

// Import statement: brings a class into scope by name.
import jakarta.validation.constraints.NotBlank;
// Import statement: brings a class into scope by name.
import jakarta.validation.constraints.Size;
// Import statement: brings a class into scope by name.
import jakarta.validation.constraints.Pattern;

// Comment: explains code for readers.
/**
 // Comment: explains code for readers.
 * DTO for resetting password: contains OTP, email and new password fields.
 // Comment: explains code for readers.
 */
// Class declaration: defines a new type.
public class ResetPasswordRequest {

    // Annotation: adds metadata used by frameworks/tools.
    @NotBlank(message = "OTP is required")
    // Annotation: adds metadata used by frameworks/tools.
    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
    // Field declaration: defines a member variable.
    private String otp;

    // Annotation: adds metadata used by frameworks/tools.
    @NotBlank(message = "Email is required")
    // Field declaration: defines a member variable.
    private String email;

    // Annotation: adds metadata used by frameworks/tools.
    @NotBlank(message = "New password is required")
    // Annotation: adds metadata used by frameworks/tools.
    @Size(min = 8, message = "Password must be at least 8 characters")
    // Annotation: adds metadata used by frameworks/tools.
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
             // Statement: message = "Password must contain uppercase, lowercase and a digit")
             message = "Password must contain uppercase, lowercase and a digit")
    // Field declaration: defines a member variable.
    private String newPassword;

    // Annotation: adds metadata used by frameworks/tools.
    @NotBlank(message = "Confirm password is required")
    // Field declaration: defines a member variable.
    private String confirmPassword;

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
    public String getNewPassword() {
        // Return: sends a value back to the caller.
        return newPassword;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setNewPassword(String newPassword) {
        // Uses current object (this) to access a field or method.
        this.newPassword = newPassword;
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
