package com.example.demo.service;

import com.example.demo.entity.Invoice;
import com.example.demo.entity.Payment;
import com.example.demo.repository.InvoiceRepository;
import com.example.demo.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportingService {
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public ReportingService(InvoiceRepository invoiceRepository, PaymentRepository paymentRepository) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
    }

    public FinancialSnapshot getFinancialSnapshot() {
        BigDecimal paidRevenue = paymentRepository.getTotalSuccessfulAmount();
        long unpaidCount = invoiceRepository.findByStatus(Invoice.InvoiceStatus.DUE).size()
                + invoiceRepository.findByStatus(Invoice.InvoiceStatus.PARTIAL).size();
        return new FinancialSnapshot(paidRevenue, unpaidCount);
    }

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

    public List<PaymentRow> getReconciliationReport() {
        return paymentRepository.findAll().stream()
                .map(p -> new PaymentRow(
                        p.getTransactionId(),
                        p.getInvoice().getInvoiceNumber(),
                        p.getInvoice().getStudent().getFullName(),
                        p.getAmount(),
                        p.getStatus().name(),
                        p.getPaidAt()))
                .toList();
    }

    public record FinancialSnapshot(BigDecimal totalRevenue, long unpaidCount) {}

    public record UnpaidStudentRow(String invoiceNumber, String studentName, String studentEmail,
                                   BigDecimal amount, String status) {}

    public record PaymentRow(String transactionId, String invoiceNumber, String studentName,
                             BigDecimal amount, String paymentStatus, java.time.LocalDateTime paidAt) {}
}
