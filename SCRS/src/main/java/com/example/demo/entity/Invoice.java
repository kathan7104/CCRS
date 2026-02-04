// Package declaration: groups related classes in a namespace.
package com.example.demo.entity;

// Import statement: brings a class into scope by name.
import jakarta.persistence.*;
// Import statement: brings a class into scope by name.
import java.math.BigDecimal;
// Import statement: brings a class into scope by name.
import java.time.LocalDateTime;
// Import statement: brings a class into scope by name.
import java.util.HashSet;
// Import statement: brings a class into scope by name.
import java.util.Set;

// Annotation: adds metadata used by frameworks/tools.
@Entity
// Annotation: adds metadata used by frameworks/tools.
@Table(name = "invoices", indexes = {@Index(name = "idx_invoice_number", columnList = "invoice_number")})
// Class declaration: defines a new type.
public class Invoice {

    // Annotation: adds metadata used by frameworks/tools.
    @Id
    // Annotation: adds metadata used by frameworks/tools.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Field declaration: defines a member variable.
    private Long id;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "invoice_number", nullable = false, unique = true, length = 100)
    // Field declaration: defines a member variable.
    private String invoiceNumber;

    // Annotation: adds metadata used by frameworks/tools.
    @ManyToOne(optional = false)
    // Annotation: adds metadata used by frameworks/tools.
    @JoinColumn(name = "student_id", nullable = false)
    // Field declaration: defines a member variable.
    private User student;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "total_amount", nullable = false, precision = 16, scale = 2)
    // Field declaration: defines a member variable.
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // Annotation: adds metadata used by frameworks/tools.
    @Enumerated(EnumType.STRING)
    // Annotation: adds metadata used by frameworks/tools.
    @Column(nullable = false, length = 20)
    // Field declaration: defines a member variable.
    private InvoiceStatus status = InvoiceStatus.DUE;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "issued_at", nullable = false)
    // Field declaration: defines a member variable.
    private LocalDateTime issuedAt;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "due_date")
    // Field declaration: defines a member variable.
    private LocalDateTime dueDate;

    // Annotation: adds metadata used by frameworks/tools.
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    // Method or constructor declaration.
    private Set<InvoiceItem> items = new HashSet<>();

    // Enum declaration: defines a fixed set of constants.
    public enum InvoiceStatus {
        // Statement: DUE,
        DUE,
        // Statement: PARTIAL,
        PARTIAL,
        // Statement: PAID,
        PAID,
        // Statement: CANCELLED
        CANCELLED
    // Closes the current code block.
    }

    // Annotation: adds metadata used by frameworks/tools.
    @PrePersist
    // Opens a method/constructor/block.
    protected void onCreate() {
        // Statement: issuedAt = LocalDateTime.now();
        issuedAt = LocalDateTime.now();
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Constructors, getters, setters
    // Opens a method/constructor/block.
    public Invoice() {
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
    public String getInvoiceNumber() {
        // Return: sends a value back to the caller.
        return invoiceNumber;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setInvoiceNumber(String invoiceNumber) {
        // Uses current object (this) to access a field or method.
        this.invoiceNumber = invoiceNumber;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public User getStudent() {
        // Return: sends a value back to the caller.
        return student;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setStudent(User student) {
        // Uses current object (this) to access a field or method.
        this.student = student;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public BigDecimal getTotalAmount() {
        // Return: sends a value back to the caller.
        return totalAmount;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setTotalAmount(BigDecimal totalAmount) {
        // Uses current object (this) to access a field or method.
        this.totalAmount = totalAmount;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public InvoiceStatus getStatus() {
        // Return: sends a value back to the caller.
        return status;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setStatus(InvoiceStatus status) {
        // Uses current object (this) to access a field or method.
        this.status = status;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public LocalDateTime getIssuedAt() {
        // Return: sends a value back to the caller.
        return issuedAt;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public LocalDateTime getDueDate() {
        // Return: sends a value back to the caller.
        return dueDate;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setDueDate(LocalDateTime dueDate) {
        // Uses current object (this) to access a field or method.
        this.dueDate = dueDate;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public Set<InvoiceItem> getItems() {
        // Return: sends a value back to the caller.
        return items;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setItems(Set<InvoiceItem> items) {
        // Uses current object (this) to access a field or method.
        this.items = items;
    // Closes the current code block.
    }
// Closes the current code block.
}
