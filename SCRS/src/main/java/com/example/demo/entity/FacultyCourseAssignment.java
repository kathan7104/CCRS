/*
 * File: src/main/java/com/example/demo/entity/FacultyCourseAssignment.java
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
@Table(
    name = "faculty_course_assignments",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"faculty_id", "course_id"})}
)
// Class Summary: Entity class that is a JPA entity mapped to a database table.
public class FacultyCourseAssignment {
// @Id marks the primary key field for the entity.
    @Id
// @GeneratedValue lets JPA auto-generate primary key values.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
// Field: stores id for this class.
    private Long id;

// @ManyToOne maps a many-to-one relationship between entities.
    @ManyToOne(optional = false)
// @JoinColumn defines the foreign key column for a relationship.
    @JoinColumn(name = "faculty_id", nullable = false)
// Field: stores faculty for this class.
    private User faculty;

// @ManyToOne maps a many-to-one relationship between entities.
    @ManyToOne(optional = false)
// @JoinColumn defines the foreign key column for a relationship.
    @JoinColumn(name = "course_id", nullable = false)
// Field: stores course for this class.
    private Course course;

// @Column customizes the database column mapping.
    @Column(name = "assigned_at", nullable = false)
// Field: stores assignedAt for this class.
    private LocalDateTime assignedAt;

    @PrePersist
// Method: performs a focused unit of work in this class.
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
    }

// Method: performs a focused unit of work in this class.
    public Long getId() {
        return id;
    }

// Method: performs a focused unit of work in this class.
    public User getFaculty() {
        return faculty;
    }

// Method: performs a focused unit of work in this class.
    public void setFaculty(User faculty) {
        this.faculty = faculty;
    }

// Method: performs a focused unit of work in this class.
    public Course getCourse() {
        return course;
    }

// Method: performs a focused unit of work in this class.
    public void setCourse(Course course) {
        this.course = course;
    }

// Method: performs a focused unit of work in this class.
    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }
}
