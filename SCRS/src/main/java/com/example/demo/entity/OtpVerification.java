// Package declaration: groups related classes in a namespace.
package com.example.demo.entity;

// Import statement: brings a class into scope by name.
import jakarta.persistence.*;

// Import statement: brings a class into scope by name.
import java.time.LocalDateTime;

// Comment: explains code for readers.
/**
 // Comment: explains code for readers.
 * Stores OTP for email/mobile verification and forgot-password flows.
 // Comment: explains code for readers.
 */
// Annotation: adds metadata used by frameworks/tools.
@Entity
// Annotation: adds metadata used by frameworks/tools.
@Table(name = "otp_verifications", indexes = {
    // Annotation: adds metadata used by frameworks/tools.
    @Index(name = "idx_otp_identifier", columnList = "identifier"),
    // Annotation: adds metadata used by frameworks/tools.
    @Index(name = "idx_otp_expires", columnList = "expires_at")
// Statement: })
})
// Class declaration: defines a new type.
public class OtpVerification {

    // Enum declaration: defines a fixed set of constants.
    public enum OtpType {
        // Statement: EMAIL_VERIFICATION,
        EMAIL_VERIFICATION,
        // Statement: MOBILE_VERIFICATION,
        MOBILE_VERIFICATION,
        // Statement: FORGOT_PASSWORD
        FORGOT_PASSWORD
    // Closes the current code block.
    }

    // Annotation: adds metadata used by frameworks/tools.
    @Id
    // Annotation: adds metadata used by frameworks/tools.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Field declaration: defines a member variable.
    private Long id;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(nullable = false, length = 255)
    // Field declaration: defines a member variable.
    private String identifier; // email or mobile number

    // Annotation: adds metadata used by frameworks/tools.
    @Column(nullable = false, length = 6)
    // Field declaration: defines a member variable.
    private String otp;

    // Annotation: adds metadata used by frameworks/tools.
    @Enumerated(EnumType.STRING)
    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "otp_type", nullable = false)
    // Field declaration: defines a member variable.
    private OtpType otpType;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "created_at")
    // Field declaration: defines a member variable.
    private LocalDateTime createdAt;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "expires_at", nullable = false)
    // Field declaration: defines a member variable.
    private LocalDateTime expiresAt;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "used")
    // Field declaration: defines a member variable.
    private boolean used = false;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "user_id")
    // Field declaration: defines a member variable.
    private Long userId; // for forgot-password link to user

    // Annotation: adds metadata used by frameworks/tools.
    @PrePersist
    // Opens a method/constructor/block.
    protected void onCreate() {
        // Statement: createdAt = LocalDateTime.now();
        createdAt = LocalDateTime.now();
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public OtpVerification() {
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public OtpVerification(String identifier, String otp, OtpType otpType, int validMinutes) {
        // Uses current object (this) to access a field or method.
        this.identifier = identifier;
        // Uses current object (this) to access a field or method.
        this.otp = otp;
        // Uses current object (this) to access a field or method.
        this.otpType = otpType;
        // Uses current object (this) to access a field or method.
        this.expiresAt = LocalDateTime.now().plusMinutes(validMinutes);
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public Long getId() {
        // Return: sends a value back to the caller.
        return id;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setId(Long id) {
        // Uses current object (this) to access a field or method.
        this.id = id;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public String getIdentifier() {
        // Return: sends a value back to the caller.
        return identifier;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setIdentifier(String identifier) {
        // Uses current object (this) to access a field or method.
        this.identifier = identifier;
    // Closes the current code block.
    }

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
    public OtpType getOtpType() {
        // Return: sends a value back to the caller.
        return otpType;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setOtpType(OtpType otpType) {
        // Uses current object (this) to access a field or method.
        this.otpType = otpType;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public LocalDateTime getCreatedAt() {
        // Return: sends a value back to the caller.
        return createdAt;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setCreatedAt(LocalDateTime createdAt) {
        // Uses current object (this) to access a field or method.
        this.createdAt = createdAt;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public LocalDateTime getExpiresAt() {
        // Return: sends a value back to the caller.
        return expiresAt;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setExpiresAt(LocalDateTime expiresAt) {
        // Uses current object (this) to access a field or method.
        this.expiresAt = expiresAt;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public boolean isUsed() {
        // Return: sends a value back to the caller.
        return used;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setUsed(boolean used) {
        // Uses current object (this) to access a field or method.
        this.used = used;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public Long getUserId() {
        // Return: sends a value back to the caller.
        return userId;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setUserId(Long userId) {
        // Uses current object (this) to access a field or method.
        this.userId = userId;
    // Closes the current code block.
    }
// Closes the current code block.
}
