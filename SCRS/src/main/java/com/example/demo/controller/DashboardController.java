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
public class DashboardController {
    private final EnrollmentRepository enrollmentRepository;
    public DashboardController(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model, HttpServletRequest request) {
        List<Enrollment> enrollments = Collections.emptyList();
        // 1. Put data on the page so the user can see it
        model.addAttribute("currentPath", request.getRequestURI());
        // 2. Check a rule -> decide what to do next
        if (userDetails != null) {
            // 3. Put data on the page so the user can see it
            model.addAttribute("userName", userDetails.getUser().getFullName());
            // 4. Put data on the page so the user can see it
            model.addAttribute("userEmail", userDetails.getUsername());
            boolean isAuthority = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_AUTHORITY") || a.getAuthority().startsWith("ROLE_AUTHORITY_"));
            // 5. Put data on the page so the user can see it
            model.addAttribute("isAuthority", isAuthority);
            // 6. Check a rule -> decide what to do next
            if (!isAuthority) {
                // 7. Get or save data in the database
                enrollments = enrollmentRepository.findByStudentId(userDetails.getUser().getId());
            }
        }
        // 8. Put data on the page so the user can see it
        model.addAttribute("enrollments", enrollments);
        // 9. Send the result back to the screen
        return "dashboard";
    }
    @GetMapping("/dashboard/authority")
    public String authorityDashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model, HttpServletRequest request) {
        // 1. Put data on the page so the user can see it
        model.addAttribute("currentPath", request.getRequestURI());
        // 2. Check a rule -> decide what to do next
        if (userDetails != null) {
            // 3. Put data on the page so the user can see it
            model.addAttribute("userName", userDetails.getUser().getFullName());
            // 4. Put data on the page so the user can see it
            model.addAttribute("userEmail", userDetails.getUsername());
            // 5. Put data on the page so the user can see it
            model.addAttribute("isAuthority", true);
        }
        // 6. Put data on the page so the user can see it
        model.addAttribute("enrollments", Collections.emptyList());
        // 7. Send the result back to the screen
        return "dashboard";
    }
}
