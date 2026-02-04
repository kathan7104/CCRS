// Package declaration: groups related classes in a namespace.
package com.example.demo.entity;

// Import statement: brings a class into scope by name.
import jakarta.persistence.*;
// Import statement: brings a class into scope by name.
import java.math.BigDecimal;
// Import statement: brings a class into scope by name.
import java.time.LocalDateTime;

// Annotation: adds metadata used by frameworks/tools.
@Entity
// Annotation: adds metadata used by frameworks/tools.
@Table(name = "payments", indexes = {@Index(name = "idx_payment_tx", columnList = "transaction_id")})
// Class declaration: defines a new type.
public class Payment {

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
    @Column(nullable = false, precision = 16, scale = 2)
    // Field declaration: defines a member variable.
    private BigDecimal amount = BigDecimal.ZERO;

    // Annotation: adds metadata used by frameworks/tools.
    @Enumerated(EnumType.STRING)
    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "method", nullable = false, length = 20)
    // Field declaration: defines a member variable.
    private PaymentMethod method;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "transaction_id", length = 255)
    // Field declaration: defines a member variable.
    private String transactionId;

    // Annotation: adds metadata used by frameworks/tools.
    @Enumerated(EnumType.STRING)
    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "status", nullable = false, length = 20)
    // Field declaration: defines a member variable.
    private PaymentStatus status = PaymentStatus.PENDING;

    // Annotation: adds metadata used by frameworks/tools.
    @Column(name = "paid_at")
    // Field declaration: defines a member variable.
    private LocalDateTime paidAt;

    // Enum declaration: defines a fixed set of constants.
    public enum PaymentMethod {
        // Statement: CASH,
        CASH,
        // Statement: CHEQUE,
        CHEQUE,
        // Statement: CARD,
        CARD,
        // Statement: ONLINE
        ONLINE
    // Closes the current code block.
    }

    // Enum declaration: defines a fixed set of constants.
    public enum PaymentStatus {
        // Statement: PENDING,
        PENDING,
        // Statement: SUCCESS,
        SUCCESS,
        // Statement: FAILED
        FAILED
    // Closes the current code block.
    }

    // Annotation: adds metadata used by frameworks/tools.
    @PrePersist
    // Opens a method/constructor/block.
    protected void onCreate() {
        // Comment: explains code for readers.
        // default behavior; set paidAt when status becomes SUCCESS in service layer
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Constructors, getters and setters
    // Opens a method/constructor/block.
    public Payment() {
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
    public BigDecimal getAmount() {
        // Return: sends a value back to the caller.
        return amount;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setAmount(BigDecimal amount) {
        // Uses current object (this) to access a field or method.
        this.amount = amount;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public PaymentMethod getMethod() {
        // Return: sends a value back to the caller.
        return method;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setMethod(PaymentMethod method) {
        // Uses current object (this) to access a field or method.
        this.method = method;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public String getTransactionId() {
        // Return: sends a value back to the caller.
        return transactionId;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setTransactionId(String transactionId) {
        // Uses current object (this) to access a field or method.
        this.transactionId = transactionId;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public PaymentStatus getStatus() {
        // Return: sends a value back to the caller.
        return status;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setStatus(PaymentStatus status) {
        // Uses current object (this) to access a field or method.
        this.status = status;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public LocalDateTime getPaidAt() {
        // Return: sends a value back to the caller.
        return paidAt;
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    public void setPaidAt(LocalDateTime paidAt) {
        // Uses current object (this) to access a field or method.
        this.paidAt = paidAt;
    // Closes the current code block.
    }
// Closes the current code block.
}
