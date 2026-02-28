package com.example.demo.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity
@Table(name = "invoice_items", indexes = {@Index(name = "idx_invoice_item_invoice", columnList = "invoice_id")})
public class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    @Column(length = 255)
    private String description;
    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;
    public InvoiceItem() {
    }
    public Long getId() {
        // 1. Send the result back to the screen
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Invoice getInvoice() {
        // 1. Send the result back to the screen
        return invoice;
    }
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }
    public Course getCourse() {
        // 1. Send the result back to the screen
        return course;
    }
    public void setCourse(Course course) {
        this.course = course;
    }
    public String getDescription() {
        // 1. Send the result back to the screen
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public java.math.BigDecimal getAmount() {
        // 1. Send the result back to the screen
        return amount;
    }
    public void setAmount(java.math.BigDecimal amount) {
        this.amount = amount;
    }
}
