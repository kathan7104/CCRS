/*
 * File: src/main/java/com/example/demo/controller/StaffController.java
 * Role: Controller
 * MVC Fit: Handles HTTP requests in the MVC layer.
 * Connects To: Client -> Controller -> Service -> Repository -> Database -> Response
 */

package com.example.demo.controller;

import com.example.demo.entity.FeeStructure;
import com.example.demo.entity.Payment;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.StaffBillingService;
import com.example.demo.service.FeeStructureService;
import com.example.demo.service.ReportingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;

// Class Summary: Controller class that handles HTTP requests in the MVC layer.
// @Controller marks this class as an MVC controller that returns views.
@Controller
// @RequestMapping defines a common URL prefix for all endpoints in this controller.
@RequestMapping("/staff")
public class StaffController {
// Field: stores feeStructureService for this class.
    private final FeeStructureService feeStructureService;
// Field: stores reportingService for this class.
    private final ReportingService reportingService;
// Field: stores staffBillingService for this class.
    private final StaffBillingService staffBillingService;

// Constructor: Spring injects dependencies here.
    public StaffController(FeeStructureService feeStructureService,
                           ReportingService reportingService,
                           StaffBillingService staffBillingService) {
        this.feeStructureService = feeStructureService;
        this.reportingService = reportingService;
        this.staffBillingService = staffBillingService;
    }

// @GetMapping handles HTTP GET requests for the given path.
    @GetMapping("/dashboard")
// Endpoint handler: validates input, calls service layer, and returns a response/view.
    public String dashboard(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        model.addAttribute("userName", principal.getUser().getFullName());
        model.addAttribute("activeFee", feeStructureService.getActive());
        model.addAttribute("feeCount", feeStructureService.getAll().size());
        model.addAttribute("snapshot", reportingService.getFinancialSnapshot());
        return "staff/dashboard";
    }

// @GetMapping handles HTTP GET requests for the given path.
    @GetMapping("/fee-structures")
// Endpoint handler: validates input, calls service layer, and returns a response/view.
    public String feeStructures(Model model) {
        model.addAttribute("fees", feeStructureService.getAll());
        model.addAttribute("logs", feeStructureService.getRecentAuditLogs());
        return "staff/fees/list";
    }

// @GetMapping handles HTTP GET requests for the given path.
    @GetMapping("/fee-structures/new")
// Endpoint handler: validates input, calls service layer, and returns a response/view.
    public String newFeeStructure(Model model) {
        FeeStructure fee = new FeeStructure();
        fee.setEffectiveFrom(LocalDate.now());
        model.addAttribute("fee", fee);
        return "staff/fees/form";
    }

// @PostMapping handles HTTP POST requests for the given path.
    @PostMapping("/fee-structures")
// Endpoint handler for POST /fee-structures: reads inputs, calls service, returns a view/JSON.
    public String createFeeStructure(@AuthenticationPrincipal CustomUserDetails principal,
// @RequestParam binds a query parameter or form field to a method parameter.
                                     @RequestParam String name,
// @RequestParam binds a query parameter or form field to a method parameter.
                                     @RequestParam BigDecimal costPerCredit,
// @RequestParam binds a query parameter or form field to a method parameter.
                                     @RequestParam BigDecimal labFee,
// @RequestParam binds a query parameter or form field to a method parameter.
                                     @RequestParam BigDecimal differentialFee,
// @RequestParam binds a query parameter or form field to a method parameter.
                                     @RequestParam BigDecimal latePenalty,
// @RequestParam binds a query parameter or form field to a method parameter.
                                     @RequestParam LocalDate effectiveFrom,
// @RequestParam binds a query parameter or form field to a method parameter.
                                     @RequestParam(defaultValue = "false") boolean active,
                                     RedirectAttributes redirectAttributes) {
        FeeStructure fee = new FeeStructure();
        fee.setName(name);
        fee.setCostPerCredit(costPerCredit);
        fee.setLabFee(labFee);
        fee.setDifferentialFee(differentialFee);
        fee.setLatePenalty(latePenalty);
        fee.setEffectiveFrom(effectiveFrom);
        fee.setActive(active);
        feeStructureService.createOrUpdate(fee, principal.getUsername(), "CREATE");
        redirectAttributes.addFlashAttribute("successMessage", "Fee structure created.");
        return "redirect:/staff/fee-structures";
    }

// @GetMapping handles HTTP GET requests for the given path.
    @GetMapping("/fee-structures/{id}/edit")
// Endpoint handler: validates input, calls service layer, and returns a response/view.
    public String editFeeStructure(@PathVariable Long id, Model model) {
        model.addAttribute("fee", feeStructureService.getById(id));
        return "staff/fees/form";
    }

// @PostMapping handles HTTP POST requests for the given path.
    @PostMapping("/fee-structures/{id}")
// Endpoint handler for POST /fee-structures/{id}: reads inputs, calls service, returns a view/JSON.
    public String updateFeeStructure(@PathVariable Long id,
                                     @AuthenticationPrincipal CustomUserDetails principal,
// @RequestParam binds a query parameter or form field to a method parameter.
                                     @RequestParam String name,
// @RequestParam binds a query parameter or form field to a method parameter.
                                     @RequestParam BigDecimal costPerCredit,
// @RequestParam binds a query parameter or form field to a method parameter.
                                     @RequestParam BigDecimal labFee,
// @RequestParam binds a query parameter or form field to a method parameter.
                                     @RequestParam BigDecimal differentialFee,
// @RequestParam binds a query parameter or form field to a method parameter.
                                     @RequestParam BigDecimal latePenalty,
// @RequestParam binds a query parameter or form field to a method parameter.
                                     @RequestParam LocalDate effectiveFrom,
// @RequestParam binds a query parameter or form field to a method parameter.
                                     @RequestParam(defaultValue = "false") boolean active,
                                     RedirectAttributes redirectAttributes) {
        FeeStructure fee = feeStructureService.getById(id);
        fee.setName(name);
        fee.setCostPerCredit(costPerCredit);
        fee.setLabFee(labFee);
        fee.setDifferentialFee(differentialFee);
        fee.setLatePenalty(latePenalty);
        fee.setEffectiveFrom(effectiveFrom);
        fee.setActive(active);
        feeStructureService.createOrUpdate(fee, principal.getUsername(), "UPDATE");
        redirectAttributes.addFlashAttribute("successMessage", "Fee structure updated.");
        return "redirect:/staff/fee-structures";
    }

// @PostMapping handles HTTP POST requests for the given path.
    @PostMapping("/fee-structures/{id}/delete")
// Endpoint handler for POST /fee-structures/{id}/delete: reads inputs, calls service, returns a view/JSON.
    public String deleteFeeStructure(@PathVariable Long id,
                                     @AuthenticationPrincipal CustomUserDetails principal,
                                     RedirectAttributes redirectAttributes) {
        feeStructureService.delete(id, principal.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "Fee structure deleted.");
        return "redirect:/staff/fee-structures";
    }

// @GetMapping handles HTTP GET requests for the given path.
    @GetMapping("/reports")
// Endpoint handler: validates input, calls service layer, and returns a response/view.
    public String reports(@RequestParam(defaultValue = "unpaid") String reportType, Model model) {
        model.addAttribute("reportType", reportType);
        model.addAttribute("snapshot", reportingService.getFinancialSnapshot());
        model.addAttribute("unpaidRows", reportingService.getUnpaidStudentsReport());
        model.addAttribute("payments", reportingService.getReconciliationReport());
        return "staff/reports";
    }

// @GetMapping handles HTTP GET requests for the given path.
    @GetMapping("/invoices")
// Endpoint handler: validates input, calls service layer, and returns a response/view.
    public String invoices(Model model) {
        model.addAttribute("students", staffBillingService.getActiveStudents());
        model.addAttribute("invoiceRows", staffBillingService.getInvoiceRows());
        return "staff/invoices/list";
    }

// @PostMapping handles HTTP POST requests for the given path.
    @PostMapping("/invoices/generate")
// Endpoint handler for POST /invoices/generate: reads inputs, calls service, returns a view/JSON.
    public String generateSemesterInvoice(@RequestParam Long studentId,
// @RequestParam binds a query parameter or form field to a method parameter.
                                          @RequestParam int semester,
                                          RedirectAttributes redirectAttributes) {
        try {
            var invoice = staffBillingService.generateSemesterInvoice(studentId, semester);
            redirectAttributes.addFlashAttribute("successMessage", "Invoice ready: " + invoice.getInvoiceNumber());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/staff/invoices";
    }

// @PostMapping handles HTTP POST requests for the given path.
    @PostMapping("/invoices/{invoiceId}/offline-payment")
// Endpoint handler for POST /invoices/{invoiceId}/offline-payment: reads inputs, calls service, returns a view/JSON.
    public String collectOffline(@PathVariable Long invoiceId,
// @RequestParam binds a query parameter or form field to a method parameter.
                                 @RequestParam Payment.PaymentMethod method,
// @RequestParam binds a query parameter or form field to a method parameter.
                                 @RequestParam(required = false) BigDecimal amount,
// @RequestParam binds a query parameter or form field to a method parameter.
                                 @RequestParam(required = false) String chequeNumber,
// @RequestParam binds a query parameter or form field to a method parameter.
                                 @RequestParam(required = false) String chequeBankName,
// @RequestParam binds a query parameter or form field to a method parameter.
                                 @RequestParam(required = false) String chequeIfscCode,
                                 RedirectAttributes redirectAttributes) {
        try {
            var payment = staffBillingService.recordOfflinePayment(
                    invoiceId, method, amount, chequeNumber, chequeBankName, chequeIfscCode);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Offline payment recorded. Transaction ID: " + payment.getTransactionId());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/staff/invoices";
    }
}
