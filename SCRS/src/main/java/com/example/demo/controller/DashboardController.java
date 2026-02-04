// Package declaration: groups related classes in a namespace.
package com.example.demo.controller;

// Import statement: brings a class into scope by name.
import com.example.demo.entity.Enrollment;
// Import statement: brings a class into scope by name.
import com.example.demo.repository.EnrollmentRepository;
// Import statement: brings a class into scope by name.
import com.example.demo.security.CustomUserDetails;
// Import statement: brings a class into scope by name.
import jakarta.servlet.http.HttpServletRequest;
// Import statement: brings a class into scope by name.
import org.springframework.security.core.annotation.AuthenticationPrincipal;
// Import statement: brings a class into scope by name.
import org.springframework.stereotype.Controller;
// Import statement: brings a class into scope by name.
import org.springframework.ui.Model;
// Import statement: brings a class into scope by name.
import org.springframework.web.bind.annotation.GetMapping;

// Import statement: brings a class into scope by name.
import java.util.Collections;
// Import statement: brings a class into scope by name.
import java.util.List;

// Annotation: adds metadata used by frameworks/tools.
@Controller
// Comment: explains code for readers.
/**
 // Comment: explains code for readers.
 * Controller for the authenticated user dashboard.
 // Comment: explains code for readers.
 * Adds simple user info to the model for display.
 // Comment: explains code for readers.
 */
// Class declaration: defines a new type.
public class DashboardController {

    // Field declaration: defines a member variable.
    private final EnrollmentRepository enrollmentRepository;

    // Opens a method/constructor/block.
    public DashboardController(EnrollmentRepository enrollmentRepository) {
        // Uses current object (this) to access a field or method.
        this.enrollmentRepository = enrollmentRepository;
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Show dashboard page and populate user name/email when available.
    // Annotation: adds metadata used by frameworks/tools.
    @GetMapping("/dashboard")
    // Opens a method/constructor/block.
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model, HttpServletRequest request) {
        // Statement: List<Enrollment> enrollments = Collections.emptyList();
        List<Enrollment> enrollments = Collections.emptyList();
        // Statement: model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("currentPath", request.getRequestURI());

        // Opens a method/constructor/block.
        if (userDetails != null) {
            // Statement: model.addAttribute("userName", userDetails.getUser().getFullName());
            model.addAttribute("userName", userDetails.getUser().getFullName());
            // Statement: model.addAttribute("userEmail", userDetails.getUsername());
            model.addAttribute("userEmail", userDetails.getUsername());
            // Statement: boolean isAuthority = userDetails.getAuthorities().stream().anyMatch(a -> a.g...
            boolean isAuthority = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_AUTHORITY") || a.getAuthority().startsWith("ROLE_AUTHORITY_"));
            // Statement: model.addAttribute("isAuthority", isAuthority);
            model.addAttribute("isAuthority", isAuthority);

            // Opens a method/constructor/block.
            if (!isAuthority) {
                // Comment: explains code for readers.
                // Fetch student enrollments
                // Statement: enrollments = enrollmentRepository.findByStudentId(userDetails.getUser().getI...
                enrollments = enrollmentRepository.findByStudentId(userDetails.getUser().getId());
            // Closes the current code block.
            }
        // Closes the current code block.
        }
        // Statement: model.addAttribute("enrollments", enrollments);
        model.addAttribute("enrollments", enrollments);
        // Return: sends a value back to the caller.
        return "dashboard";
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Separate mapping intended for college authority post-login redirect. Reuses `dashboard` template
    // Annotation: adds metadata used by frameworks/tools.
    @GetMapping("/dashboard/authority")
    // Opens a method/constructor/block.
    public String authorityDashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model, HttpServletRequest request) {
        // Statement: model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("currentPath", request.getRequestURI());
        // Opens a method/constructor/block.
        if (userDetails != null) {
            // Statement: model.addAttribute("userName", userDetails.getUser().getFullName());
            model.addAttribute("userName", userDetails.getUser().getFullName());
            // Statement: model.addAttribute("userEmail", userDetails.getUsername());
            model.addAttribute("userEmail", userDetails.getUsername());
            // Statement: model.addAttribute("isAuthority", true);
            model.addAttribute("isAuthority", true);
        // Closes the current code block.
        }
        // Statement: model.addAttribute("enrollments", Collections.emptyList());
        model.addAttribute("enrollments", Collections.emptyList());
        // Return: sends a value back to the caller.
        return "dashboard";
    // Closes the current code block.
    }
// Closes the current code block.
}
