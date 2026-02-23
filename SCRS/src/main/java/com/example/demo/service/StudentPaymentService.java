package com.example.demo.service;

import com.example.demo.entity.Course;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.FeeStructure;
import com.example.demo.entity.Invoice;
import com.example.demo.entity.Payment;
import com.example.demo.entity.User;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.FeeStructureRepository;
import com.example.demo.repository.InvoiceRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class StudentPaymentService {
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private static final String PROVIDER_RAZORPAY = "razorpay";
    private static final String PROVIDER_MOCK = "mock";

    private final EnrollmentRepository enrollmentRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final RazorpayGatewayService razorpayGatewayService;

    @Value("${ccrs.payment.razorpay.company-name:CCRS College}")
    private String companyName;

    @Value("${ccrs.payment.razorpay.key-secret:}")
    private String razorpayKeySecret;

    @Value("${ccrs.payment.provider:mock}")
    private String paymentProvider;

    public StudentPaymentService(EnrollmentRepository enrollmentRepository,
                                 FeeStructureRepository feeStructureRepository,
                                 InvoiceRepository invoiceRepository,
                                 PaymentRepository paymentRepository,
                                 UserRepository userRepository,
                                 RazorpayGatewayService razorpayGatewayService) {
        this.enrollmentRepository = enrollmentRepository;
        this.feeStructureRepository = feeStructureRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.razorpayGatewayService = razorpayGatewayService;
    }

    public PaymentDashboardData getPaymentDashboard(User student) {
        FeeStructure activeFee = feeStructureRepository.findFirstByActiveTrueOrderByEffectiveFromDesc().orElse(null);
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(student.getId()).stream()
                .filter(e -> e.getStatus() == Enrollment.EnrollmentStatus.APPROVED
                        || e.getStatus() == Enrollment.EnrollmentStatus.ENROLLED)
                .toList();

        int maxSemester = enrollments.stream()
                .map(e -> e.getCourse() == null ? 0 : safeSemesterLimit(e.getCourse().getDurationSemesters()))
                .max(Integer::compareTo)
                .orElse(0);

        List<SemesterSummary> summaries = new ArrayList<>();
        for (int semester = 1; semester <= maxSemester; semester++) {
            int currentSemester = semester;
            List<Enrollment> semesterEnrollments = enrollments.stream()
                    .filter(e -> e.getCourse() != null)
                    .filter(e -> currentSemester <= safeSemesterLimit(e.getCourse().getDurationSemesters()))
                    .toList();

            BigDecimal expected = semesterEnrollments.stream()
                    .map(e -> calculateSemesterCourseFee(e.getCourse(), activeFee))
                    .reduce(ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);

            Optional<Invoice> invoice = findLatestSemesterInvoice(student.getId(), semester);
            BigDecimal remaining = invoice
                    .map(i -> dueAmount(i.getId(), i.getTotalAmount()))
                    .orElse(expected);

            summaries.add(new SemesterSummary(
                    semester,
                    semesterEnrollments.size(),
                    expected,
                    invoice.map(Invoice::getId).orElse(null),
                    invoice.map(Invoice::getInvoiceNumber).orElse(null),
                    invoice.map(i -> i.getStatus().name()).orElse("NOT_GENERATED"),
                    remaining,
                    invoice.map(Invoice::getDueDate).orElse(null)
            ));
        }

        List<StudentInvoiceRow> invoices = invoiceRepository.findByStudentId(student.getId()).stream()
                .sorted(Comparator.comparing(Invoice::getIssuedAt).reversed())
                .map(i -> {
                    BigDecimal paid = paidAmount(i.getId());
                    BigDecimal remaining = dueAmount(i.getId(), i.getTotalAmount());
                    return new StudentInvoiceRow(i.getId(), i.getInvoiceNumber(), i.getStatus().name(), i.getTotalAmount(), paid, remaining,
                            i.getIssuedAt(), i.getDueDate());
                })
                .toList();

        return new PaymentDashboardData(activeFee, summaries, invoices);
    }

    @Transactional
    public CheckoutSession createCheckoutSession(User student, Long invoiceId) {
        Invoice invoice = getOwnedInvoice(student, invoiceId);
        ensurePayable(invoice);

        BigDecimal due = dueAmount(invoice.getId(), invoice.getTotalAmount());
        if (due.compareTo(BigDecimal.ZERO) <= 0) {
            invoice.setStatus(Invoice.InvoiceStatus.PAID);
            invoiceRepository.save(invoice);
            throw new IllegalStateException("No due amount left for this invoice.");
        }

        long amountPaise = toPaise(due);
        String provider = getActiveProvider();
        String orderId;
        String currency;
        String keyId = "";

        if (PROVIDER_RAZORPAY.equals(provider)) {
            String receipt = "INV-" + invoice.getId() + "-" + System.currentTimeMillis();
            RazorpayGatewayService.RazorpayOrder order;
            try {
                order = razorpayGatewayService.createOrder(amountPaise, receipt, Map.of(
                        "invoiceId", String.valueOf(invoice.getId()),
                        "studentId", String.valueOf(student.getId())
                ));
            } catch (Exception ex) {
                throw new IllegalStateException(ex.getMessage(), ex);
            }
            orderId = order.orderId();
            currency = order.currency();
            keyId = razorpayGatewayService.getKeyId();
        } else {
            orderId = "mock_order_" + UUID.randomUUID().toString().replace("-", "");
            currency = "INR";
        }

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(due);
        payment.setMethod(Payment.PaymentMethod.ONLINE);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setGatewayOrderId(orderId);
        paymentRepository.save(payment);

        return new CheckoutSession(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                orderId,
                amountPaise,
                currency,
                keyId,
                provider,
                student.getFullName(),
                student.getEmail(),
                student.getMobileNumber(),
                companyName
        );
    }

    @Transactional
    public Payment verifyRazorpayPayment(User student,
                                         Long invoiceId,
                                         String razorpayOrderId,
                                         String razorpayPaymentId,
                                         String razorpaySignature) {
        if (razorpayOrderId == null || razorpayOrderId.isBlank()
                || razorpayPaymentId == null || razorpayPaymentId.isBlank()
                || razorpaySignature == null || razorpaySignature.isBlank()) {
            throw new IllegalArgumentException("Missing Razorpay payment fields.");
        }

        Invoice invoice = getOwnedInvoice(student, invoiceId);
        ensurePayable(invoice);

        Payment payment = paymentRepository
                .findTopByInvoiceIdAndGatewayOrderIdOrderByIdDesc(invoiceId, razorpayOrderId)
                .orElseThrow(() -> new IllegalStateException("Payment order not found for this invoice."));

        if (payment.getStatus() == Payment.PaymentStatus.SUCCESS) {
            return payment;
        }

        String computed = hmacSha256(razorpayOrderId + "|" + razorpayPaymentId, razorpayKeySecret);
        if (!constantTimeEquals(computed, razorpaySignature)) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setGatewaySignature(razorpaySignature);
            paymentRepository.save(payment);
            throw new IllegalStateException("Razorpay signature verification failed.");
        }

        RazorpayGatewayService.RazorpayPayment gatewayPayment;
        try {
            gatewayPayment = razorpayGatewayService.fetchPayment(razorpayPaymentId);
        } catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }

        if (gatewayPayment == null) {
            throw new IllegalStateException("Razorpay payment not found.");
        }
        if (!razorpayOrderId.equals(gatewayPayment.orderId())) {
            throw new IllegalStateException("Razorpay payment does not match order.");
        }
        String gatewayStatus = gatewayPayment.status() == null ? "" : gatewayPayment.status().toLowerCase();
        if (!("captured".equals(gatewayStatus) || "authorized".equals(gatewayStatus))) {
            throw new IllegalStateException("Razorpay payment is not successful yet. Current status: " + gatewayPayment.status());
        }

        payment.setTransactionId(razorpayPaymentId);
        payment.setGatewaySignature(razorpaySignature);
        payment.setAmount(gatewayPayment.amount().setScale(2, RoundingMode.HALF_UP));
        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);
        refreshInvoiceStatus(invoice);

        return payment;
    }

    @Transactional
    public Payment completeMockPayment(User student,
                                       Long invoiceId,
                                       String gatewayOrderId,
                                       boolean success,
                                       MockPaymentDetails paymentDetails) {
        Invoice invoice = getOwnedInvoice(student, invoiceId);
        ensurePayable(invoice);

        if (gatewayOrderId == null || gatewayOrderId.isBlank()) {
            throw new IllegalArgumentException("Missing mock order id.");
        }

        Payment payment = paymentRepository
                .findTopByInvoiceIdAndGatewayOrderIdOrderByIdDesc(invoiceId, gatewayOrderId)
                .orElseThrow(() -> new IllegalStateException("Mock payment order not found for this invoice."));

        if (payment.getStatus() == Payment.PaymentStatus.SUCCESS) {
            return payment;
        }

        if (!success) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new IllegalStateException("Mock payment marked as failed.");
        }

        validateMockPaymentDetails(paymentDetails);
        payment.setTransactionId(buildMockTransactionId(paymentDetails));
        payment.setGatewaySignature(buildMockSignatureSummary(paymentDetails));
        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);
        refreshInvoiceStatus(invoice);
        return payment;
    }

    private Invoice getOwnedInvoice(User student, Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found."));
        if (invoice.getStudent() == null || !invoice.getStudent().getId().equals(student.getId())) {
            throw new IllegalStateException("You cannot access this invoice.");
        }
        return invoice;
    }

    private void ensurePayable(Invoice invoice) {
        if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
            throw new IllegalStateException("Invoice is already paid.");
        }
        if (invoice.getStatus() == Invoice.InvoiceStatus.CANCELLED) {
            throw new IllegalStateException("Cancelled invoice cannot be paid.");
        }
    }

    private void refreshInvoiceStatus(Invoice invoice) {
        BigDecimal totalPaid = paidAmount(invoice.getId());
        BigDecimal remaining = invoice.getTotalAmount().subtract(totalPaid).setScale(2, RoundingMode.HALF_UP);
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            invoice.setStatus(Invoice.InvoiceStatus.PAID);
            markStudentEnrolledIfFirstSemesterPaid(invoice);
        } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus(Invoice.InvoiceStatus.PARTIAL);
        } else {
            invoice.setStatus(Invoice.InvoiceStatus.DUE);
        }
        invoiceRepository.save(invoice);
    }

    private Optional<Invoice> findLatestSemesterInvoice(Long studentId, int semester) {
        return invoiceRepository.findByStudentIdAndInvoiceNumberStartingWith(studentId, "SEM-" + semester + "-").stream()
                .max(Comparator.comparing(Invoice::getIssuedAt));
    }

    private BigDecimal paidAmount(Long invoiceId) {
        BigDecimal value = paymentRepository.getSuccessfulAmountByInvoiceId(invoiceId);
        return (value == null ? BigDecimal.ZERO : value).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal dueAmount(Long invoiceId, BigDecimal total) {
        return total.subtract(paidAmount(invoiceId)).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateSemesterCourseFee(Course course, FeeStructure feeStructure) {
        if (course == null) {
            return ZERO;
        }

        int durationSemesters = safeSemesterLimit(course.getDurationSemesters());
        BigDecimal semesterTuition = BigDecimal.valueOf(course.getFee())
                .divide(BigDecimal.valueOf(durationSemesters), 2, RoundingMode.HALF_UP);

        if (feeStructure == null) {
            return semesterTuition;
        }

        BigDecimal creditFee = feeStructure.getCostPerCredit().multiply(BigDecimal.valueOf(course.getCredits()));
        BigDecimal fixed = feeStructure.getLabFee().add(feeStructure.getDifferentialFee());
        return semesterTuition.add(creditFee).add(fixed).setScale(2, RoundingMode.HALF_UP);
    }

    private int safeSemesterLimit(Integer value) {
        return value == null || value < 1 ? 1 : value;
    }

    private long toPaise(BigDecimal rupees) {
        return rupees.setScale(2, RoundingMode.HALF_UP).movePointRight(2).longValueExact();
    }

    private String hmacSha256(String data, String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("Razorpay secret key is missing.");
        }
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            hmac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(hmac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to verify Razorpay signature.", ex);
        }
    }

    private void validateMockPaymentDetails(MockPaymentDetails paymentDetails) {
        if (paymentDetails == null || paymentDetails.paymentMode() == null || paymentDetails.paymentMode().isBlank()) {
            throw new IllegalArgumentException("Select a payment option (UPI or CARD).");
        }
        String mode = paymentDetails.paymentMode().trim().toUpperCase();
        if (!"UPI".equals(mode) && !"CARD".equals(mode)) {
            throw new IllegalArgumentException("Unsupported payment option. Choose UPI or CARD.");
        }
        if ("UPI".equals(mode)) {
            String upiId = safe(paymentDetails.upiId());
            if (!upiId.matches("^[a-zA-Z0-9._-]{2,}@[a-zA-Z]{2,}$")) {
                throw new IllegalArgumentException("Enter a valid UPI ID (example: name@bank).");
            }
            return;
        }

        String cardNumberDigits = safe(paymentDetails.cardNumber()).replaceAll("\\s+", "");
        if (!cardNumberDigits.matches("^\\d{12,19}$")) {
            throw new IllegalArgumentException("Enter a valid card number.");
        }
        if (safe(paymentDetails.cardHolderName()).isBlank()) {
            throw new IllegalArgumentException("Card holder name is required.");
        }
        String expiry = safe(paymentDetails.cardExpiry());
        if (!expiry.matches("^(0[1-9]|1[0-2])/\\d{2}$")) {
            throw new IllegalArgumentException("Card expiry must be in MM/YY format.");
        }
        String cvv = safe(paymentDetails.cardCvv());
        if (!cvv.matches("^\\d{3,4}$")) {
            throw new IllegalArgumentException("CVV must be 3 or 4 digits.");
        }
    }

    private String buildMockTransactionId(MockPaymentDetails paymentDetails) {
        String mode = paymentDetails.paymentMode().trim().toUpperCase();
        String suffix;
        if ("UPI".equals(mode)) {
            String upi = safe(paymentDetails.upiId());
            suffix = upi.substring(Math.max(0, upi.length() - 4)).toUpperCase();
        } else {
            String cardDigits = safe(paymentDetails.cardNumber()).replaceAll("\\s+", "");
            suffix = cardDigits.substring(Math.max(0, cardDigits.length() - 4));
        }
        return "mock_" + mode.toLowerCase() + "_" + suffix + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private String buildMockSignatureSummary(MockPaymentDetails paymentDetails) {
        String mode = paymentDetails.paymentMode().trim().toUpperCase();
        if ("UPI".equals(mode)) {
            return "mock_signature_ok|mode=UPI|upi=" + safe(paymentDetails.upiId());
        }
        String cardDigits = safe(paymentDetails.cardNumber()).replaceAll("\\s+", "");
        String masked = "****" + cardDigits.substring(Math.max(0, cardDigits.length() - 4));
        return "mock_signature_ok|mode=CARD|card=" + masked + "|holder=" + safe(paymentDetails.cardHolderName());
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean constantTimeEquals(String a, String b) {
        return java.security.MessageDigest.isEqual(
                a.getBytes(StandardCharsets.UTF_8),
                b.getBytes(StandardCharsets.UTF_8)
        );
    }

    private void markStudentEnrolledIfFirstSemesterPaid(Invoice invoice) {
        if (invoice == null || invoice.getInvoiceNumber() == null || !invoice.getInvoiceNumber().startsWith("SEM-1-")) {
            return;
        }
        if (invoice.getStudent() == null || invoice.getStudent().getId() == null) {
            return;
        }

        Set<Long> courseIdsInInvoice = new HashSet<>();
        for (var item : invoice.getItems()) {
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

    public String getPaymentProviderLabel() {
        return PROVIDER_RAZORPAY.equals(getActiveProvider()) ? "Razorpay" : "Mock Gateway";
    }

    public boolean isMockProviderActive() {
        return PROVIDER_MOCK.equals(getActiveProvider());
    }

    private String getActiveProvider() {
        if (paymentProvider == null) {
            return PROVIDER_MOCK;
        }
        String normalized = paymentProvider.trim().toLowerCase();
        if (PROVIDER_RAZORPAY.equals(normalized)) {
            return PROVIDER_RAZORPAY;
        }
        return PROVIDER_MOCK;
    }

    public record PaymentDashboardData(FeeStructure activeFee,
                                       List<SemesterSummary> semesters,
                                       List<StudentInvoiceRow> invoices) {
    }

    public record SemesterSummary(int semester,
                                  int enrolledCourses,
                                  BigDecimal expectedAmount,
                                  Long invoiceId,
                                  String invoiceNumber,
                                  String invoiceStatus,
                                  BigDecimal dueAmount,
                                  LocalDateTime dueDate) {
    }

    public record StudentInvoiceRow(Long invoiceId,
                                    String invoiceNumber,
                                    String status,
                                    BigDecimal totalAmount,
                                    BigDecimal paidAmount,
                                    BigDecimal dueAmount,
                                    LocalDateTime issuedAt,
                                    LocalDateTime dueDate) {
    }

    public record CheckoutSession(Long invoiceId,
                                  String invoiceNumber,
                                  String orderId,
                                  long amountPaise,
                                  String currency,
                                  String keyId,
                                  String provider,
                                  String studentName,
                                  String studentEmail,
                                  String studentPhone,
                                  String merchantName) {
    }

    public record MockPaymentDetails(String paymentMode,
                                     String upiId,
                                     String cardNumber,
                                     String cardHolderName,
                                     String cardExpiry,
                                     String cardCvv) {
    }
}
