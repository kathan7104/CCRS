// Package declaration: groups related classes in a namespace.
package com.example.demo.entity;

// Import statement: brings a class into scope by name.
import jakarta.persistence.*;
// Import statement: brings a class into scope by name.
import jakarta.validation.constraints.Email;
// Import statement: brings a class into scope by name.
import jakarta.validation.constraints.NotBlank;
// Import statement: brings a class into scope by name.
import jakarta.validation.constraints.Pattern;
// Import statement: brings a class into scope by name.
import jakarta.validation.constraints.Size;

// Import statement: brings a class into scope by name.
import java.time.LocalDateTime;
// Import statement: brings a class into scope by name.
import java.util.HashSet;
// Import statement: brings a class into scope by name.
import java.util.Set;

// Comment: explains code for readers.
/**
 // Comment: explains code for readers.
 * User entity for CCRS - represents students and their authentication data.
 // Comment: explains code for readers.
 */
// Annotation: adds metadata used by frameworks/tools.
@Entity
// Annotation: adds metadata used by frameworks/tools.
@Table(name = "users", indexes = {
    // Annotation: adds metadata used by frameworks/tools.
    @Index(name = "idx_user_email", columnList = "email", unique = true),
    // Annotation: adds metadata used by frameworks/tools.
    @Index(name = "idx_user_mobile", columnList = "mobile_number", unique = true)
// Statement: })
})
// Class declaration: defines a new type.
public class User {

    // Annotation: adds metadata used by frameworks/tools.
    @Id
    // Annotation: adds metadata used by frameworks/tools.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Field declaration: defines a member variable.
    private Long id;

    // Annotation: adds metadata used by frameworks/tools.
    @NotBlank(message = "Email is required")
    // Annotation: adds metadata used by frameworks/tools.
    @Email(message = "Valid email is required")
    // Annotation: adds metadata used by frameworks/tools.
    @Column(nullable = false, unique = true, length = 255)
    // Field declaration: defines a member variable.
    private String email;

    // Annotation: adds metadata used by frameworks/tools.
    @NotBlank(message = "Mobile number is required")
    // Annotation: adds metadata used by frameworks/tools.
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile must be 10 digits")
    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "mobile_number", nullable = false, unique = true, length = 10)
    // Field declaration: defines a member variable.
    private String mobileNumber;

    // Annotation: adds metadata used by frameworks/tools.
    @NotBlank(message = "Password is required")
    // Annotation: adds metadata used by frameworks/tools.
    @Size(min = 8, message = "Password must be at least 8 characters")
    // Annotation: adds metadata used by frameworks/tools.
    @Column(nullable = false, length = 255)
    // Field declaration: defines a member variable.
    private String password;

    // Annotation: adds metadata used by frameworks/tools.
    @NotBlank(message = "Full name is required")
    // Annotation: adds metadata used by frameworks/tools.
    @Size(max = 100)
    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "full_name", nullable = false, length = 100)
    // Field declaration: defines a member variable.
    private String fullName;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "email_verified")
    // Field declaration: defines a member variable.
    private boolean emailVerified = false;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "mobile_verified")
    // Field declaration: defines a member variable.
    private boolean mobileVerified = false;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "is_active")
    // Field declaration: defines a member variable.
    private boolean active = true;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "created_at")
    // Field declaration: defines a member variable.
    private LocalDateTime createdAt;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "updated_at")
    // Field declaration: defines a member variable.
    private LocalDateTime updatedAt;

    // Annotation: adds metadata used by frameworks/tools.
    @ElementCollection(fetch = FetchType.EAGER)
    // Annotation: adds metadata used by frameworks/tools.
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "role")
    // Method or constructor declaration.
    private Set<String> roles = new HashSet<>();

    // Annotation: adds metadata used by frameworks/tools.
    @PrePersist
    // Opens a method/constructor/block.
    protected void onCreate() {
        // Statement: createdAt = LocalDateTime.now();
        createdAt = LocalDateTime.now();
        // Statement: updatedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    // Closes the current code block.
    }

    // Annotation: adds metadata used by frameworks/tools.
    @PreUpdate
    // Opens a method/constructor/block.
    protected void onUpdate() {
        // Statement: updatedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Constructors
    // Opens a method/constructor/block.
    public User() {
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public User(String email, String mobileNumber, String password, String fullName) {
        // Uses current object (this) to access a field or method.
        this.email = email;
        // Uses current object (this) to access a field or method.
        this.mobileNumber = mobileNumber;
        // Uses current object (this) to access a field or method.
        this.password = password;
        // Uses current object (this) to access a field or method.
        this.fullName = fullName;
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Getters and Setters
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
    public String getEmail() {
        // Return: sends a value back to the caller.
        return email;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setEmail(String email) {
        // Uses current object (this) to access a field or method.
        this.email = email;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public String getMobileNumber() {
        // Return: sends a value back to the caller.
        return mobileNumber;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setMobileNumber(String mobileNumber) {
        // Uses current object (this) to access a field or method.
        this.mobileNumber = mobileNumber;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public String getPassword() {
        // Return: sends a value back to the caller.
        return password;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setPassword(String password) {
        // Uses current object (this) to access a field or method.
        this.password = password;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public String getFullName() {
        // Return: sends a value back to the caller.
        return fullName;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setFullName(String fullName) {
        // Uses current object (this) to access a field or method.
        this.fullName = fullName;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public boolean isEmailVerified() {
        // Return: sends a value back to the caller.
        return emailVerified;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setEmailVerified(boolean emailVerified) {
        // Uses current object (this) to access a field or method.
        this.emailVerified = emailVerified;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public boolean isMobileVerified() {
        // Return: sends a value back to the caller.
        return mobileVerified;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setMobileVerified(boolean mobileVerified) {
        // Uses current object (this) to access a field or method.
        this.mobileVerified = mobileVerified;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public boolean isActive() {
        // Return: sends a value back to the caller.
        return active;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setActive(boolean active) {
        // Uses current object (this) to access a field or method.
        this.active = active;
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
    public LocalDateTime getUpdatedAt() {
        // Return: sends a value back to the caller.
        return updatedAt;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setUpdatedAt(LocalDateTime updatedAt) {
        // Uses current object (this) to access a field or method.
        this.updatedAt = updatedAt;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public Set<String> getRoles() {
        // Return: sends a value back to the caller.
        return roles;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setRoles(Set<String> roles) {
        // Uses current object (this) to access a field or method.
        this.roles = roles;
    // Closes the current code block.
    }
// Closes the current code block.
}
