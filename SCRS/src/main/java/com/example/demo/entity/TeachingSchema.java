/*
 * File: src/main/java/com/example/demo/entity/TeachingSchema.java
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
@Table(name = "teaching_schemas", indexes = {
        @Index(name = "idx_teaching_schema_department_program", columnList = "department,program_name")
})
// Class Summary: Entity class that is a JPA entity mapped to a database table.
public class TeachingSchema {

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
    @Column(name = "schema_version", nullable = false)
// Field: stores schemaVersion for this class.
    private Integer schemaVersion;

// @Column customizes the database column mapping.
    @Column(name = "file_name", nullable = false, length = 255)
// Field: stores fileName for this class.
    private String fileName;

// @Column customizes the database column mapping.
    @Column(name = "file_path", nullable = false, length = 500)
// Field: stores filePath for this class.
    private String filePath;

// @Column customizes the database column mapping.
    @Column(name = "uploaded_at", nullable = false)
// Field: stores uploadedAt for this class.
    private LocalDateTime uploadedAt;

    @PrePersist
// Method: performs a focused unit of work in this class.
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
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
    public Integer getSchemaVersion() {
        return schemaVersion;
    }

// Method: performs a focused unit of work in this class.
    public void setSchemaVersion(Integer schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

// Method: performs a focused unit of work in this class.
    public String getFileName() {
        return fileName;
    }

// Method: performs a focused unit of work in this class.
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

// Method: performs a focused unit of work in this class.
    public String getFilePath() {
        return filePath;
    }

// Method: performs a focused unit of work in this class.
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

// Method: performs a focused unit of work in this class.
    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}
