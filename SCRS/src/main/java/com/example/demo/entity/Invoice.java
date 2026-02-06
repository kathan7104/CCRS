package com.example.demo.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity representing Invoice.
 */
@Entity
@Table(name = "invoices", indexes = {@Index(name = "idx_invoice_number", columnList = "invoice_number")})
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "invoice_number", nullable = false, unique = true, length = 100)
    private String invoiceNumber;
    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    @Column(name = "total_amount", nullable = false, precision = 16, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvoiceStatus status = InvoiceStatus.DUE;
    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InvoiceItem> items = new HashSet<>();
    public enum InvoiceStatus {
        DUE,
        PARTIAL,
        PAID,
        CANCELLED
    }
    @PrePersist
    protected void onCreate() {
        issuedAt = LocalDateTime.now();
    }
    public Invoice() {
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getInvoiceNumber() {
        return invoiceNumber;
    }
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
    public User getStudent() {
        return student;
    }
    public void setStudent(User student) {
        this.student = student;
    }
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    public InvoiceStatus getStatus() {
        return status;
    }
    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }
    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }
    public LocalDateTime getDueDate() {
        return dueDate;
    }
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
    public Set<InvoiceItem> getItems() {
        return items;
    }
    public void setItems(Set<InvoiceItem> items) {
        this.items = items;
    }
}
