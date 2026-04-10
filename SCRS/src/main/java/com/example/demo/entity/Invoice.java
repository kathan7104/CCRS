/*
 * File: src/main/java/com/example/demo/entity/Invoice.java
 * Role: Entity
 * MVC Fit: JPA entity that maps to a database table.
 * Connects To: Repository reads/writes this entity
 */

package com.example.demo.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
// Class Summary: Entity class that is a JPA entity mapped to a database table.
// @Entity maps this class to a database table managed by JPA.
@Entity
// @Table specifies the database table name for this entity.
@Table(name = "invoices", indexes = {@Index(name = "idx_invoice_number", columnList = "invoice_number")})
public class Invoice {
// @Id marks the primary key field for the entity.
    @Id
// @GeneratedValue lets JPA auto-generate primary key values.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
// Field: stores id for this class.
    private Long id;
// @Column customizes the database column mapping.
    @Column(name = "invoice_number", nullable = false, unique = true, length = 100)
// Field: stores invoiceNumber for this class.
    private String invoiceNumber;
// @ManyToOne maps a many-to-one relationship between entities.
    @ManyToOne(optional = false)
// @JoinColumn defines the foreign key column for a relationship.
    @JoinColumn(name = "student_id", nullable = false)
// Field: stores student for this class.
    private User student;
// @Column customizes the database column mapping.
    @Column(name = "total_amount", nullable = false, precision = 16, scale = 2)
// Field: stores totalAmount for this class.
    private BigDecimal totalAmount = BigDecimal.ZERO;
// @Enumerated tells JPA how to store enum values in the database.
    @Enumerated(EnumType.STRING)
// @Column customizes the database column mapping.
    @Column(nullable = false, length = 20)
// Field: stores status for this class.
    private InvoiceStatus status = InvoiceStatus.DUE;
// @Column customizes the database column mapping.
    @Column(name = "issued_at", nullable = false)
// Field: stores issuedAt for this class.
    private LocalDateTime issuedAt;
// @Column customizes the database column mapping.
    @Column(name = "due_date")
// Field: stores dueDate for this class.
    private LocalDateTime dueDate;
// @OneToMany maps a one-to-many relationship between entities.
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
// Field: stores items for this class.
// Method: performs a focused unit of work in this class.
    private Set<InvoiceItem> items = new HashSet<>();
    public enum InvoiceStatus {
        DUE,
        PARTIAL,
        PAID,
        CANCELLED
    }
    @PrePersist
// Method: performs a focused unit of work in this class.
    protected void onCreate() {
        issuedAt = LocalDateTime.now();
    }
// Constructor: Spring injects dependencies here.
    public Invoice() {
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
    public String getInvoiceNumber() {
        // 1. Send the result back to the screen
        return invoiceNumber;
    }
// Method: performs a focused unit of work in this class.
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
// Method: performs a focused unit of work in this class.
    public User getStudent() {
        // 1. Send the result back to the screen
        return student;
    }
// Method: performs a focused unit of work in this class.
    public void setStudent(User student) {
        this.student = student;
    }
// Method: performs a focused unit of work in this class.
    public BigDecimal getTotalAmount() {
        // 1. Send the result back to the screen
        return totalAmount;
    }
// Method: performs a focused unit of work in this class.
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
// Method: performs a focused unit of work in this class.
    public InvoiceStatus getStatus() {
        // 1. Send the result back to the screen
        return status;
    }
// Method: performs a focused unit of work in this class.
    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }
// Method: performs a focused unit of work in this class.
    public LocalDateTime getIssuedAt() {
        // 1. Send the result back to the screen
        return issuedAt;
    }
// Method: performs a focused unit of work in this class.
    public LocalDateTime getDueDate() {
        // 1. Send the result back to the screen
        return dueDate;
    }
// Method: performs a focused unit of work in this class.
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
// Method: performs a focused unit of work in this class.
    public Set<InvoiceItem> getItems() {
        // 1. Send the result back to the screen
        return items;
    }
// Method: performs a focused unit of work in this class.
    public void setItems(Set<InvoiceItem> items) {
        this.items = items;
    }
}
