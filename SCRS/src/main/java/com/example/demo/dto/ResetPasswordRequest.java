/*
 * File: src/main/java/com/example/demo/dto/ResetPasswordRequest.java
 * Role: DTO
 * MVC Fit: Data Transfer Object used between layers or views.
 * Connects To: Controller/Service use it to carry data
 */

package com.example.demo.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
// Class Summary: DTO class that is a data carrier between layers or views.
public class ResetPasswordRequest {
    @NotBlank(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
// Field: stores otp for this class.
    private String otp;
    @NotBlank(message = "Email is required")
// Field: stores email for this class.
    private String email;
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
             message = "Password must contain uppercase, lowercase and a digit")
// Field: stores newPassword for this class.
    private String newPassword;
    @NotBlank(message = "Confirm password is required")
// Field: stores confirmPassword for this class.
    private String confirmPassword;
// Method: performs a focused unit of work in this class.
    public String getOtp() {
        // 1. Send the result back to the screen
        return otp;
    }
// Method: performs a focused unit of work in this class.
    public void setOtp(String otp) {
        this.otp = otp;
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
    public String getNewPassword() {
        // 1. Send the result back to the screen
        return newPassword;
    }
// Method: performs a focused unit of work in this class.
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
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
