package com.example.demo.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "payments", indexes = {@Index(name = "idx_payment_tx", columnList = "transaction_id")})
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;
    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;
    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false, length = 20)
    private PaymentMethod method;
    @Column(name = "transaction_id", length = 255)
    private String transactionId;
    @Column(name = "gateway_order_id", length = 255)
    private String gatewayOrderId;
    @Column(name = "gateway_signature", length = 500)
    private String gatewaySignature;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.PENDING;
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
    public enum PaymentMethod {
        CASH,
        CHEQUE,
        CARD,
        ONLINE
    }
    public enum PaymentStatus {
        PENDING,
        SUCCESS,
        FAILED
    }
    @PrePersist
    protected void onCreate() {
    }
    public Payment() {
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
    public BigDecimal getAmount() {
        // 1. Send the result back to the screen
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public PaymentMethod getMethod() {
        // 1. Send the result back to the screen
        return method;
    }
    public void setMethod(PaymentMethod method) {
        this.method = method;
    }
    public String getTransactionId() {
        // 1. Send the result back to the screen
        return transactionId;
    }
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    public PaymentStatus getStatus() {
        // 1. Send the result back to the screen
        return status;
    }
    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
    public String getGatewayOrderId() {
        return gatewayOrderId;
    }
    public void setGatewayOrderId(String gatewayOrderId) {
        this.gatewayOrderId = gatewayOrderId;
    }
    public String getGatewaySignature() {
        return gatewaySignature;
    }
    public void setGatewaySignature(String gatewaySignature) {
        this.gatewaySignature = gatewaySignature;
    }
    public LocalDateTime getPaidAt() {
        // 1. Send the result back to the screen
        return paidAt;
    }
    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
}
