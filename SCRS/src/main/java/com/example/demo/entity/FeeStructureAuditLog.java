package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_structure_audit_logs")
public class FeeStructureAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fee_structure_id")
    private FeeStructure feeStructure;

    @Column(name = "action", nullable = false, length = 20)
    private String action;

    @Column(name = "changed_by", nullable = false, length = 255)
    private String changedBy;

    @Column(name = "change_summary", nullable = false, length = 1000)
    private String changeSummary;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public FeeStructure getFeeStructure() {
        return feeStructure;
    }

    public void setFeeStructure(FeeStructure feeStructure) {
        this.feeStructure = feeStructure;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public String getChangeSummary() {
        return changeSummary;
    }

    public void setChangeSummary(String changeSummary) {
        this.changeSummary = changeSummary;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }
}
