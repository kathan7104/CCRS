// Package declaration: groups related classes in a namespace.
package com.example.demo.dto;

// Import statement: brings a class into scope by name.
import com.example.demo.entity.User;

// Comment: explains code for readers.
/**
 // Comment: explains code for readers.
 * Result of registration: user plus optional OTPs for display when email/SMS are not sent.
 // Comment: explains code for readers.
 */
// Class declaration: defines a new type.
public class RegistrationResult {
    // Field declaration: defines a member variable.
    private final User user;
    // Field declaration: defines a member variable.
    private final String emailOtpForDisplay;
    // Field declaration: defines a member variable.
    private final String mobileOtpForDisplay;

    // Opens a method/constructor/block.
    public RegistrationResult(User user, String emailOtpForDisplay, String mobileOtpForDisplay) {
        // Uses current object (this) to access a field or method.
        this.user = user;
        // Uses current object (this) to access a field or method.
        this.emailOtpForDisplay = emailOtpForDisplay;
        // Uses current object (this) to access a field or method.
        this.mobileOtpForDisplay = mobileOtpForDisplay;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public User getUser() {
        // Return: sends a value back to the caller.
        return user;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public String getEmailOtpForDisplay() {
        // Return: sends a value back to the caller.
        return emailOtpForDisplay;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public String getMobileOtpForDisplay() {
        // Return: sends a value back to the caller.
        return mobileOtpForDisplay;
    // Closes the current code block.
    }
// Closes the current code block.
}
