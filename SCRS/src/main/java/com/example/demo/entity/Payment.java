/*
 * File: src/main/java/com/example/demo/entity/Payment.java
 * Role: Entity
 * MVC Fit: JPA entity that maps to a database table.
 * Connects To: Repository reads/writes this entity
 */

package com.example.demo.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
// Class Summary: Entity class that is a JPA entity mapped to a database table.
// @Entity maps this class to a database table managed by JPA.
@Entity
// @Table specifies the database table name for this entity.
@Table(name = "payments", indexes = {@Index(name = "idx_payment_tx", columnList = "transaction_id")})
public class Payment {
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
// @Column customizes the database column mapping.
    @Column(nullable = false, precision = 16, scale = 2)
// Field: stores amount for this class.
    private BigDecimal amount = BigDecimal.ZERO;
// @Enumerated tells JPA how to store enum values in the database.
    @Enumerated(EnumType.STRING)
// @Column customizes the database column mapping.
    @Column(name = "method", nullable = false, length = 20)
// Field: stores method for this class.
    private PaymentMethod method;
// @Column customizes the database column mapping.
    @Column(name = "cheque_number", length = 50)
// Field: stores chequeNumber for this class.
    private String chequeNumber;
// @Column customizes the database column mapping.
    @Column(name = "cheque_bank_name", length = 100)
// Field: stores chequeBankName for this class.
    private String chequeBankName;
// @Column customizes the database column mapping.
    @Column(name = "cheque_ifsc_code", length = 20)
// Field: stores chequeIfscCode for this class.
    private String chequeIfscCode;
// @Column customizes the database column mapping.
    @Column(name = "transaction_id", length = 255)
// Field: stores transactionId for this class.
    private String transactionId;
// @Column customizes the database column mapping.
    @Column(name = "gateway_order_id", length = 255)
// Field: stores gatewayOrderId for this class.
    private String gatewayOrderId;
// @Column customizes the database column mapping.
    @Column(name = "gateway_signature", length = 500)
// Field: stores gatewaySignature for this class.
    private String gatewaySignature;
// @Enumerated tells JPA how to store enum values in the database.
    @Enumerated(EnumType.STRING)
// @Column customizes the database column mapping.
    @Column(name = "status", nullable = false, length = 20)
// Field: stores status for this class.
    private PaymentStatus status = PaymentStatus.PENDING;
// @Column customizes the database column mapping.
    @Column(name = "paid_at")
// Field: stores paidAt for this class.
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
// Method: performs a focused unit of work in this class.
    protected void onCreate() {
    }
// Constructor: Spring injects dependencies here.
    public Payment() {
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
    public BigDecimal getAmount() {
        // 1. Send the result back to the screen
        return amount;
    }
// Method: performs a focused unit of work in this class.
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
// Method: performs a focused unit of work in this class.
    public PaymentMethod getMethod() {
        // 1. Send the result back to the screen
        return method;
    }
// Method: performs a focused unit of work in this class.
    public void setMethod(PaymentMethod method) {
        this.method = method;
    }
// Method: performs a focused unit of work in this class.
    public String getTransactionId() {
        // 1. Send the result back to the screen
        return transactionId;
    }
// Method: performs a focused unit of work in this class.
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
// Method: performs a focused unit of work in this class.
    public String getChequeNumber() {
        return chequeNumber;
    }
// Method: performs a focused unit of work in this class.
    public void setChequeNumber(String chequeNumber) {
        this.chequeNumber = chequeNumber;
    }
// Method: performs a focused unit of work in this class.
    public String getChequeBankName() {
        return chequeBankName;
    }
// Method: performs a focused unit of work in this class.
    public void setChequeBankName(String chequeBankName) {
        this.chequeBankName = chequeBankName;
    }
// Method: performs a focused unit of work in this class.
    public String getChequeIfscCode() {
        return chequeIfscCode;
    }
// Method: performs a focused unit of work in this class.
    public void setChequeIfscCode(String chequeIfscCode) {
        this.chequeIfscCode = chequeIfscCode;
    }
// Method: performs a focused unit of work in this class.
    public PaymentStatus getStatus() {
        // 1. Send the result back to the screen
        return status;
    }
// Method: performs a focused unit of work in this class.
    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
// Method: performs a focused unit of work in this class.
    public String getGatewayOrderId() {
        return gatewayOrderId;
    }
// Method: performs a focused unit of work in this class.
    public void setGatewayOrderId(String gatewayOrderId) {
        this.gatewayOrderId = gatewayOrderId;
    }
// Method: performs a focused unit of work in this class.
    public String getGatewaySignature() {
        return gatewaySignature;
    }
// Method: performs a focused unit of work in this class.
    public void setGatewaySignature(String gatewaySignature) {
        this.gatewaySignature = gatewaySignature;
    }
// Method: performs a focused unit of work in this class.
    public LocalDateTime getPaidAt() {
        // 1. Send the result back to the screen
        return paidAt;
    }
// Method: performs a focused unit of work in this class.
    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
}
