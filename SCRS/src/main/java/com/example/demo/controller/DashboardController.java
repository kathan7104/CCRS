package com.example.demo.controller;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.FacultySubjectAssignment;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.FacultySubjectAssignmentRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.StudentAccessService;
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
    private final FacultySubjectAssignmentRepository facultySubjectAssignmentRepository;
    private final StudentAccessService studentAccessService;
    public DashboardController(EnrollmentRepository enrollmentRepository,
                               FacultySubjectAssignmentRepository facultySubjectAssignmentRepository,
                               StudentAccessService studentAccessService) {
        this.enrollmentRepository = enrollmentRepository;
        this.facultySubjectAssignmentRepository = facultySubjectAssignmentRepository;
        this.studentAccessService = studentAccessService;
    }
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model, HttpServletRequest request) {
        if (userDetails != null && userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))) {
            if (!studentAccessService.hasActiveEnrollment(userDetails.getUser())) {
                model.addAttribute("errorMessage", "Your student account is temporary. Please complete course registration and wait for approval.");
                return "redirect:/courses";
            }
        }
        List<Enrollment> enrollments = Collections.emptyList();
        List<FacultySubjectAssignment> assignedSubjects = Collections.emptyList();
        boolean isFaculty = false;
        String overviewText = "Welcome to the KOR Institute of Technology Student Portal.";
        model.addAttribute("currentPath", request.getRequestURI());
        if (userDetails != null) {
            model.addAttribute("userName", userDetails.getUser().getFullName());
            model.addAttribute("userEmail", userDetails.getUsername());
            boolean isAuthority = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_AUTHORITY") || a.getAuthority().startsWith("ROLE_AUTHORITY_"));
            model.addAttribute("isAuthority", isAuthority);
            isFaculty = userDetails.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_AUTHORITY_FACULTY".equals(a.getAuthority()));
            boolean hasStudentAcademicAccess = !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))
                    || studentAccessService.hasActiveEnrollment(userDetails.getUser());
            model.addAttribute("hasStudentAcademicAccess", hasStudentAcademicAccess);
            overviewText = resolveOverviewText(userDetails);
            if (isFaculty) {
                assignedSubjects = facultySubjectAssignmentRepository.findByFacultyIdWithSubject(userDetails.getUser().getId());
            } else if (!isAuthority) {
                enrollments = enrollmentRepository.findByStudentId(userDetails.getUser().getId());
            }
        }
        // 8. Put data on the page so the user can see it
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("assignedSubjects", assignedSubjects);
        model.addAttribute("isFaculty", isFaculty);
        model.addAttribute("overviewText", overviewText);
        // 9. Send the result back to the screen
        return "dashboard";
    }
    @GetMapping("/dashboard/authority")
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
                ? "Welcome to the KOR Institute of Technology Student Portal."
                : resolveOverviewText(userDetails));
        // 7. Send the result back to the screen
        return "dashboard";
    }

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

