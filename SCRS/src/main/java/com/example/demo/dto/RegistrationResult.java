package com.example.demo.dto;
import com.example.demo.entity.User;
public class RegistrationResult {
    private final User user;
    private final String emailOtpForDisplay;
    private final String mobileOtpForDisplay;
    public RegistrationResult(User user, String emailOtpForDisplay, String mobileOtpForDisplay) {
        this.user = user;
        this.emailOtpForDisplay = emailOtpForDisplay;
        this.mobileOtpForDisplay = mobileOtpForDisplay;
    }
    public User getUser() {
        // 1. Send the result back to the screen
        return user;
    }
    public String getEmailOtpForDisplay() {
        // 1. Send the result back to the screen
        return emailOtpForDisplay;
    }
    public String getMobileOtpForDisplay() {
        // 1. Send the result back to the screen
        return mobileOtpForDisplay;
    }
}
