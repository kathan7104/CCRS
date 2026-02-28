package com.example.demo.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
public class ResetPasswordRequest {
    @NotBlank(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
    private String otp;
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
             message = "Password must contain uppercase, lowercase and a digit")
    private String newPassword;
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
    public String getOtp() {
        // 1. Send the result back to the screen
        return otp;
    }
    public void setOtp(String otp) {
        this.otp = otp;
    }
    public String getEmail() {
        // 1. Send the result back to the screen
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getNewPassword() {
        // 1. Send the result back to the screen
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    public String getConfirmPassword() {
        // 1. Send the result back to the screen
        return confirmPassword;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
