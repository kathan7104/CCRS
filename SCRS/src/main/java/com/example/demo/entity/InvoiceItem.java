/*
 * File: src/main/java/com/example/demo/entity/InvoiceItem.java
 * Role: Entity
 * MVC Fit: JPA entity that maps to a database table.
 * Connects To: Repository reads/writes this entity
 */

package com.example.demo.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
// Class Summary: Entity class that is a JPA entity mapped to a database table.
// @Entity maps this class to a database table managed by JPA.
@Entity
// @Table specifies the database table name for this entity.
@Table(name = "invoice_items", indexes = {@Index(name = "idx_invoice_item_invoice", columnList = "invoice_id")})
public class InvoiceItem {
// @Id marks the primary key field for the entity.
    @Id
// @GeneratedValue lets JPA auto-generate primary key values.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
// Field: stores id for this class.
    private Long id;
// @ManyToOne maps a many-to-one relationship between entities.
    @ManyToOne(optional = false)
// @JoinColumn defines the foreign key column for a relationship.
    @JoinColumn(name = "invoice_id", nullable = false)
// Field: stores invoice for this class.
    private Invoice invoice;
// @ManyToOne maps a many-to-one relationship between entities.
    @ManyToOne
// @JoinColumn defines the foreign key column for a relationship.
    @JoinColumn(name = "course_id")
// Field: stores course for this class.
    private Course course;
// @Column customizes the database column mapping.
    @Column(length = 255)
// Field: stores description for this class.
    private String description;
// @Column customizes the database column mapping.
    @Column(nullable = false, precision = 16, scale = 2)
// Field: stores amount for this class.
    private BigDecimal amount = BigDecimal.ZERO;
// Constructor: Spring injects dependencies here.
    public InvoiceItem() {
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
    public Invoice getInvoice() {
        // 1. Send the result back to the screen
        return invoice;
    }
// Method: performs a focused unit of work in this class.
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
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
    public String getDescription() {
        // 1. Send the result back to the screen
        return description;
    }
// Method: performs a focused unit of work in this class.
    public void setDescription(String description) {
        this.description = description;
    }
// Method: performs a focused unit of work in this class.
    public java.math.BigDecimal getAmount() {
        // 1. Send the result back to the screen
        return amount;
    }
// Method: performs a focused unit of work in this class.
    public void setAmount(java.math.BigDecimal amount) {
        this.amount = amount;
    }
}
