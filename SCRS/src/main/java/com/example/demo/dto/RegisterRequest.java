/*
 * File: src/main/java/com/example/demo/dto/RegisterRequest.java
 * Role: DTO
 * MVC Fit: Data Transfer Object used between layers or views.
 * Connects To: Controller/Service use it to carry data
 */

package com.example.demo.dto;
import jakarta.validation.constraints.*;
// Class Summary: DTO class that is a data carrier between layers or views.
public class RegisterRequest {
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100)
// Field: stores fullName for this class.
    private String fullName;
    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
// Field: stores email for this class.
    private String email;
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile must be exactly 10 digits")
// Field: stores mobileNumber for this class.
    private String mobileNumber;
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
             message = "Password must contain uppercase, lowercase and a digit")
// Field: stores password for this class.
    private String password;
    @NotBlank(message = "Confirm password is required")
// Field: stores confirmPassword for this class.
    private String confirmPassword;
// Method: performs a focused unit of work in this class.
    public String getFullName() {
        // 1. Send the result back to the screen
        return fullName;
    }
// Method: performs a focused unit of work in this class.
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
// Method: performs a focused unit of work in this class.
    public String getEmail() {
        // 1. Send the result back to the screen
        return email;
    }
// Method: performs a focused unit of work in this class.
    public void setEmail(String email) {
        this.email = email;
    }
// Method: performs a focused unit of work in this class.
    public String getMobileNumber() {
        // 1. Send the result back to the screen
        return mobileNumber;
    }
// Method: performs a focused unit of work in this class.
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
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
// Method: performs a focused unit of work in this class.
    public String getConfirmPassword() {
        // 1. Send the result back to the screen
        return confirmPassword;
    }
// Method: performs a focused unit of work in this class.
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
