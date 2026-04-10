/*
 * File: src/main/java/com/example/demo/entity/FeeStructureAuditLog.java
 * Role: Entity
 * MVC Fit: JPA entity that maps to a database table.
 * Connects To: Repository reads/writes this entity
 */

package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Class Summary: Entity class that is a JPA entity mapped to a database table.
// @Entity maps this class to a database table managed by JPA.
@Entity
// @Table specifies the database table name for this entity.
@Table(name = "fee_structure_audit_logs")
public class FeeStructureAuditLog {
// @Id marks the primary key field for the entity.
    @Id
// @GeneratedValue lets JPA auto-generate primary key values.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
// Field: stores id for this class.
    private Long id;

// @ManyToOne maps a many-to-one relationship between entities.
    @ManyToOne
// @JoinColumn defines the foreign key column for a relationship.
    @JoinColumn(name = "fee_structure_id")
// Field: stores feeStructure for this class.
    private FeeStructure feeStructure;

// @Column customizes the database column mapping.
    @Column(name = "action", nullable = false, length = 20)
// Field: stores action for this class.
    private String action;

// @Column customizes the database column mapping.
    @Column(name = "changed_by", nullable = false, length = 255)
// Field: stores changedBy for this class.
    private String changedBy;

// @Column customizes the database column mapping.
    @Column(name = "change_summary", nullable = false, length = 1000)
// Field: stores changeSummary for this class.
    private String changeSummary;

// @Column customizes the database column mapping.
    @Column(name = "changed_at", nullable = false)
// Field: stores changedAt for this class.
    private LocalDateTime changedAt;

    @PrePersist
// Method: performs a focused unit of work in this class.
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }

// Method: performs a focused unit of work in this class.
    public Long getId() {
        return id;
    }

// Method: performs a focused unit of work in this class.
    public FeeStructure getFeeStructure() {
        return feeStructure;
    }

// Method: performs a focused unit of work in this class.
    public void setFeeStructure(FeeStructure feeStructure) {
        this.feeStructure = feeStructure;
    }

// Method: performs a focused unit of work in this class.
    public String getAction() {
        return action;
    }

// Method: performs a focused unit of work in this class.
    public void setAction(String action) {
        this.action = action;
    }

// Method: performs a focused unit of work in this class.
    public String getChangedBy() {
        return changedBy;
    }

// Method: performs a focused unit of work in this class.
    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

// Method: performs a focused unit of work in this class.
    public String getChangeSummary() {
        return changeSummary;
    }

// Method: performs a focused unit of work in this class.
    public void setChangeSummary(String changeSummary) {
        this.changeSummary = changeSummary;
    }

// Method: performs a focused unit of work in this class.
    public LocalDateTime getChangedAt() {
        return changedAt;
    }
}
