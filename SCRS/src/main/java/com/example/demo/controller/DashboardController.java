package com.example.demo.controller;

import com.example.demo.entity.Enrollment;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;

@Controller
/**
 * Controller for the authenticated user dashboard.
 * Adds simple user info to the model for display.
 */
public class DashboardController {

    private final EnrollmentRepository enrollmentRepository;

    public DashboardController(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    // Show dashboard page and populate user name/email when available.
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model, HttpServletRequest request) {
        List<Enrollment> enrollments = Collections.emptyList();
        model.addAttribute("currentPath", request.getRequestURI());

        if (userDetails != null) {
            model.addAttribute("userName", userDetails.getUser().getFullName());
            model.addAttribute("userEmail", userDetails.getUsername());
            boolean isAuthority = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_AUTHORITY") || a.getAuthority().startsWith("ROLE_AUTHORITY_"));
            model.addAttribute("isAuthority", isAuthority);

            if (!isAuthority) {
                // Fetch student enrollments
                enrollments = enrollmentRepository.findByStudentId(userDetails.getUser().getId());
            }
        }
        model.addAttribute("enrollments", enrollments);
        return "dashboard";
    }

    // Separate mapping intended for college authority post-login redirect. Reuses `dashboard` template
    @GetMapping("/dashboard/authority")
    public String authorityDashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
        if (userDetails != null) {
            model.addAttribute("userName", userDetails.getUser().getFullName());
            model.addAttribute("userEmail", userDetails.getUsername());
            model.addAttribute("isAuthority", true);
        }
        model.addAttribute("enrollments", Collections.emptyList());
        return "dashboard";
    }
}
