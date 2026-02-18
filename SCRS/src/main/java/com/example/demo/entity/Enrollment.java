package com.example.demo.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnrollmentDocument> documents = new ArrayList<>();
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
    public Enrollment() {
    }
    public Long getId() {
        // 1. Send the result back to the screen
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public User getStudent() {
        // 1. Send the result back to the screen
        return student;
    }
    public void setStudent(User student) {
        this.student = student;
    }
    public Course getCourse() {
        // 1. Send the result back to the screen
        return course;
    }
    public void setCourse(Course course) {
        this.course = course;
    }
    public EnrollmentStatus getStatus() {
        // 1. Send the result back to the screen
        return status;
    }
    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }
    public LocalDateTime getRegisteredAt() {
        // 1. Send the result back to the screen
        return registeredAt;
    }
    public LocalDateTime getFinalizedAt() {
        // 1. Send the result back to the screen
        return finalizedAt;
    }
    public void setFinalizedAt(LocalDateTime finalizedAt) {
        this.finalizedAt = finalizedAt;
    }
    public String getComments() {
        // 1. Send the result back to the screen
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    public Double getPastEducationMarks() {
        // 1. Send the result back to the screen
        return pastEducationMarks;
    }
    public void setPastEducationMarks(Double pastEducationMarks) {
        this.pastEducationMarks = pastEducationMarks;
    }
    public String getMarksheetPath() {
        // 1. Send the result back to the screen
        return marksheetPath;
    }
    public void setMarksheetPath(String marksheetPath) {
        this.marksheetPath = marksheetPath;
    }
    public String getPersonalInfo() {
        // 1. Send the result back to the screen
        return personalInfo;
    }
    public void setPersonalInfo(String personalInfo) {
        this.personalInfo = personalInfo;
    }
    public List<EnrollmentDocument> getDocuments() {
        return documents;
    }
    public void addDocument(EnrollmentDocument document) {
        document.setEnrollment(this);
        this.documents.add(document);
    }
}
