// Package declaration: groups related classes in a namespace.
package com.example.demo.entity;

// Import statement: brings a class into scope by name.
import jakarta.persistence.*;
// Import statement: brings a class into scope by name.
import java.time.LocalDateTime;
// Import statement: brings a class into scope by name.
import java.util.HashSet;
// Import statement: brings a class into scope by name.
import java.util.Set;

// Annotation: adds metadata used by frameworks/tools.
@Entity
// Annotation: adds metadata used by frameworks/tools.
@Table(name = "courses", indexes = {
    // Annotation: adds metadata used by frameworks/tools.
    @Index(name = "idx_course_code", columnList = "code")
// Statement: })
})
// Class declaration: defines a new type.
public class Course {

    // Annotation: adds metadata used by frameworks/tools.
    @Id
    // Annotation: adds metadata used by frameworks/tools.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Field declaration: defines a member variable.
    private Long id;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(nullable = false, unique = true, length = 50)
    // Field declaration: defines a member variable.
    private String code;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(nullable = false, length = 255)
    // Field declaration: defines a member variable.
    private String name;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(nullable = false, length = 100)
    // Field declaration: defines a member variable.
    private String department;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(nullable = false)
    // Field declaration: defines a member variable.
    private Integer capacity;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "remaining_seats", nullable = false)
    // Field declaration: defines a member variable.
    private Integer remainingSeats;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(nullable = false)
    // Field declaration: defines a member variable.
    private Integer credits;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(nullable = false)
    // Field declaration: defines a member variable.
    private Integer fee;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "program_level", nullable = false, length = 20)
    // Field declaration: defines a member variable.
    private String programLevel;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "level", nullable = false, length = 20)
    // Field declaration: defines a member variable.
    private String level;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "duration_years", nullable = false)
    // Field declaration: defines a member variable.
    private Integer durationYears;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "required_qualification", nullable = false, length = 255)
    // Field declaration: defines a member variable.
    private String requiredQualification;

    // Annotation: adds metadata used by frameworks/tools.
    @ManyToMany
    // Annotation: adds metadata used by frameworks/tools.
    @JoinTable(name = "course_prerequisites",
            // Statement: joinColumns = @JoinColumn(name = "course_id"),
            joinColumns = @JoinColumn(name = "course_id"),
            // Statement: inverseJoinColumns = @JoinColumn(name = "prerequisite_id"))
            inverseJoinColumns = @JoinColumn(name = "prerequisite_id"))
    // Method or constructor declaration.
    private Set<Course> prerequisites = new HashSet<>();

    // Annotation: adds metadata used by frameworks/tools.
    @Version
    // Field declaration: defines a member variable.
    private Long version;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "created_at", nullable = false, updatable = false)
    // Field declaration: defines a member variable.
    private LocalDateTime createdAt;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "updated_at")
    // Field declaration: defines a member variable.
    private LocalDateTime updatedAt;

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
    // Getters and setters
    // Opens a method/constructor/block.
    public Course() {
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
    public String getCode() {
        // Return: sends a value back to the caller.
        return code;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setCode(String code) {
        // Uses current object (this) to access a field or method.
        this.code = code;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public String getName() {
        // Return: sends a value back to the caller.
        return name;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setName(String name) {
        // Uses current object (this) to access a field or method.
        this.name = name;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public String getDepartment() {
        // Return: sends a value back to the caller.
        return department;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setDepartment(String department) {
        // Uses current object (this) to access a field or method.
        this.department = department;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public Integer getCapacity() {
        // Return: sends a value back to the caller.
        return capacity;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setCapacity(Integer capacity) {
        // Uses current object (this) to access a field or method.
        this.capacity = capacity;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public Integer getRemainingSeats() {
        // Return: sends a value back to the caller.
        return remainingSeats;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setRemainingSeats(Integer remainingSeats) {
        // Uses current object (this) to access a field or method.
        this.remainingSeats = remainingSeats;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public Integer getCredits() {
        // Return: sends a value back to the caller.
        return credits;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setCredits(Integer credits) {
        // Uses current object (this) to access a field or method.
        this.credits = credits;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public Integer getFee() {
        // Return: sends a value back to the caller.
        return fee;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setFee(Integer fee) {
        // Uses current object (this) to access a field or method.
        this.fee = fee;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public String getProgramLevel() {
        // Return: sends a value back to the caller.
        return programLevel;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setProgramLevel(String programLevel) {
        // Uses current object (this) to access a field or method.
        this.programLevel = programLevel;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public String getLevel() {
        // Return: sends a value back to the caller.
        return level;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setLevel(String level) {
        // Uses current object (this) to access a field or method.
        this.level = level;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public Integer getDurationYears() {
        // Return: sends a value back to the caller.
        return durationYears;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setDurationYears(Integer durationYears) {
        // Uses current object (this) to access a field or method.
        this.durationYears = durationYears;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public String getRequiredQualification() {
        // Return: sends a value back to the caller.
        return requiredQualification;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setRequiredQualification(String requiredQualification) {
        // Uses current object (this) to access a field or method.
        this.requiredQualification = requiredQualification;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public Set<Course> getPrerequisites() {
        // Return: sends a value back to the caller.
        return prerequisites;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setPrerequisites(Set<Course> prerequisites) {
        // Uses current object (this) to access a field or method.
        this.prerequisites = prerequisites;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public Long getVersion() {
        // Return: sends a value back to the caller.
        return version;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setVersion(Long version) {
        // Uses current object (this) to access a field or method.
        this.version = version;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public LocalDateTime getCreatedAt() {
        // Return: sends a value back to the caller.
        return createdAt;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public LocalDateTime getUpdatedAt() {
        // Return: sends a value back to the caller.
        return updatedAt;
    // Closes the current code block.
    }
// Closes the current code block.
}
