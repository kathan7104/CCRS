package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments", uniqueConstraints = {@UniqueConstraint(columnNames = {"student_id", "course_id"})},
        indexes = {@Index(name = "idx_enrollment_student", columnList = "student_id"),
                @Index(name = "idx_enrollment_course", columnList = "course_id")})
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EnrollmentStatus status = EnrollmentStatus.PENDING;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    @Column(name = "finalized_at")
    private LocalDateTime finalizedAt;

    @Column(length = 500)
    private String comments;

    @Column(name = "past_education_marks")
    private Double pastEducationMarks;

    @Column(name = "marksheet_path")
    private String marksheetPath;
    
    @Column(name = "personal_info", length = 1000)
    private String personalInfo; // Stored as JSON or text

    public enum EnrollmentStatus {
        PENDING,
        ENROLLED,
        WAITLISTED,
        CANCELLED,
        COMPLETED
    }

    @PrePersist
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
    }

    // Constructors, getters and setters
    public Enrollment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public LocalDateTime getFinalizedAt() {
        return finalizedAt;
    }

    public void setFinalizedAt(LocalDateTime finalizedAt) {
        this.finalizedAt = finalizedAt;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Double getPastEducationMarks() {
        return pastEducationMarks;
    }

    public void setPastEducationMarks(Double pastEducationMarks) {
        this.pastEducationMarks = pastEducationMarks;
    }

    public String getMarksheetPath() {
        return marksheetPath;
    }

    public void setMarksheetPath(String marksheetPath) {
        this.marksheetPath = marksheetPath;
    }

    public String getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(String personalInfo) {
        this.personalInfo = personalInfo;
    }
}
