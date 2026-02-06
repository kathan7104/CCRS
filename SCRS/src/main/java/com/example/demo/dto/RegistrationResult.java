package com.example.demo.dto;
import com.example.demo.entity.User;


/**
 * DTO for Registration result.
 */
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
        return user;
    }
    public String getEmailOtpForDisplay() {
        return emailOtpForDisplay;
    }
    public String getMobileOtpForDisplay() {
        return mobileOtpForDisplay;
    }
}
