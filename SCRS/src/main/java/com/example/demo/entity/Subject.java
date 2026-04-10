/*
 * File: src/main/java/com/example/demo/entity/Subject.java
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
@Table(name = "subjects", uniqueConstraints = {
        @UniqueConstraint(name = "uk_subject_department_code", columnNames = {"department", "subject_code"})
}, indexes = {
        @Index(name = "idx_subject_department", columnList = "department")
})
// Class Summary: Entity class that is a JPA entity mapped to a database table.
public class Subject {

// @Id marks the primary key field for the entity.
    @Id
// @GeneratedValue lets JPA auto-generate primary key values.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
// Field: stores id for this class.
    private Long id;

// @Column customizes the database column mapping.
    @Column(nullable = false, length = 100)
// Field: stores department for this class.
    private String department;

// @Column customizes the database column mapping.
    @Column(name = "program_name", nullable = false, length = 100)
// Field: stores programName for this class.
    private String programName;

// @Column customizes the database column mapping.
    @Column(name = "subject_code", nullable = false, length = 50)
// Field: stores subjectCode for this class.
    private String subjectCode;

// @Column customizes the database column mapping.
    @Column(name = "subject_name", nullable = false, length = 255)
// Field: stores subjectName for this class.
    private String subjectName;

// @Column customizes the database column mapping.
    @Column(name = "semester")
// Field: stores semester for this class.
    private Integer semester;

// @Column customizes the database column mapping.
    @Column(name = "credits")
// Field: stores credits for this class.
    private Integer credits;

// @ManyToOne maps a many-to-one relationship between entities.
    @ManyToOne(fetch = FetchType.LAZY)
// @JoinColumn defines the foreign key column for a relationship.
    @JoinColumn(name = "teaching_schema_id")
// Field: stores teachingSchema for this class.
    private TeachingSchema teachingSchema;

// @Column customizes the database column mapping.
    @Column(name = "created_at", nullable = false, updatable = false)
// Field: stores createdAt for this class.
    private LocalDateTime createdAt;

// @Column customizes the database column mapping.
    @Column(name = "updated_at")
// Field: stores updatedAt for this class.
    private LocalDateTime updatedAt;

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

// Method: performs a focused unit of work in this class.
    public Long getId() {
        return id;
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
    public String getProgramName() {
        return programName;
    }

// Method: performs a focused unit of work in this class.
    public void setProgramName(String programName) {
        this.programName = programName;
    }

// Method: performs a focused unit of work in this class.
    public String getSubjectCode() {
        return subjectCode;
    }

// Method: performs a focused unit of work in this class.
    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

// Method: performs a focused unit of work in this class.
    public String getSubjectName() {
        return subjectName;
    }

// Method: performs a focused unit of work in this class.
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

// Method: performs a focused unit of work in this class.
    public Integer getSemester() {
        return semester;
    }

// Method: performs a focused unit of work in this class.
    public void setSemester(Integer semester) {
        this.semester = semester;
    }

// Method: performs a focused unit of work in this class.
    public Integer getCredits() {
        return credits;
    }

// Method: performs a focused unit of work in this class.
    public void setCredits(Integer credits) {
        this.credits = credits;
    }

// Method: performs a focused unit of work in this class.
    public TeachingSchema getTeachingSchema() {
        return teachingSchema;
    }

// Method: performs a focused unit of work in this class.
    public void setTeachingSchema(TeachingSchema teachingSchema) {
        this.teachingSchema = teachingSchema;
    }

// Method: performs a focused unit of work in this class.
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

// Method: performs a focused unit of work in this class.
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
