/*
 * File: src/main/java/com/example/demo/entity/Department.java
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
@Table(name = "departments", indexes = {
        @Index(name = "idx_department_name", columnList = "name", unique = true)
})
// Class Summary: Entity class that is a JPA entity mapped to a database table.
public class Department {

// @Id marks the primary key field for the entity.
    @Id
// @GeneratedValue lets JPA auto-generate primary key values.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
// Field: stores id for this class.
    private Long id;

// @Column customizes the database column mapping.
    @Column(nullable = false, unique = true, length = 100)
// Field: stores name for this class.
    private String name;

// @Column customizes the database column mapping.
    @Column(name = "is_active", nullable = false)
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
    public String getName() {
        return name;
    }

// Method: performs a focused unit of work in this class.
    public void setName(String name) {
        this.name = name;
    }

// Method: performs a focused unit of work in this class.
    public boolean isActive() {
        return active;
    }

// Method: performs a focused unit of work in this class.
    public void setActive(boolean active) {
        this.active = active;
    }
}
