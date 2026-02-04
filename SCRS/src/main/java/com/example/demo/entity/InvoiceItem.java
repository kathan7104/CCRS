// Package declaration: groups related classes in a namespace.
package com.example.demo.entity;

// Import statement: brings a class into scope by name.
import jakarta.persistence.*;
// Import statement: brings a class into scope by name.
import java.math.BigDecimal;

// Annotation: adds metadata used by frameworks/tools.
@Entity
// Annotation: adds metadata used by frameworks/tools.
@Table(name = "invoice_items", indexes = {@Index(name = "idx_invoice_item_invoice", columnList = "invoice_id")})
// Class declaration: defines a new type.
public class InvoiceItem {

    // Annotation: adds metadata used by frameworks/tools.
    @Id
    // Annotation: adds metadata used by frameworks/tools.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Field declaration: defines a member variable.
    private Long id;

    // Annotation: adds metadata used by frameworks/tools.
    @ManyToOne(optional = false)
    // Annotation: adds metadata used by frameworks/tools.
    @JoinColumn(name = "invoice_id", nullable = false)
    // Field declaration: defines a member variable.
    private Invoice invoice;

    // Annotation: adds metadata used by frameworks/tools.
    @ManyToOne
    // Annotation: adds metadata used by frameworks/tools.
    @JoinColumn(name = "course_id")
    // Field declaration: defines a member variable.
    private Course course;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(length = 255)
    // Field declaration: defines a member variable.
    private String description;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(nullable = false, precision = 16, scale = 2)
    // Field declaration: defines a member variable.
    private BigDecimal amount = BigDecimal.ZERO;

    // Comment: explains code for readers.
    // Constructors, getters and setters
    // Opens a method/constructor/block.
    public InvoiceItem() {
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public Long getId() {
        // Return: sends a value back to the caller.
        return id;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setId(Long id) {
        // Uses current object (this) to access a field or method.
        this.id = id;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public Invoice getInvoice() {
        // Return: sends a value back to the caller.
        return invoice;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setInvoice(Invoice invoice) {
        // Uses current object (this) to access a field or method.
        this.invoice = invoice;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public Course getCourse() {
        // Return: sends a value back to the caller.
        return course;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setCourse(Course course) {
        // Uses current object (this) to access a field or method.
        this.course = course;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public String getDescription() {
        // Return: sends a value back to the caller.
        return description;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setDescription(String description) {
        // Uses current object (this) to access a field or method.
        this.description = description;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public java.math.BigDecimal getAmount() {
        // Return: sends a value back to the caller.
        return amount;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setAmount(java.math.BigDecimal amount) {
        // Uses current object (this) to access a field or method.
        this.amount = amount;
    // Closes the current code block.
    }
// Closes the current code block.
}
