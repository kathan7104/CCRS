/*
 * File: src/main/java/com/example/demo/controller/StudentPaymentController.java
 * Role: Controller
 * MVC Fit: Handles HTTP requests in the MVC layer.
 * Connects To: Client -> Controller -> Service -> Repository -> Database -> Response
 */

package com.example.demo.controller;

import com.example.demo.entity.Payment;
import com.example.demo.entity.Enrollment;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.service.StudentPaymentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Class Summary: Controller class that handles HTTP requests in the MVC layer.
// @Controller marks this class as an MVC controller that returns views.
@Controller
// @RequestMapping defines a common URL prefix for all endpoints in this controller.
@RequestMapping({"/payments", "/payment"})
public class StudentPaymentController {
// Field: stores studentPaymentService for this class.
    private final StudentPaymentService studentPaymentService;
// Field: stores enrollmentRepository for this class.
    private final EnrollmentRepository enrollmentRepository;

// Constructor: Spring injects dependencies here.
    public StudentPaymentController(StudentPaymentService studentPaymentService,
                                    EnrollmentRepository enrollmentRepository) {
        this.studentPaymentService = studentPaymentService;
        this.enrollmentRepository = enrollmentRepository;
    }

// @GetMapping handles HTTP GET requests for the given path.
    @GetMapping
// Endpoint handler: reads inputs, calls service layer, and returns a response/view.
    public String payments(@AuthenticationPrincipal CustomUserDetails principal,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (!canAccessPayments(principal)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Payments are available after your application is approved.");
            return "redirect:/dashboard";
        }
        var dashboard = studentPaymentService.getPaymentDashboard(principal.getUser());
        model.addAttribute("currentPath", "/payments");
        model.addAttribute("userName", principal.getUser().getFullName());
        model.addAttribute("paymentData", dashboard);
        model.addAttribute("paymentProviderLabel", studentPaymentService.getPaymentProviderLabel());
        model.addAttribute("mockProviderActive", studentPaymentService.isMockProviderActive());
        return "payments/list";
    }

// @GetMapping handles HTTP GET requests for the given path.
    @GetMapping("/home")
// Endpoint handler: validates input, calls service layer, and returns a response/view.
    public String paymentsAliasHome() {
        return "redirect:/payments";
    }

// @GetMapping handles HTTP GET requests for the given path.
    @GetMapping("/{invoiceId}/checkout")
// Endpoint handler for GET /{invoiceId}/checkout: reads inputs, calls service, returns a view/JSON.
    public String checkoutPageGet(@AuthenticationPrincipal CustomUserDetails principal,
// @PathVariable binds a URL path segment to a method parameter.
                                  @PathVariable Long invoiceId,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        return renderCheckout(principal, invoiceId, model, redirectAttributes);
    }

// @PostMapping handles HTTP POST requests for the given path.
    @PostMapping("/{invoiceId}/checkout")
// Endpoint handler for POST /{invoiceId}/checkout: reads inputs, calls service, returns a view/JSON.
    public String checkoutPage(@AuthenticationPrincipal CustomUserDetails principal,
// @PathVariable binds a URL path segment to a method parameter.
                               @PathVariable Long invoiceId,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        return renderCheckout(principal, invoiceId, model, redirectAttributes);
    }

// Endpoint handler: reads inputs, calls service layer, and returns a response/view.
    private String renderCheckout(CustomUserDetails principal,
                                  Long invoiceId,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        try {
            if (!canAccessPayments(principal)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Payments are available after your application is approved.");
                return "redirect:/dashboard";
            }
            var session = studentPaymentService.createCheckoutSession(principal.getUser(), invoiceId);
            model.addAttribute("userName", principal.getUser().getFullName());
            model.addAttribute("checkoutSession", session);
            return "payments/checkout";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/payments";
        }
    }

// @PostMapping handles HTTP POST requests for the given path.
    @PostMapping("/{invoiceId}/mock/complete")
// Endpoint handler for POST /{invoiceId}/mock/complete: reads inputs, calls service, returns a view/JSON.
    public String completeMock(@AuthenticationPrincipal CustomUserDetails principal,
// @PathVariable binds a URL path segment to a method parameter.
                               @PathVariable Long invoiceId,
// @RequestParam binds a query parameter or form field to a method parameter.
                               @RequestParam("gateway_order_id") String gatewayOrderId,
// @RequestParam binds a query parameter or form field to a method parameter.
                               @RequestParam("result") String result,
// @RequestParam binds a query parameter or form field to a method parameter.
                               @RequestParam(value = "paymentMode", required = false) String paymentMode,
// @RequestParam binds a query parameter or form field to a method parameter.
                               @RequestParam(value = "upiId", required = false) String upiId,
// @RequestParam binds a query parameter or form field to a method parameter.
                               @RequestParam(value = "cardNumber", required = false) String cardNumber,
// @RequestParam binds a query parameter or form field to a method parameter.
                               @RequestParam(value = "cardHolderName", required = false) String cardHolderName,
// @RequestParam binds a query parameter or form field to a method parameter.
                               @RequestParam(value = "cardExpiry", required = false) String cardExpiry,
// @RequestParam binds a query parameter or form field to a method parameter.
                               @RequestParam(value = "cardCvv", required = false) String cardCvv,
                               RedirectAttributes redirectAttributes) {
        try {
            if (!canAccessPayments(principal)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Payments are available after your application is approved.");
                return "redirect:/dashboard";
            }
            boolean success = "SUCCESS".equalsIgnoreCase(result);
            StudentPaymentService.MockPaymentDetails details = new StudentPaymentService.MockPaymentDetails(
                    paymentMode, upiId, cardNumber, cardHolderName, cardExpiry, cardCvv
            );
            Payment payment = studentPaymentService.completeMockPayment(
                    principal.getUser(),
                    invoiceId,
                    gatewayOrderId,
                    success,
                    details
            );
            redirectAttributes.addFlashAttribute("successMessage",
                    "Mock payment completed. Transaction ID: " + payment.getTransactionId());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/payments";
    }

// @PostMapping handles HTTP POST requests for the given path.
    @PostMapping("/{invoiceId}/razorpay/verify")
// Endpoint handler for POST /{invoiceId}/razorpay/verify: reads inputs, calls service, returns a view/JSON.
    public String verifyRazorpay(@AuthenticationPrincipal CustomUserDetails principal,
// @PathVariable binds a URL path segment to a method parameter.
                                 @PathVariable Long invoiceId,
// @RequestParam binds a query parameter or form field to a method parameter.
                                 @RequestParam("razorpay_order_id") String razorpayOrderId,
// @RequestParam binds a query parameter or form field to a method parameter.
                                 @RequestParam("razorpay_payment_id") String razorpayPaymentId,
// @RequestParam binds a query parameter or form field to a method parameter.
                                 @RequestParam("razorpay_signature") String razorpaySignature,
                                 RedirectAttributes redirectAttributes) {
        try {
            if (!canAccessPayments(principal)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Payments are available after your application is approved.");
                return "redirect:/dashboard";
            }
            Payment payment = studentPaymentService.verifyRazorpayPayment(
                    principal.getUser(),
                    invoiceId,
                    razorpayOrderId,
                    razorpayPaymentId,
                    razorpaySignature
            );
            redirectAttributes.addFlashAttribute("successMessage",
                    "Payment verified successfully. Transaction ID: " + payment.getTransactionId());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/payments";
    }

// Endpoint handler: validates input, calls service layer, and returns a response/view.
    private boolean canAccessPayments(CustomUserDetails principal) {
        if (principal == null || principal.getUser() == null) {
            return false;
        }
        return enrollmentRepository.findByStudentId(principal.getUser().getId()).stream()
                .anyMatch(e -> e.getStatus() == Enrollment.EnrollmentStatus.APPROVED
                        || e.getStatus() == Enrollment.EnrollmentStatus.ENROLLED);
    }
}
