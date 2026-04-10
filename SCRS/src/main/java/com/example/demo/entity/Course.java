/*
 * File: src/main/java/com/example/demo/entity/Course.java
 * Role: Entity
 * MVC Fit: JPA entity that maps to a database table.
 * Connects To: Repository reads/writes this entity
 */

package com.example.demo.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
// @Entity maps this class to a database table managed by JPA.
@Entity
// @Table specifies the database table name for this entity.
@Table(name = "courses", indexes = {
    @Index(name = "idx_course_code", columnList = "code")
})
// Class Summary: Entity class that is a JPA entity mapped to a database table.
public class Course {
// @Id marks the primary key field for the entity.
    @Id
// @GeneratedValue lets JPA auto-generate primary key values.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
// Field: stores id for this class.
    private Long id;
// @Column customizes the database column mapping.
    @Column(nullable = false, unique = true, length = 50)
// Field: stores code for this class.
    private String code;
// @Column customizes the database column mapping.
    @Column(nullable = false, length = 255)
// Field: stores name for this class.
    private String name;
// @Column customizes the database column mapping.
    @Column(nullable = false, length = 100)
// Field: stores department for this class.
    private String department;
// @Column customizes the database column mapping.
    @Column(name = "program_name", nullable = false, length = 100)
// Field: stores programName for this class.
    private String programName;
// @Column customizes the database column mapping.
    @Column(name = "batch_year", nullable = false)
// Field: stores batchYear for this class.
    private Integer batchYear;
// @Column customizes the database column mapping.
    @Column(nullable = false)
// Field: stores capacity for this class.
    private Integer capacity;
// @Column customizes the database column mapping.
    @Column(name = "remaining_seats", nullable = false)
// Field: stores remainingSeats for this class.
    private Integer remainingSeats;
// @Column customizes the database column mapping.
    @Column(nullable = false)
// Field: stores credits for this class.
    private Integer credits;
// @Column customizes the database column mapping.
    @Column(nullable = false)
// Field: stores fee for this class.
    private Integer fee;
// @Column customizes the database column mapping.
    @Column(name = "program_level", nullable = false, length = 20)
// Field: stores programLevel for this class.
    private String programLevel;
// @Column customizes the database column mapping.
    @Column(name = "level", nullable = false, length = 20)
// Field: stores level for this class.
    private String level;
// @Column customizes the database column mapping.
    @Column(name = "duration_semesters", nullable = false)
// Field: stores durationSemesters for this class.
    private Integer durationSemesters;
// @ManyToOne maps a many-to-one relationship between entities.
    @ManyToOne
// @JoinColumn defines the foreign key column for a relationship.
    @JoinColumn(name = "teaching_schema_id")
// Field: stores teachingSchema for this class.
    private TeachingSchema teachingSchema;
// @Column customizes the database column mapping.
    @Column(name = "required_qualification", nullable = false, length = 255)
// Field: stores requiredQualification for this class.
    private String requiredQualification;
// @Column customizes the database column mapping.
    @Column(name = "required_document_types", length = 500)
// Field: stores requiredDocumentTypes for this class.
    private String requiredDocumentTypes;
// @ManyToMany maps a many-to-many relationship between entities.
    @ManyToMany
// @JoinTable defines a join table for a many-to-many relationship.
    @JoinTable(name = "course_prerequisites",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "prerequisite_id"))
// Field: stores prerequisites for this class.
// Method: performs a focused unit of work in this class.
    private Set<Course> prerequisites = new HashSet<>();
    @Version
// Field: stores version for this class.
    private Long version;
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
// Constructor: Spring injects dependencies here.
    public Course() {
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
    public String getCode() {
        // 1. Send the result back to the screen
        return code;
    }
// Method: performs a focused unit of work in this class.
    public void setCode(String code) {
        this.code = code;
    }
// Method: performs a focused unit of work in this class.
    public String getName() {
        // 1. Send the result back to the screen
        return name;
    }
// Method: performs a focused unit of work in this class.
    public void setName(String name) {
        this.name = name;
    }
// Method: performs a focused unit of work in this class.
    public String getDepartment() {
        // 1. Send the result back to the screen
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
    public Integer getBatchYear() {
        return batchYear;
    }
// Method: performs a focused unit of work in this class.
    public void setBatchYear(Integer batchYear) {
        this.batchYear = batchYear;
    }
// Method: performs a focused unit of work in this class.
    public Integer getCapacity() {
        // 1. Send the result back to the screen
        return capacity;
    }
// Method: performs a focused unit of work in this class.
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
// Method: performs a focused unit of work in this class.
    public Integer getRemainingSeats() {
        // 1. Send the result back to the screen
        return remainingSeats;
    }
// Method: performs a focused unit of work in this class.
    public void setRemainingSeats(Integer remainingSeats) {
        this.remainingSeats = remainingSeats;
    }
// Method: performs a focused unit of work in this class.
    public Integer getCredits() {
        // 1. Send the result back to the screen
        return credits;
    }
// Method: performs a focused unit of work in this class.
    public void setCredits(Integer credits) {
        this.credits = credits;
    }
// Method: performs a focused unit of work in this class.
    public Integer getFee() {
        // 1. Send the result back to the screen
        return fee;
    }
// Method: performs a focused unit of work in this class.
    public void setFee(Integer fee) {
        this.fee = fee;
    }
// Method: performs a focused unit of work in this class.
    public String getProgramLevel() {
        // 1. Send the result back to the screen
        return programLevel;
    }
// Method: performs a focused unit of work in this class.
    public void setProgramLevel(String programLevel) {
        this.programLevel = programLevel;
    }
// Method: performs a focused unit of work in this class.
    public String getLevel() {
        // 1. Send the result back to the screen
        return level;
    }
// Method: performs a focused unit of work in this class.
    public void setLevel(String level) {
        this.level = level;
    }
// Method: performs a focused unit of work in this class.
    public Integer getDurationSemesters() {
        // 1. Send the result back to the screen
        return durationSemesters;
    }
// Method: performs a focused unit of work in this class.
    public void setDurationSemesters(Integer durationSemesters) {
        this.durationSemesters = durationSemesters;
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
    public String getRequiredQualification() {
        // 1. Send the result back to the screen
        return requiredQualification;
    }
// Method: performs a focused unit of work in this class.
    public void setRequiredQualification(String requiredQualification) {
        this.requiredQualification = requiredQualification;
    }
// Method: performs a focused unit of work in this class.
    public String getRequiredDocumentTypes() {
        return requiredDocumentTypes;
    }
// Method: performs a focused unit of work in this class.
    public void setRequiredDocumentTypes(String requiredDocumentTypes) {
        this.requiredDocumentTypes = requiredDocumentTypes;
    }
// Method: performs a focused unit of work in this class.
    public List<String> getRequiredDocumentTypeList() {
        if (requiredDocumentTypes == null || requiredDocumentTypes.isBlank()) {
            return List.of();
        }
        String[] tokens = requiredDocumentTypes.split(",");
        List<String> result = new ArrayList<>();
        for (String token : tokens) {
            String normalized = token == null ? "" : token.trim().toUpperCase(Locale.ROOT);
            if (!normalized.isBlank()) {
                result.add(normalized);
            }
        }
        return result;
    }
// Method: performs a focused unit of work in this class.
    public void setRequiredDocumentTypeList(List<String> values) {
        if (values == null || values.isEmpty()) {
            this.requiredDocumentTypes = null;
            return;
        }
        Set<String> normalized = new LinkedHashSet<>();
        for (String value : values) {
            String cleaned = value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
            if (!cleaned.isBlank()) {
                normalized.add(cleaned);
            }
        }
        this.requiredDocumentTypes = normalized.isEmpty() ? null : String.join(",", normalized);
    }
// Method: performs a focused unit of work in this class.
    public Set<Course> getPrerequisites() {
        // 1. Send the result back to the screen
        return prerequisites;
    }
// Method: performs a focused unit of work in this class.
    public void setPrerequisites(Set<Course> prerequisites) {
        this.prerequisites = prerequisites;
    }
// Method: performs a focused unit of work in this class.
    public Long getVersion() {
        // 1. Send the result back to the screen
        return version;
    }
// Method: performs a focused unit of work in this class.
    public void setVersion(Long version) {
        this.version = version;
    }
// Method: performs a focused unit of work in this class.
    public LocalDateTime getCreatedAt() {
        // 1. Send the result back to the screen
        return createdAt;
    }
// Method: performs a focused unit of work in this class.
    public LocalDateTime getUpdatedAt() {
        // 1. Send the result back to the screen
        return updatedAt;
    }
}
