package com.example.demo.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name = "courses", indexes = {
    @Index(name = "idx_course_code", columnList = "code")
})
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 50)
    private String code;
    @Column(nullable = false, length = 255)
    private String name;
    @Column(nullable = false, length = 100)
    private String department;
    @Column(name = "program_name", nullable = false, length = 100)
    private String programName;
    @Column(name = "batch_year", nullable = false)
    private Integer batchYear;
    @Column(nullable = false)
    private Integer capacity;
    @Column(name = "remaining_seats", nullable = false)
    private Integer remainingSeats;
    @Column(nullable = false)
    private Integer credits;
    @Column(nullable = false)
    private Integer fee;
    @Column(name = "program_level", nullable = false, length = 20)
    private String programLevel;
    @Column(name = "level", nullable = false, length = 20)
    private String level;
    @Column(name = "duration_semesters", nullable = false)
    private Integer durationSemesters;
    @ManyToOne
    @JoinColumn(name = "teaching_schema_id")
    private TeachingSchema teachingSchema;
    @Column(name = "required_qualification", nullable = false, length = 255)
    private String requiredQualification;
    @ManyToMany
    @JoinTable(name = "course_prerequisites",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "prerequisite_id"))
    private Set<Course> prerequisites = new HashSet<>();
    @Version
    private Long version;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    public Course() {
    }
    public Long getId() {
        // 1. Send the result back to the screen
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCode() {
        // 1. Send the result back to the screen
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getName() {
        // 1. Send the result back to the screen
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDepartment() {
        // 1. Send the result back to the screen
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public String getProgramName() {
        return programName;
    }
    public void setProgramName(String programName) {
        this.programName = programName;
    }
    public Integer getBatchYear() {
        return batchYear;
    }
    public void setBatchYear(Integer batchYear) {
        this.batchYear = batchYear;
    }
    public Integer getCapacity() {
        // 1. Send the result back to the screen
        return capacity;
    }
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    public Integer getRemainingSeats() {
        // 1. Send the result back to the screen
        return remainingSeats;
    }
    public void setRemainingSeats(Integer remainingSeats) {
        this.remainingSeats = remainingSeats;
    }
    public Integer getCredits() {
        // 1. Send the result back to the screen
        return credits;
    }
    public void setCredits(Integer credits) {
        this.credits = credits;
    }
    public Integer getFee() {
        // 1. Send the result back to the screen
        return fee;
    }
    public void setFee(Integer fee) {
        this.fee = fee;
    }
    public String getProgramLevel() {
        // 1. Send the result back to the screen
        return programLevel;
    }
    public void setProgramLevel(String programLevel) {
        this.programLevel = programLevel;
    }
    public String getLevel() {
        // 1. Send the result back to the screen
        return level;
    }
    public void setLevel(String level) {
        this.level = level;
    }
    public Integer getDurationSemesters() {
        // 1. Send the result back to the screen
        return durationSemesters;
    }
    public void setDurationSemesters(Integer durationSemesters) {
        this.durationSemesters = durationSemesters;
    }
    public TeachingSchema getTeachingSchema() {
        return teachingSchema;
    }
    public void setTeachingSchema(TeachingSchema teachingSchema) {
        this.teachingSchema = teachingSchema;
    }
    public String getRequiredQualification() {
        // 1. Send the result back to the screen
        return requiredQualification;
    }
    public void setRequiredQualification(String requiredQualification) {
        this.requiredQualification = requiredQualification;
    }
    public Set<Course> getPrerequisites() {
        // 1. Send the result back to the screen
        return prerequisites;
    }
    public void setPrerequisites(Set<Course> prerequisites) {
        this.prerequisites = prerequisites;
    }
    public Long getVersion() {
        // 1. Send the result back to the screen
        return version;
    }
    public void setVersion(Long version) {
        this.version = version;
    }
    public LocalDateTime getCreatedAt() {
        // 1. Send the result back to the screen
        return createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        // 1. Send the result back to the screen
        return updatedAt;
    }
}
