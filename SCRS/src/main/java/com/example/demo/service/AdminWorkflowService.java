package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AdminWorkflowService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final InvoiceRepository invoiceRepository;
    private final FeeStructureRepository feeStructureRepository;

    public AdminWorkflowService(EnrollmentRepository enrollmentRepository,
                                CourseRepository courseRepository,
                                InvoiceRepository invoiceRepository,
                                FeeStructureRepository feeStructureRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.invoiceRepository = invoiceRepository;
        this.feeStructureRepository = feeStructureRepository;
    }

    @Transactional
    public void approveEnrollment(Long enrollmentId, String adminNote) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));
        if (enrollment.getStatus() != Enrollment.EnrollmentStatus.PENDING) {
            throw new IllegalStateException("Only pending applications can be approved.");
        }

        enrollment.setStatus(Enrollment.EnrollmentStatus.ENROLLED);
        enrollment.setComments(adminNote);
        enrollmentRepository.save(enrollment);
        generateInvoice(enrollment);
    }

    @Transactional
    public void rejectEnrollment(Long enrollmentId, String adminNote) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));
        if (enrollment.getStatus() != Enrollment.EnrollmentStatus.PENDING) {
            throw new IllegalStateException("Only pending applications can be rejected.");
        }

        Course course = courseRepository.findByIdForUpdate(enrollment.getCourse().getId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        course.setRemainingSeats(course.getRemainingSeats() + 1);
        courseRepository.save(course);

        enrollment.setStatus(Enrollment.EnrollmentStatus.CANCELLED);
        enrollment.setComments(adminNote);
        enrollmentRepository.save(enrollment);
    }

    private void generateInvoice(Enrollment enrollment) {
        Course course = enrollment.getCourse();
        FeeStructure activeFee = feeStructureRepository.findFirstByActiveTrueOrderByEffectiveFromDesc()
                .orElse(null);

        BigDecimal tuition = BigDecimal.valueOf(course.getFee());
        BigDecimal creditCost = activeFee == null
                ? BigDecimal.ZERO
                : activeFee.getCostPerCredit().multiply(BigDecimal.valueOf(course.getCredits()));
        BigDecimal labFee = activeFee == null ? BigDecimal.ZERO : activeFee.getLabFee();
        BigDecimal differentialFee = activeFee == null ? BigDecimal.ZERO : activeFee.getDifferentialFee();
        BigDecimal latePenalty = activeFee == null ? BigDecimal.ZERO : activeFee.getLatePenalty();

        Invoice invoice = new Invoice();
        invoice.setStudent(enrollment.getStudent());
        invoice.setStatus(Invoice.InvoiceStatus.DUE);
        invoice.setInvoiceNumber("INV-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "-" + enrollment.getId());
        invoice.setDueDate(LocalDateTime.now().plusDays(10));

        addItem(invoice, course, "Course tuition fee", tuition);
        addItem(invoice, course, "Credit fee", creditCost);
        addItem(invoice, course, "Mandatory lab fee", labFee);
        addItem(invoice, course, "Differential fee", differentialFee);
        addItem(invoice, course, "Late penalty", latePenalty);

        BigDecimal total = tuition.add(creditCost).add(labFee).add(differentialFee).add(latePenalty);
        invoice.setTotalAmount(total);
        invoiceRepository.save(invoice);
    }

    private void addItem(Invoice invoice, Course course, String description, BigDecimal amount) {
        InvoiceItem item = new InvoiceItem();
        item.setInvoice(invoice);
        item.setCourse(course);
        item.setDescription(description);
        item.setAmount(amount);
        invoice.getItems().add(item);
    }
}
