/*
 * File: src/main/java/com/example/demo/entity/EnrollmentDocument.java
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
@Table(name = "enrollment_documents", indexes = {
        @Index(name = "idx_enrollment_document_enrollment", columnList = "enrollment_id")
})
// Class Summary: Entity class that is a JPA entity mapped to a database table.
public class EnrollmentDocument {

    public enum DocumentType {
        SSC_MARKSHEET,
        HSC_MARKSHEET,
        SCHOOL_LEAVING_CERTIFICATE,
        BACHELOR_SEMESTER_MARKSHEET,
        DEGREE_CERTIFICATE,
        MARKSHEET,
        ID_PROOF,
        ADDRESS_PROOF,
        PASSPORT_PHOTO,
        CASTE_CERTIFICATE,
        INCOME_CERTIFICATE,
        TRANSFER_CERTIFICATE,
        OTHER
    }

// @Id marks the primary key field for the entity.
    @Id
// @GeneratedValue lets JPA auto-generate primary key values.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
// Field: stores id for this class.
    private Long id;

// @ManyToOne maps a many-to-one relationship between entities.
    @ManyToOne(optional = false)
// @JoinColumn defines the foreign key column for a relationship.
    @JoinColumn(name = "enrollment_id", nullable = false)
// Field: stores enrollment for this class.
    private Enrollment enrollment;

// @Enumerated tells JPA how to store enum values in the database.
    @Enumerated(EnumType.STRING)
// @Column customizes the database column mapping.
    @Column(name = "document_type", nullable = false, length = 50)
// Field: stores documentType for this class.
    private DocumentType documentType;

// @Column customizes the database column mapping.
    @Column(name = "file_name", nullable = false, length = 255)
// Field: stores fileName for this class.
    private String fileName;

// @Column customizes the database column mapping.
    @Column(name = "file_path", nullable = false, length = 500)
// Field: stores filePath for this class.
    private String filePath;

// @Column customizes the database column mapping.
    @Column(name = "content_type", length = 100)
// Field: stores contentType for this class.
    private String contentType;

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
    public Enrollment getEnrollment() {
        return enrollment;
    }

// Method: performs a focused unit of work in this class.
    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

// Method: performs a focused unit of work in this class.
    public DocumentType getDocumentType() {
        return documentType;
    }

// Method: performs a focused unit of work in this class.
    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
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
    public String getContentType() {
        return contentType;
    }

// Method: performs a focused unit of work in this class.
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

// Method: performs a focused unit of work in this class.
    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}
