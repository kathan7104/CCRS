/*
 * File: src/main/java/com/example/demo/controller/DashboardController.java
 * Role: Controller
 * MVC Fit: Handles HTTP requests in the MVC layer.
 * Connects To: Client -> Controller -> Service -> Repository -> Database -> Response
 */

package com.example.demo.controller;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.FacultySubjectAssignment;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.FacultySubjectAssignmentRepository;
import com.example.demo.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Collections;
import java.util.List;
// Class Summary: Controller class that handles HTTP requests in the MVC layer.
// @Controller marks this class as an MVC controller that returns views.
@Controller
public class DashboardController {
// Field: stores enrollmentRepository for this class.
    private final EnrollmentRepository enrollmentRepository;
// Field: stores facultySubjectAssignmentRepository for this class.
    private final FacultySubjectAssignmentRepository facultySubjectAssignmentRepository;
// Constructor: Spring injects dependencies here.
    public DashboardController(EnrollmentRepository enrollmentRepository,
                               FacultySubjectAssignmentRepository facultySubjectAssignmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.facultySubjectAssignmentRepository = facultySubjectAssignmentRepository;
    }
// @GetMapping handles HTTP GET requests for the given path.
    @GetMapping("/dashboard")
// Endpoint handler: validates input, calls service layer, and returns a response/view.
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model, HttpServletRequest request) {
        List<Enrollment> enrollments = Collections.emptyList();
        List<FacultySubjectAssignment> assignedSubjects = Collections.emptyList();
        boolean isFaculty = false;
        boolean canAccessPayments = false;
        boolean isTempStudent = false;
        String overviewText = "Welcome to the College Course Registration System.";
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
            isFaculty = userDetails.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_AUTHORITY_FACULTY".equals(a.getAuthority()));
            overviewText = resolveOverviewText(userDetails);
            // 6. Check a rule -> decide what to do next
            if (isFaculty) {
                assignedSubjects = facultySubjectAssignmentRepository.findByFacultyIdWithSubject(userDetails.getUser().getId());
            } else if (!isAuthority) {
                // 7. Get or save data in the database
                enrollments = enrollmentRepository.findByStudentId(userDetails.getUser().getId());
                canAccessPayments = enrollments.stream().anyMatch(e ->
                        e.getStatus() == Enrollment.EnrollmentStatus.APPROVED
                                || e.getStatus() == Enrollment.EnrollmentStatus.ENROLLED);
                isTempStudent = enrollments.stream().noneMatch(e -> e.getStatus() != Enrollment.EnrollmentStatus.CANCELLED);
            }
        }
        // 8. Put data on the page so the user can see it
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("assignedSubjects", assignedSubjects);
        model.addAttribute("isFaculty", isFaculty);
        model.addAttribute("overviewText", overviewText);
        model.addAttribute("canAccessPayments", canAccessPayments);
        model.addAttribute("isTempStudent", isTempStudent);
        // 9. Send the result back to the screen
        return "dashboard";
    }
// @GetMapping handles HTTP GET requests for the given path.
    @GetMapping("/dashboard/authority")
// Endpoint handler: validates input, calls service layer, and returns a response/view.
    public String authorityDashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model, HttpServletRequest request) {
        if (userDetails != null && userDetails.getAuthorities().stream()
                .anyMatch(a -> "ROLE_AUTHORITY_FACULTY".equals(a.getAuthority()))) {
            return "redirect:/faculty/roster";
        }
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
        model.addAttribute("assignedSubjects", Collections.emptyList());
        model.addAttribute("isFaculty", false);
        model.addAttribute("overviewText", userDetails == null
                ? "Welcome to the College Course Registration System."
                : resolveOverviewText(userDetails));
        model.addAttribute("canAccessPayments", false);
        model.addAttribute("isTempStudent", false);
        // 7. Send the result back to the screen
        return "dashboard";
    }

// Endpoint handler: validates input, calls service layer, and returns a response/view.
    private String resolveOverviewText(CustomUserDetails userDetails) {
        boolean isFaculty = userDetails.getAuthorities().stream()
                .anyMatch(a -> "ROLE_AUTHORITY_FACULTY".equals(a.getAuthority()));
        if (isFaculty) {
            return "Use this dashboard to review your assigned subjects and departmental roster.";
        }
        boolean isDirector = userDetails.getAuthorities().stream()
                .anyMatch(a -> "ROLE_AUTHORITY_DIRECTOR".equals(a.getAuthority()));
        if (isDirector) {
            return "Use this dashboard to manage courses, faculty assignments, and department operations.";
        }
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> "ROLE_AUTHORITY_ADMIN".equals(a.getAuthority()));
        if (isAdmin) {
            return "Use this dashboard to manage users, enrollment approvals, and academic reports.";
        }
        boolean isStaff = userDetails.getAuthorities().stream()
                .anyMatch(a -> "ROLE_AUTHORITY_STAFF".equals(a.getAuthority()));
        if (isStaff) {
            return "Use this dashboard to manage fee structures, invoices, and payment operations.";
        }
        return "Use this dashboard to browse courses and track your application and payment progress.";
    }
}
