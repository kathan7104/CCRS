/*
 * File: src/main/java/com/example/demo/entity/OtpVerification.java
 * Role: Entity
 * MVC Fit: JPA entity that maps to a database table.
 * Connects To: Repository reads/writes this entity
 */

package com.example.demo.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
// @Entity maps this class to a database table managed by JPA.
@Entity
// @Table specifies the database table name for this entity.
@Table(name = "otp_verifications", indexes = {
    @Index(name = "idx_otp_identifier", columnList = "identifier"),
    @Index(name = "idx_otp_expires", columnList = "expires_at")
})
// Class Summary: Entity class that is a JPA entity mapped to a database table.
public class OtpVerification {
    public enum OtpType {
        EMAIL_VERIFICATION,
        MOBILE_VERIFICATION,
        FORGOT_PASSWORD
    }
// @Id marks the primary key field for the entity.
    @Id
// @GeneratedValue lets JPA auto-generate primary key values.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
// Field: stores id for this class.
    private Long id;
// @Column customizes the database column mapping.
    @Column(nullable = false, length = 255)
// Field: stores identifier for this class.
    private String identifier; // email or mobile number
// @Column customizes the database column mapping.
    @Column(nullable = false, length = 6)
// Field: stores otp for this class.
    private String otp;
// @Enumerated tells JPA how to store enum values in the database.
    @Enumerated(EnumType.STRING)
// @Column customizes the database column mapping.
    @Column(name = "otp_type", nullable = false)
// Field: stores otpType for this class.
    private OtpType otpType;
// @Column customizes the database column mapping.
    @Column(name = "created_at")
// Field: stores createdAt for this class.
    private LocalDateTime createdAt;
// @Column customizes the database column mapping.
    @Column(name = "expires_at", nullable = false)
// Field: stores expiresAt for this class.
    private LocalDateTime expiresAt;
// @Column customizes the database column mapping.
    @Column(name = "used")
// Field: stores used for this class.
    private boolean used = false;
// @Column customizes the database column mapping.
    @Column(name = "user_id")
// Field: stores userId for this class.
    private Long userId; // for forgot-password link to user
    @PrePersist
// Method: performs a focused unit of work in this class.
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
// Constructor: Spring injects dependencies here.
    public OtpVerification() {
    }
// Constructor: Spring injects dependencies here.
    public OtpVerification(String identifier, String otp, OtpType otpType, int validMinutes) {
        this.identifier = identifier;
        this.otp = otp;
        this.otpType = otpType;
        this.expiresAt = LocalDateTime.now().plusMinutes(validMinutes);
    }
// Method: performs a focused unit of work in this class.
    public Long getId() {
        // 1. Send the result back to the screen
        return id;
    }
// Method: performs a focused unit of work in this class.
    public void setId(Long id) {
        this.id = id;
    }
// Method: performs a focused unit of work in this class.
    public String getIdentifier() {
        // 1. Send the result back to the screen
        return identifier;
    }
// Method: performs a focused unit of work in this class.
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
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
    public OtpType getOtpType() {
        // 1. Send the result back to the screen
        return otpType;
    }
// Method: performs a focused unit of work in this class.
    public void setOtpType(OtpType otpType) {
        this.otpType = otpType;
    }
// Method: performs a focused unit of work in this class.
    public LocalDateTime getCreatedAt() {
        // 1. Send the result back to the screen
        return createdAt;
    }
// Method: performs a focused unit of work in this class.
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
// Method: performs a focused unit of work in this class.
    public LocalDateTime getExpiresAt() {
        // 1. Send the result back to the screen
        return expiresAt;
    }
// Method: performs a focused unit of work in this class.
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
// Method: performs a focused unit of work in this class.
    public boolean isUsed() {
        // 1. Send the result back to the screen
        return used;
    }
// Method: performs a focused unit of work in this class.
    public void setUsed(boolean used) {
        this.used = used;
    }
// Method: performs a focused unit of work in this class.
    public Long getUserId() {
        // 1. Send the result back to the screen
        return userId;
    }
// Method: performs a focused unit of work in this class.
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
