package com.example.demo.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email", unique = true),
    @Index(name = "idx_user_mobile", columnList = "mobile_number", unique = true)
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile must be 10 digits")
    @Column(name = "mobile_number", nullable = false, unique = true, length = 10)
    private String mobileNumber;
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Column(nullable = false, length = 255)
    private String password;
    @NotBlank(message = "Full name is required")
    @Size(max = 100)
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;
    @Column(name = "department", length = 100)
    private String department;
    @Column(name = "email_verified")
    private boolean emailVerified = false;
    @Column(name = "mobile_verified")
    private boolean mobileVerified = false;
    @Column(name = "is_active")
    private boolean active = true;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    public User() {
    }
    public User(String email, String mobileNumber, String password, String fullName) {
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.password = password;
        this.fullName = fullName;
    }
    public Long getId() {
        // 1. Send the result back to the screen
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEmail() {
        // 1. Send the result back to the screen
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getMobileNumber() {
        // 1. Send the result back to the screen
        return mobileNumber;
    }
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
    public String getPassword() {
        // 1. Send the result back to the screen
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getFullName() {
        // 1. Send the result back to the screen
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public boolean isEmailVerified() {
        // 1. Send the result back to the screen
        return emailVerified;
    }
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
    public boolean isMobileVerified() {
        // 1. Send the result back to the screen
        return mobileVerified;
    }
    public void setMobileVerified(boolean mobileVerified) {
        this.mobileVerified = mobileVerified;
    }
    public boolean isActive() {
        // 1. Send the result back to the screen
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public LocalDateTime getCreatedAt() {
        // 1. Send the result back to the screen
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        // 1. Send the result back to the screen
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Set<String> getRoles() {
        // 1. Send the result back to the screen
        return roles;
    }
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
