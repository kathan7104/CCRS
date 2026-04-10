/*
 * File: src/main/java/com/example/demo/entity/User.java
 * Role: Entity
 * MVC Fit: JPA entity that maps to a database table.
 * Connects To: Repository reads/writes this entity
 */

package com.example.demo.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
// @Entity maps this class to a database table managed by JPA.
@Entity
// @Table specifies the database table name for this entity.
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email", unique = true),
    @Index(name = "idx_user_mobile", columnList = "mobile_number", unique = true)
})
// Class Summary: Entity class that is a JPA entity mapped to a database table.
public class User {
// @Id marks the primary key field for the entity.
    @Id
// @GeneratedValue lets JPA auto-generate primary key values.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
// Field: stores id for this class.
    private Long id;
    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
// @Column customizes the database column mapping.
    @Column(nullable = false, unique = true, length = 255)
// Field: stores email for this class.
    private String email;
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile must be 10 digits")
// @Column customizes the database column mapping.
    @Column(name = "mobile_number", nullable = false, unique = true, length = 10)
// Field: stores mobileNumber for this class.
    private String mobileNumber;
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
// @Column customizes the database column mapping.
    @Column(nullable = false, length = 255)
// Field: stores password for this class.
    private String password;
    @NotBlank(message = "Full name is required")
    @Size(max = 100)
// @Column customizes the database column mapping.
    @Column(name = "full_name", nullable = false, length = 100)
// Field: stores fullName for this class.
    private String fullName;
// @Column customizes the database column mapping.
    @Column(name = "department", length = 100)
// Field: stores department for this class.
    private String department;
// @Column customizes the database column mapping.
    @Column(name = "email_verified")
// Field: stores emailVerified for this class.
    private boolean emailVerified = false;
// @Column customizes the database column mapping.
    @Column(name = "mobile_verified")
// Field: stores mobileVerified for this class.
    private boolean mobileVerified = false;
// @Column customizes the database column mapping.
    @Column(name = "is_active")
// Field: stores active for this class.
    private boolean active = true;
// @Column customizes the database column mapping.
    @Column(name = "created_at")
// Field: stores createdAt for this class.
    private LocalDateTime createdAt;
// @Column customizes the database column mapping.
    @Column(name = "updated_at")
// Field: stores updatedAt for this class.
    private LocalDateTime updatedAt;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
// @Column customizes the database column mapping.
    @Column(name = "role")
// Field: stores roles for this class.
// Method: performs a focused unit of work in this class.
    private Set<String> roles = new HashSet<>();
    @PrePersist
// Method: performs a focused unit of work in this class.
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    @PreUpdate
// Method: performs a focused unit of work in this class.
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
// Constructor: Spring injects dependencies here.
    public User() {
    }
// Constructor: Spring injects dependencies here.
    public User(String email, String mobileNumber, String password, String fullName) {
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.password = password;
        this.fullName = fullName;
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
    public String getEmail() {
        // 1. Send the result back to the screen
        return email;
    }
// Method: performs a focused unit of work in this class.
    public void setEmail(String email) {
        this.email = email;
    }
// Method: performs a focused unit of work in this class.
    public String getMobileNumber() {
        // 1. Send the result back to the screen
        return mobileNumber;
    }
// Method: performs a focused unit of work in this class.
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
// Method: performs a focused unit of work in this class.
    public String getPassword() {
        // 1. Send the result back to the screen
        return password;
    }
// Method: performs a focused unit of work in this class.
    public void setPassword(String password) {
        this.password = password;
    }
// Method: performs a focused unit of work in this class.
    public String getFullName() {
        // 1. Send the result back to the screen
        return fullName;
    }
// Method: performs a focused unit of work in this class.
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
// Method: performs a focused unit of work in this class.
    public String getDepartment() {
        return department;
    }
// Method: performs a focused unit of work in this class.
    public void setDepartment(String department) {
        this.department = department;
    }
// Method: performs a focused unit of work in this class.
    public boolean isEmailVerified() {
        // 1. Send the result back to the screen
        return emailVerified;
    }
// Method: performs a focused unit of work in this class.
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
// Method: performs a focused unit of work in this class.
    public boolean isMobileVerified() {
        // 1. Send the result back to the screen
        return mobileVerified;
    }
// Method: performs a focused unit of work in this class.
    public void setMobileVerified(boolean mobileVerified) {
        this.mobileVerified = mobileVerified;
    }
// Method: performs a focused unit of work in this class.
    public boolean isActive() {
        // 1. Send the result back to the screen
        return active;
    }
// Method: performs a focused unit of work in this class.
    public void setActive(boolean active) {
        this.active = active;
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
    public LocalDateTime getUpdatedAt() {
        // 1. Send the result back to the screen
        return updatedAt;
    }
// Method: performs a focused unit of work in this class.
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
// Method: performs a focused unit of work in this class.
    public Set<String> getRoles() {
        // 1. Send the result back to the screen
        return roles;
    }
// Method: performs a focused unit of work in this class.
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
