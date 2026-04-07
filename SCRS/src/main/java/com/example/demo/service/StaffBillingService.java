package com.example.demo.service;

import com.example.demo.entity.Course;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.FeeStructure;
import com.example.demo.entity.Invoice;
import com.example.demo.entity.InvoiceItem;
import com.example.demo.entity.Payment;
import com.example.demo.entity.User;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.FeeStructureRepository;
import com.example.demo.repository.InvoiceRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class StaffBillingService {
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public StaffBillingService(UserRepository userRepository,
                              EnrollmentRepository enrollmentRepository,
                              FeeStructureRepository feeStructureRepository,
                              InvoiceRepository invoiceRepository,
                              PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.feeStructureRepository = feeStructureRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
    }

    public List<User> getActiveStudents() {
        return userRepository.findByRole("STUDENT").stream()
                .filter(User::isActive)
                .sorted(Comparator.comparing(User::getFullName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public List<StaffInvoiceRow> getInvoiceRows() {
        return invoiceRepository.findAll().stream()
                .sorted(Comparator.comparing(Invoice::getIssuedAt).reversed())
                .map(i -> {
                    BigDecimal paid = paidAmount(i.getId());
                    BigDecimal due = i.getTotalAmount().subtract(paid).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
                    return new StaffInvoiceRow(
                            i.getId(),
                            i.getInvoiceNumber(),
                            i.getStudent() != null ? i.getStudent().getFullName() : "-",
                            i.getStudent() != null ? i.getStudent().getEmail() : "-",
                            i.getStatus().name(),
                            i.getTotalAmount(),
                            paid,
                            due,
                            i.getIssuedAt(),
                            i.getDueDate()
                    );
                })
                .toList();
    }

    @Transactional
    public Invoice generateSemesterInvoice(Long studentId, int semester) {
        if (semester < 1) {
            throw new IllegalArgumentException("Semester must be 1 or greater.");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found."));
        if (!student.getRoles().contains("STUDENT")) {
            throw new IllegalArgumentException("Selected user is not a student.");
        }

        FeeStructure activeFee = feeStructureRepository.findFirstByActiveTrueOrderByEffectiveFromDesc()
                .orElseThrow(() -> new IllegalStateException("No active fee structure found."));

        List<Enrollment> semesterEnrollments = enrollmentRepository.findByStudentId(student.getId()).stream()
                .filter(e -> e.getStatus() == Enrollment.EnrollmentStatus.APPROVED
                        || e.getStatus() == Enrollment.EnrollmentStatus.ENROLLED)
                .filter(e -> e.getCourse() != null)
                .filter(e -> semester <= safeSemesterLimit(e.getCourse().getDurationSemesters()))
                .toList();

        if (semesterEnrollments.isEmpty()) {
            throw new IllegalStateException("No approved enrolled courses found for this semester.");
        }

        Optional<Invoice> existing = findLatestSemesterInvoice(student.getId(), semester);
        if (existing.isPresent()) {
            Invoice invoice = existing.get();
            if (invoice.getStatus() == Invoice.InvoiceStatus.DUE
                    || invoice.getStatus() == Invoice.InvoiceStatus.PARTIAL
                    || invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
                return invoice;
            }
        }

        Invoice invoice = new Invoice();
        invoice.setStudent(student);
        invoice.setStatus(Invoice.InvoiceStatus.DUE);
        invoice.setInvoiceNumber("SEM-" + semester + "-" + student.getId() + "-" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        invoice.setDueDate(LocalDateTime.now().plusDays(10));

        BigDecimal total = ZERO;
        for (Enrollment enrollment : semesterEnrollments) {
            Course course = enrollment.getCourse();
            BigDecimal courseTotal = calculateSemesterCourseFee(course, activeFee);
            total = total.add(courseTotal);

            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            item.setCourse(course);
            item.setDescription("Semester " + semester + " fee - " + course.getCode());
            item.setAmount(courseTotal);
            invoice.getItems().add(item);
        }

        invoice.setTotalAmount(total.setScale(2, RoundingMode.HALF_UP));
        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Payment recordOfflinePayment(Long invoiceId,
                                        Payment.PaymentMethod method,
                                        BigDecimal amount,
                                        String chequeNumber,
                                        String chequeBankName,
                                        String chequeIfscCode) {
        if (method != Payment.PaymentMethod.CASH && method != Payment.PaymentMethod.CHEQUE) {
            throw new IllegalArgumentException("Only CASH/CHEQUE entries are allowed here.");
        }
        if (method == Payment.PaymentMethod.CHEQUE) {
            String cleaned = chequeNumber == null ? "" : chequeNumber.trim();
            if (cleaned.isBlank()) {
                throw new IllegalArgumentException("Cheque number is required for cheque payments.");
            }
            if (cleaned.length() > 50) {
                throw new IllegalArgumentException("Cheque number is too long.");
            }
            String bankName = chequeBankName == null ? "" : chequeBankName.trim();
            if (bankName.isBlank()) {
                throw new IllegalArgumentException("Bank name is required for cheque payments.");
            }
            if (bankName.length() > 100) {
                throw new IllegalArgumentException("Bank name is too long.");
            }
            String ifsc = chequeIfscCode == null ? "" : chequeIfscCode.trim();
            if (ifsc.isBlank()) {
                throw new IllegalArgumentException("IFSC code is required for cheque payments.");
            }
            if (ifsc.length() > 20) {
                throw new IllegalArgumentException("IFSC code is too long.");
            }
        }

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found."));

        if (invoice.getStatus() == Invoice.InvoiceStatus.CANCELLED) {
            throw new IllegalStateException("Cannot collect payment for cancelled invoice.");
        }

        BigDecimal due = invoice.getTotalAmount().subtract(paidAmount(invoice.getId()))
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
        if (due.compareTo(BigDecimal.ZERO) <= 0) {
            invoice.setStatus(Invoice.InvoiceStatus.PAID);
            invoiceRepository.save(invoice);
            throw new IllegalStateException("Invoice is already fully paid.");
        }

        if (amount == null) {
            throw new IllegalArgumentException("Payment amount is required.");
        }
        BigDecimal paymentAmount = amount.setScale(2, RoundingMode.HALF_UP);
        if (paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }
        if (paymentAmount.compareTo(due) > 0) {
            throw new IllegalArgumentException("Payment amount cannot exceed due amount " + due + ".");
        }

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(paymentAmount);
        payment.setMethod(method);
        if (method == Payment.PaymentMethod.CHEQUE) {
            payment.setChequeNumber(chequeNumber == null ? null : chequeNumber.trim());
            payment.setChequeBankName(chequeBankName == null ? null : chequeBankName.trim());
            payment.setChequeIfscCode(chequeIfscCode == null ? null : chequeIfscCode.trim());
        }
        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());
        payment.setTransactionId("OFF-" + method.name() + "-" + invoice.getId() + "-" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        Payment saved = paymentRepository.save(payment);

        BigDecimal remaining = due.subtract(paymentAmount).setScale(2, RoundingMode.HALF_UP);
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            invoice.setStatus(Invoice.InvoiceStatus.PAID);
            markStudentEnrolledIfFirstSemesterPaid(invoice);
        } else {
            invoice.setStatus(Invoice.InvoiceStatus.PARTIAL);
        }
        invoiceRepository.save(invoice);

        return saved;
    }

    private Optional<Invoice> findLatestSemesterInvoice(Long studentId, int semester) {
        return invoiceRepository.findByStudentIdAndInvoiceNumberStartingWith(studentId, "SEM-" + semester + "-").stream()
                .max(Comparator.comparing(Invoice::getIssuedAt));
    }

    private BigDecimal paidAmount(Long invoiceId) {
        BigDecimal value = paymentRepository.getSuccessfulAmountByInvoiceId(invoiceId);
        return (value == null ? BigDecimal.ZERO : value).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateSemesterCourseFee(Course course, FeeStructure feeStructure) {
        if (course == null) {
            return ZERO;
        }

        int durationSemesters = safeSemesterLimit(course.getDurationSemesters());
        BigDecimal semesterTuition = BigDecimal.valueOf(course.getFee())
                .divide(BigDecimal.valueOf(durationSemesters), 2, RoundingMode.HALF_UP);

        BigDecimal creditFee = feeStructure.getCostPerCredit().multiply(BigDecimal.valueOf(course.getCredits()));
        BigDecimal fixed = feeStructure.getLabFee().add(feeStructure.getDifferentialFee());
        return semesterTuition.add(creditFee).add(fixed).setScale(2, RoundingMode.HALF_UP);
    }

    private int safeSemesterLimit(Integer value) {
        return value == null || value < 1 ? 1 : value;
    }

    private void markStudentEnrolledIfFirstSemesterPaid(Invoice invoice) {
        if (invoice == null || invoice.getInvoiceNumber() == null || !invoice.getInvoiceNumber().startsWith("SEM-1-")) {
            return;
        }
        if (invoice.getStudent() == null || invoice.getStudent().getId() == null) {
            return;
        }

        Set<Long> courseIdsInInvoice = new HashSet<>();
        for (InvoiceItem item : invoice.getItems()) {
            if (item.getCourse() != null && item.getCourse().getId() != null) {
                courseIdsInInvoice.add(item.getCourse().getId());
            }
        }
        if (courseIdsInInvoice.isEmpty()) {
            return;
        }

        List<Enrollment> approved = enrollmentRepository.findByStudentIdAndStatus(
                invoice.getStudent().getId(), Enrollment.EnrollmentStatus.APPROVED);
        for (Enrollment enrollment : approved) {
            if (enrollment.getCourse() == null || enrollment.getCourse().getId() == null) {
                continue;
            }
            if (courseIdsInInvoice.contains(enrollment.getCourse().getId())) {
                enrollment.setStatus(Enrollment.EnrollmentStatus.ENROLLED);
                enrollmentRepository.save(enrollment);
                syncStudentDepartmentFromEnrollment(enrollment);
            }
        }
    }

    private void syncStudentDepartmentFromEnrollment(Enrollment enrollment) {
        if (enrollment == null || enrollment.getStudent() == null || enrollment.getCourse() == null) {
            return;
        }
        User student = enrollment.getStudent();
        String courseDepartment = enrollment.getCourse().getDepartment();
        if (courseDepartment == null || courseDepartment.isBlank()) {
            return;
        }
        if (courseDepartment.equalsIgnoreCase(student.getDepartment())) {
            return;
        }
        student.setDepartment(courseDepartment);
        userRepository.save(student);
    }

    public record StaffInvoiceRow(Long invoiceId,
                                  String invoiceNumber,
                                  String studentName,
                                  String studentEmail,
                                  String status,
                                  BigDecimal totalAmount,
                                  BigDecimal paidAmount,
                                  BigDecimal dueAmount,
                                  LocalDateTime issuedAt,
                                  LocalDateTime dueDate) {
    }
}
