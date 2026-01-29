package com.example.demo.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Stores OTP for email/mobile verification and forgot-password flows.
 */
@Entity
@Table(name = "otp_verifications", indexes = {
    @Index(name = "idx_otp_identifier", columnList = "identifier"),
    @Index(name = "idx_otp_expires", columnList = "expires_at")
})
public class OtpVerification {

    public enum OtpType {
        EMAIL_VERIFICATION,
        MOBILE_VERIFICATION,
        FORGOT_PASSWORD
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String identifier; // email or mobile number

    @Column(nullable = false, length = 6)
    private String otp;

    @Enumerated(EnumType.STRING)
    @Column(name = "otp_type", nullable = false)
    private OtpType otpType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used")
    private boolean used = false;

    @Column(name = "user_id")
    private Long userId; // for forgot-password link to user

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public OtpVerification() {
    }

    public OtpVerification(String identifier, String otp, OtpType otpType, int validMinutes) {
        this.identifier = identifier;
        this.otp = otp;
        this.otpType = otpType;
        this.expiresAt = LocalDateTime.now().plusMinutes(validMinutes);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public OtpType getOtpType() {
        return otpType;
    }

    public void setOtpType(OtpType otpType) {
        this.otpType = otpType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
