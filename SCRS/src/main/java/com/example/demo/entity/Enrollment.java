/*
 * File: src/main/java/com/example/demo/entity/Enrollment.java
 * Role: Entity
 * MVC Fit: JPA entity that maps to a database table.
 * Connects To: Repository reads/writes this entity
 */

package com.example.demo.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
// @Entity maps this class to a database table managed by JPA.
@Entity
// @Table specifies the database table name for this entity.
@Table(name = "enrollments", uniqueConstraints = {@UniqueConstraint(columnNames = {"student_id", "course_id"})},
        indexes = {@Index(name = "idx_enrollment_student", columnList = "student_id"),
// Class Summary: Entity class that is a JPA entity mapped to a database table.
                @Index(name = "idx_enrollment_course", columnList = "course_id")})
public class Enrollment {
// @Id marks the primary key field for the entity.
    @Id
// @GeneratedValue lets JPA auto-generate primary key values.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
// Field: stores id for this class.
    private Long id;
// @ManyToOne maps a many-to-one relationship between entities.
    @ManyToOne(optional = false)
// @JoinColumn defines the foreign key column for a relationship.
    @JoinColumn(name = "student_id", nullable = false)
// Field: stores student for this class.
    private User student;
// @ManyToOne maps a many-to-one relationship between entities.
    @ManyToOne(optional = false)
// @JoinColumn defines the foreign key column for a relationship.
    @JoinColumn(name = "course_id", nullable = false)
// Field: stores course for this class.
    private Course course;
// @Enumerated tells JPA how to store enum values in the database.
    @Enumerated(EnumType.STRING)
// @Column customizes the database column mapping.
    @Column(nullable = false, length = 20)
// Field: stores status for this class.
    private EnrollmentStatus status = EnrollmentStatus.PENDING;
// @Column customizes the database column mapping.
    @Column(name = "registered_at", nullable = false)
// Field: stores registeredAt for this class.
    private LocalDateTime registeredAt;
// @Column customizes the database column mapping.
    @Column(name = "finalized_at")
// Field: stores finalizedAt for this class.
    private LocalDateTime finalizedAt;
// @Column customizes the database column mapping.
    @Column(length = 500)
// Field: stores comments for this class.
    private String comments;
// @Column customizes the database column mapping.
    @Column(name = "past_education_marks")
// Field: stores pastEducationMarks for this class.
    private Double pastEducationMarks;
// @Column customizes the database column mapping.
    @Column(name = "marksheet_path")
// Field: stores marksheetPath for this class.
    private String marksheetPath;
// @Column customizes the database column mapping.
    @Column(name = "personal_info", length = 1000)
// Field: stores personalInfo for this class.
    private String personalInfo; // Stored as JSON or text
// @OneToMany maps a one-to-many relationship between entities.
    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true)
// Field: stores documents for this class.
// Method: performs a focused unit of work in this class.
    private List<EnrollmentDocument> documents = new ArrayList<>();
    public enum EnrollmentStatus {
        PENDING,
        APPROVED,
        ENROLLED,
        WAITLISTED,
        CANCELLED,
        COMPLETED
    }
    @PrePersist
// Method: performs a focused unit of work in this class.
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
    }
// Constructor: Spring injects dependencies here.
    public Enrollment() {
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
    public User getStudent() {
        // 1. Send the result back to the screen
        return student;
    }
// Method: performs a focused unit of work in this class.
    public void setStudent(User student) {
        this.student = student;
    }
// Method: performs a focused unit of work in this class.
    public Course getCourse() {
        // 1. Send the result back to the screen
        return course;
    }
// Method: performs a focused unit of work in this class.
    public void setCourse(Course course) {
        this.course = course;
    }
// Method: performs a focused unit of work in this class.
    public EnrollmentStatus getStatus() {
        // 1. Send the result back to the screen
        return status;
    }
// Method: performs a focused unit of work in this class.
    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }
// Method: performs a focused unit of work in this class.
    public LocalDateTime getRegisteredAt() {
        // 1. Send the result back to the screen
        return registeredAt;
    }
// Method: performs a focused unit of work in this class.
    public LocalDateTime getFinalizedAt() {
        // 1. Send the result back to the screen
        return finalizedAt;
    }
// Method: performs a focused unit of work in this class.
    public void setFinalizedAt(LocalDateTime finalizedAt) {
        this.finalizedAt = finalizedAt;
    }
// Method: performs a focused unit of work in this class.
    public String getComments() {
        // 1. Send the result back to the screen
        return comments;
    }
// Method: performs a focused unit of work in this class.
    public void setComments(String comments) {
        this.comments = comments;
    }
// Method: performs a focused unit of work in this class.
    public Double getPastEducationMarks() {
        // 1. Send the result back to the screen
        return pastEducationMarks;
    }
// Method: performs a focused unit of work in this class.
    public void setPastEducationMarks(Double pastEducationMarks) {
        this.pastEducationMarks = pastEducationMarks;
    }
// Method: performs a focused unit of work in this class.
    public String getMarksheetPath() {
        // 1. Send the result back to the screen
        return marksheetPath;
    }
// Method: performs a focused unit of work in this class.
    public void setMarksheetPath(String marksheetPath) {
        this.marksheetPath = marksheetPath;
    }
// Method: performs a focused unit of work in this class.
    public String getPersonalInfo() {
        // 1. Send the result back to the screen
        return personalInfo;
    }
// Method: performs a focused unit of work in this class.
    public void setPersonalInfo(String personalInfo) {
        this.personalInfo = personalInfo;
    }
// Method: performs a focused unit of work in this class.
    public List<EnrollmentDocument> getDocuments() {
        return documents;
    }
// Method: performs a focused unit of work in this class.
    public void addDocument(EnrollmentDocument document) {
        document.setEnrollment(this);
        this.documents.add(document);
    }
}
