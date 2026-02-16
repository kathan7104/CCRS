package com.example.demo.controller;

import com.example.demo.entity.FeeStructure;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.FeeStructureService;
import com.example.demo.service.ReportingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/staff")
public class StaffController {
    private final FeeStructureService feeStructureService;
    private final ReportingService reportingService;

    public StaffController(FeeStructureService feeStructureService, ReportingService reportingService) {
        this.feeStructureService = feeStructureService;
        this.reportingService = reportingService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        model.addAttribute("userName", principal.getUser().getFullName());
        model.addAttribute("activeFee", feeStructureService.getActive());
        model.addAttribute("feeCount", feeStructureService.getAll().size());
        model.addAttribute("snapshot", reportingService.getFinancialSnapshot());
        return "staff/dashboard";
    }

    @GetMapping("/fee-structures")
    public String feeStructures(Model model) {
        model.addAttribute("fees", feeStructureService.getAll());
        model.addAttribute("logs", feeStructureService.getRecentAuditLogs());
        return "staff/fees/list";
    }

    @GetMapping("/fee-structures/new")
    public String newFeeStructure(Model model) {
        FeeStructure fee = new FeeStructure();
        fee.setEffectiveFrom(LocalDate.now());
        model.addAttribute("fee", fee);
        return "staff/fees/form";
    }

    @PostMapping("/fee-structures")
    public String createFeeStructure(@AuthenticationPrincipal CustomUserDetails principal,
                                     @RequestParam String name,
                                     @RequestParam BigDecimal costPerCredit,
                                     @RequestParam BigDecimal labFee,
                                     @RequestParam BigDecimal differentialFee,
                                     @RequestParam BigDecimal latePenalty,
                                     @RequestParam LocalDate effectiveFrom,
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

    @GetMapping("/fee-structures/{id}/edit")
    public String editFeeStructure(@PathVariable Long id, Model model) {
        model.addAttribute("fee", feeStructureService.getById(id));
        return "staff/fees/form";
    }

    @PostMapping("/fee-structures/{id}")
    public String updateFeeStructure(@PathVariable Long id,
                                     @AuthenticationPrincipal CustomUserDetails principal,
                                     @RequestParam String name,
                                     @RequestParam BigDecimal costPerCredit,
                                     @RequestParam BigDecimal labFee,
                                     @RequestParam BigDecimal differentialFee,
                                     @RequestParam BigDecimal latePenalty,
                                     @RequestParam LocalDate effectiveFrom,
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

    @PostMapping("/fee-structures/{id}/delete")
    public String deleteFeeStructure(@PathVariable Long id,
                                     @AuthenticationPrincipal CustomUserDetails principal,
                                     RedirectAttributes redirectAttributes) {
        feeStructureService.delete(id, principal.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "Fee structure deleted.");
        return "redirect:/staff/fee-structures";
    }

    @GetMapping("/reports")
    public String reports(@RequestParam(defaultValue = "unpaid") String reportType, Model model) {
        model.addAttribute("reportType", reportType);
        model.addAttribute("snapshot", reportingService.getFinancialSnapshot());
        model.addAttribute("unpaidRows", reportingService.getUnpaidStudentsReport());
        model.addAttribute("payments", reportingService.getReconciliationReport());
        return "staff/reports";
    }
}
