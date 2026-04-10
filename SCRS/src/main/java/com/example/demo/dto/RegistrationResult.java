/*
 * File: src/main/java/com/example/demo/dto/RegistrationResult.java
 * Role: DTO
 * MVC Fit: Data Transfer Object used between layers or views.
 * Connects To: Controller/Service use it to carry data
 */

package com.example.demo.dto;
import com.example.demo.entity.User;
// Class Summary: DTO class that is a data carrier between layers or views.
public class RegistrationResult {
// Field: stores user for this class.
    private final User user;
// Field: stores emailOtpForDisplay for this class.
    private final String emailOtpForDisplay;
// Field: stores mobileOtpForDisplay for this class.
    private final String mobileOtpForDisplay;
// Constructor: Spring injects dependencies here.
    public RegistrationResult(User user, String emailOtpForDisplay, String mobileOtpForDisplay) {
        this.user = user;
        this.emailOtpForDisplay = emailOtpForDisplay;
        this.mobileOtpForDisplay = mobileOtpForDisplay;
    }
// Method: performs a focused unit of work in this class.
    public User getUser() {
        // 1. Send the result back to the screen
        return user;
    }
// Method: performs a focused unit of work in this class.
    public String getEmailOtpForDisplay() {
        // 1. Send the result back to the screen
        return emailOtpForDisplay;
    }
// Method: performs a focused unit of work in this class.
    public String getMobileOtpForDisplay() {
        // 1. Send the result back to the screen
        return mobileOtpForDisplay;
    }
}
