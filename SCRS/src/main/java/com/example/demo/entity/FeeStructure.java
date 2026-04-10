/*
 * File: src/main/java/com/example/demo/entity/FeeStructure.java
 * Role: Entity
 * MVC Fit: JPA entity that maps to a database table.
 * Connects To: Repository reads/writes this entity
 */

package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Class Summary: Entity class that is a JPA entity mapped to a database table.
// @Entity maps this class to a database table managed by JPA.
@Entity
// @Table specifies the database table name for this entity.
@Table(name = "fee_structures")
public class FeeStructure {
// @Id marks the primary key field for the entity.
    @Id
// @GeneratedValue lets JPA auto-generate primary key values.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
// Field: stores id for this class.
    private Long id;

// @Column customizes the database column mapping.
    @Column(name = "name", nullable = false, length = 100)
// Field: stores name for this class.
    private String name;

// @Column customizes the database column mapping.
    @Column(name = "cost_per_credit", nullable = false, precision = 16, scale = 2)
// Field: stores costPerCredit for this class.
    private BigDecimal costPerCredit = BigDecimal.ZERO;

// @Column customizes the database column mapping.
    @Column(name = "lab_fee", nullable = false, precision = 16, scale = 2)
// Field: stores labFee for this class.
    private BigDecimal labFee = BigDecimal.ZERO;

// @Column customizes the database column mapping.
    @Column(name = "differential_fee", nullable = false, precision = 16, scale = 2)
// Field: stores differentialFee for this class.
    private BigDecimal differentialFee = BigDecimal.ZERO;

// @Column customizes the database column mapping.
    @Column(name = "late_penalty", nullable = false, precision = 16, scale = 2)
// Field: stores latePenalty for this class.
    private BigDecimal latePenalty = BigDecimal.ZERO;

// @Column customizes the database column mapping.
    @Column(name = "effective_from", nullable = false)
// Field: stores effectiveFrom for this class.
    private LocalDate effectiveFrom;

// @Column customizes the database column mapping.
    @Column(name = "active", nullable = false)
// Field: stores active for this class.
    private boolean active = true;

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
    public void setId(Long id) {
        this.id = id;
    }

// Method: performs a focused unit of work in this class.
    public String getName() {
        return name;
    }

// Method: performs a focused unit of work in this class.
    public void setName(String name) {
        this.name = name;
    }

// Method: performs a focused unit of work in this class.
    public BigDecimal getCostPerCredit() {
        return costPerCredit;
    }

// Method: performs a focused unit of work in this class.
    public void setCostPerCredit(BigDecimal costPerCredit) {
        this.costPerCredit = costPerCredit;
    }

// Method: performs a focused unit of work in this class.
    public BigDecimal getLabFee() {
        return labFee;
    }

// Method: performs a focused unit of work in this class.
    public void setLabFee(BigDecimal labFee) {
        this.labFee = labFee;
    }

// Method: performs a focused unit of work in this class.
    public BigDecimal getDifferentialFee() {
        return differentialFee;
    }

// Method: performs a focused unit of work in this class.
    public void setDifferentialFee(BigDecimal differentialFee) {
        this.differentialFee = differentialFee;
    }

// Method: performs a focused unit of work in this class.
    public BigDecimal getLatePenalty() {
        return latePenalty;
    }

// Method: performs a focused unit of work in this class.
    public void setLatePenalty(BigDecimal latePenalty) {
        this.latePenalty = latePenalty;
    }

// Method: performs a focused unit of work in this class.
    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

// Method: performs a focused unit of work in this class.
    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

// Method: performs a focused unit of work in this class.
    public boolean isActive() {
        return active;
    }

// Method: performs a focused unit of work in this class.
    public void setActive(boolean active) {
        this.active = active;
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
