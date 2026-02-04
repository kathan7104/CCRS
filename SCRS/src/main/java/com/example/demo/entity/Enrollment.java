// Package declaration: groups related classes in a namespace.
package com.example.demo.entity;

// Import statement: brings a class into scope by name.
import jakarta.persistence.*;
// Import statement: brings a class into scope by name.
import java.time.LocalDateTime;

// Annotation: adds metadata used by frameworks/tools.
@Entity
// Annotation: adds metadata used by frameworks/tools.
@Table(name = "enrollments", uniqueConstraints = {@UniqueConstraint(columnNames = {"student_id", "course_id"})},
        // Statement: indexes = {@Index(name = "idx_enrollment_student", columnList = "student_id"),
        indexes = {@Index(name = "idx_enrollment_student", columnList = "student_id"),
                // Annotation: adds metadata used by frameworks/tools.
                @Index(name = "idx_enrollment_course", columnList = "course_id")})
// Class declaration: defines a new type.
public class Enrollment {

    // Annotation: adds metadata used by frameworks/tools.
    @Id
    // Annotation: adds metadata used by frameworks/tools.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Field declaration: defines a member variable.
    private Long id;

    // Annotation: adds metadata used by frameworks/tools.
    @ManyToOne(optional = false)
    // Annotation: adds metadata used by frameworks/tools.
    @JoinColumn(name = "student_id", nullable = false)
    // Field declaration: defines a member variable.
    private User student;

    // Annotation: adds metadata used by frameworks/tools.
    @ManyToOne(optional = false)
    // Annotation: adds metadata used by frameworks/tools.
    @JoinColumn(name = "course_id", nullable = false)
    // Field declaration: defines a member variable.
    private Course course;

    // Annotation: adds metadata used by frameworks/tools.
    @Enumerated(EnumType.STRING)
    // Annotation: adds metadata used by frameworks/tools.
    @Column(nullable = false, length = 20)
    // Field declaration: defines a member variable.
    private EnrollmentStatus status = EnrollmentStatus.PENDING;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "registered_at", nullable = false)
    // Field declaration: defines a member variable.
    private LocalDateTime registeredAt;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "finalized_at")
    // Field declaration: defines a member variable.
    private LocalDateTime finalizedAt;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(length = 500)
    // Field declaration: defines a member variable.
    private String comments;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "past_education_marks")
    // Field declaration: defines a member variable.
    private Double pastEducationMarks;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "marksheet_path")
    // Field declaration: defines a member variable.
    private String marksheetPath;
    
    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "personal_info", length = 1000)
    // Field declaration: defines a member variable.
    private String personalInfo; // Stored as JSON or text

    // Enum declaration: defines a fixed set of constants.
    public enum EnrollmentStatus {
        // Statement: PENDING,
        PENDING,
        // Statement: ENROLLED,
        ENROLLED,
        // Statement: WAITLISTED,
        WAITLISTED,
        // Statement: CANCELLED,
        CANCELLED,
        // Statement: COMPLETED
        COMPLETED
    // Closes the current code block.
    }

    // Annotation: adds metadata used by frameworks/tools.
    @PrePersist
    // Opens a method/constructor/block.
    protected void onCreate() {
        // Statement: registeredAt = LocalDateTime.now();
        registeredAt = LocalDateTime.now();
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Constructors, getters and setters
    // Opens a method/constructor/block.
    public Enrollment() {
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
    public User getStudent() {
        // Return: sends a value back to the caller.
        return student;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setStudent(User student) {
        // Uses current object (this) to access a field or method.
        this.student = student;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public Course getCourse() {
        // Return: sends a value back to the caller.
        return course;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setCourse(Course course) {
        // Uses current object (this) to access a field or method.
        this.course = course;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public EnrollmentStatus getStatus() {
        // Return: sends a value back to the caller.
        return status;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setStatus(EnrollmentStatus status) {
        // Uses current object (this) to access a field or method.
        this.status = status;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public LocalDateTime getRegisteredAt() {
        // Return: sends a value back to the caller.
        return registeredAt;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public LocalDateTime getFinalizedAt() {
        // Return: sends a value back to the caller.
        return finalizedAt;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setFinalizedAt(LocalDateTime finalizedAt) {
        // Uses current object (this) to access a field or method.
        this.finalizedAt = finalizedAt;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public String getComments() {
        // Return: sends a value back to the caller.
        return comments;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setComments(String comments) {
        // Uses current object (this) to access a field or method.
        this.comments = comments;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public Double getPastEducationMarks() {
        // Return: sends a value back to the caller.
        return pastEducationMarks;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setPastEducationMarks(Double pastEducationMarks) {
        // Uses current object (this) to access a field or method.
        this.pastEducationMarks = pastEducationMarks;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public String getMarksheetPath() {
        // Return: sends a value back to the caller.
        return marksheetPath;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setMarksheetPath(String marksheetPath) {
        // Uses current object (this) to access a field or method.
        this.marksheetPath = marksheetPath;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public String getPersonalInfo() {
        // Return: sends a value back to the caller.
        return personalInfo;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setPersonalInfo(String personalInfo) {
        // Uses current object (this) to access a field or method.
        this.personalInfo = personalInfo;
    // Closes the current code block.
    }
// Closes the current code block.
}
