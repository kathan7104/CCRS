package com.example.demo.controller;

import com.example.demo.entity.Payment;
import com.example.demo.security.CustomUserDetails;
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

@Controller
@RequestMapping({"/payments", "/payment"})
public class StudentPaymentController {
    private final StudentPaymentService studentPaymentService;

    public StudentPaymentController(StudentPaymentService studentPaymentService) {
        this.studentPaymentService = studentPaymentService;
    }

    @GetMapping
    public String payments(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        var dashboard = studentPaymentService.getPaymentDashboard(principal.getUser());
        model.addAttribute("currentPath", "/payments");
        model.addAttribute("userName", principal.getUser().getFullName());
        model.addAttribute("paymentData", dashboard);
        model.addAttribute("paymentProviderLabel", studentPaymentService.getPaymentProviderLabel());
        model.addAttribute("mockProviderActive", studentPaymentService.isMockProviderActive());
        return "payments/list";
    }

    @GetMapping("/home")
    public String paymentsAliasHome() {
        return "redirect:/payments";
    }

    @GetMapping("/{invoiceId}/checkout")
    public String checkoutPageGet(@AuthenticationPrincipal CustomUserDetails principal,
                                  @PathVariable Long invoiceId,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        return renderCheckout(principal, invoiceId, model, redirectAttributes);
    }

    @PostMapping("/{invoiceId}/checkout")
    public String checkoutPage(@AuthenticationPrincipal CustomUserDetails principal,
                               @PathVariable Long invoiceId,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        return renderCheckout(principal, invoiceId, model, redirectAttributes);
    }

    private String renderCheckout(CustomUserDetails principal,
                                  Long invoiceId,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        try {
            var session = studentPaymentService.createCheckoutSession(principal.getUser(), invoiceId);
            model.addAttribute("userName", principal.getUser().getFullName());
            model.addAttribute("checkoutSession", session);
            return "payments/checkout";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/payments";
        }
    }

    @PostMapping("/{invoiceId}/mock/complete")
    public String completeMock(@AuthenticationPrincipal CustomUserDetails principal,
                               @PathVariable Long invoiceId,
                               @RequestParam("gateway_order_id") String gatewayOrderId,
                               @RequestParam("result") String result,
                               @RequestParam(value = "paymentMode", required = false) String paymentMode,
                               @RequestParam(value = "upiId", required = false) String upiId,
                               @RequestParam(value = "cardNumber", required = false) String cardNumber,
                               @RequestParam(value = "cardHolderName", required = false) String cardHolderName,
                               @RequestParam(value = "cardExpiry", required = false) String cardExpiry,
                               @RequestParam(value = "cardCvv", required = false) String cardCvv,
                               RedirectAttributes redirectAttributes) {
        try {
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

    @PostMapping("/{invoiceId}/razorpay/verify")
    public String verifyRazorpay(@AuthenticationPrincipal CustomUserDetails principal,
                                 @PathVariable Long invoiceId,
                                 @RequestParam("razorpay_order_id") String razorpayOrderId,
                                 @RequestParam("razorpay_payment_id") String razorpayPaymentId,
                                 @RequestParam("razorpay_signature") String razorpaySignature,
                                 RedirectAttributes redirectAttributes) {
        try {
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
}
