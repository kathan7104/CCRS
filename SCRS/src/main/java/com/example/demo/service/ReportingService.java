/*
 * File: src/main/java/com/example/demo/service/ReportingService.java
 * Role: Service
 * MVC Fit: Contains business logic used by controllers.
 * Connects To: Controller calls Service, Service calls Repository
 */

package com.example.demo.service;

import com.example.demo.entity.Invoice;
import com.example.demo.entity.Payment;
import com.example.demo.repository.InvoiceRepository;
import com.example.demo.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// Class Summary: Service class that contains business logic used by controllers.
// @Service marks the business logic layer for Spring to manage as a bean.
@Service
public class ReportingService {
// Field: stores invoiceRepository for this class.
    private final InvoiceRepository invoiceRepository;
// Field: stores paymentRepository for this class.
    private final PaymentRepository paymentRepository;

// Constructor: Spring injects dependencies here.
    public ReportingService(InvoiceRepository invoiceRepository, PaymentRepository paymentRepository) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
    }

// Service method: contains business logic and coordinates repositories.
    public FinancialSnapshot getFinancialSnapshot() {
        BigDecimal paidRevenue = paymentRepository.getTotalSuccessfulAmount();
        long unpaidCount = invoiceRepository.findByStatus(Invoice.InvoiceStatus.DUE).size()
                + invoiceRepository.findByStatus(Invoice.InvoiceStatus.PARTIAL).size();
        return new FinancialSnapshot(paidRevenue, unpaidCount);
    }

// Service method: contains business logic and coordinates repositories.
    public List<UnpaidStudentRow> getUnpaidStudentsReport() {
        List<Invoice> due = new ArrayList<>();
        due.addAll(invoiceRepository.findByStatus(Invoice.InvoiceStatus.DUE));
        due.addAll(invoiceRepository.findByStatus(Invoice.InvoiceStatus.PARTIAL));
        return due.stream()
                .map(i -> new UnpaidStudentRow(
                        i.getInvoiceNumber(),
                        i.getStudent().getFullName(),
                        i.getStudent().getEmail(),
                        i.getTotalAmount(),
                        i.getStatus().name()))
                .toList();
    }

// Service method: contains business logic and coordinates repositories.
    public List<PaymentRow> getReconciliationReport() {
        return paymentRepository.findAll().stream()
                .map(p -> new PaymentRow(
                        p.getTransactionId(),
                        p.getInvoice().getInvoiceNumber(),
                        p.getInvoice().getStudent().getFullName(),
                        p.getAmount(),
                        p.getMethod() == null ? "-" : p.getMethod().name(),
                        p.getChequeNumber(),
                        p.getChequeBankName(),
                        p.getChequeIfscCode(),
                        p.getStatus().name(),
                        p.getPaidAt()))
                .toList();
    }

// Service method: contains business logic and coordinates repositories.
    public record FinancialSnapshot(BigDecimal totalRevenue, long unpaidCount) {}

// Service method: contains business logic and coordinates repositories.
    public record UnpaidStudentRow(String invoiceNumber, String studentName, String studentEmail,
                                   BigDecimal amount, String status) {}

// Service method: contains business logic and coordinates repositories.
    public record PaymentRow(String transactionId, String invoiceNumber, String studentName,
                             BigDecimal amount, String paymentMethod, String chequeNumber,
                             String chequeBankName, String chequeIfscCode,
                             String paymentStatus, java.time.LocalDateTime paidAt) {}
}
