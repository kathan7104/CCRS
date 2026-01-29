package com.example.demo.dto;

import com.example.demo.entity.User;

/**
 * Result of registration: user plus optional OTPs for display when email/SMS are not sent.
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
