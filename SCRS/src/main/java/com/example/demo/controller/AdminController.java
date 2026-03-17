package com.example.demo.controller;

import com.example.demo.entity.Department;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.User;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.AdminWorkflowService;
import com.example.demo.service.ReportingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final String SUPER_ADMIN_ROLE = "AUTHORITY_SUPER_ADMIN";
    private static final Set<String> MANAGED_ROLES = Set.of("AUTHORITY_ADMIN", "AUTHORITY_DIRECTOR", "AUTHORITY_STAFF", SUPER_ADMIN_ROLE);

    private final EnrollmentRepository enrollmentRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final AdminWorkflowService adminWorkflowService;
    private final ReportingService reportingService;
    private final PasswordEncoder passwordEncoder;

    public AdminController(EnrollmentRepository enrollmentRepository,
                           DepartmentRepository departmentRepository,
                           UserRepository userRepository,
                           AdminWorkflowService adminWorkflowService,
                           ReportingService reportingService,
                           PasswordEncoder passwordEncoder) {
        this.enrollmentRepository = enrollmentRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.adminWorkflowService = adminWorkflowService;
        this.reportingService = reportingService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute("userName", userDetails.getUser().getFullName());
        model.addAttribute("pendingApprovals", enrollmentRepository.findByStatusOrderByRegisteredAtDesc(Enrollment.EnrollmentStatus.PENDING).size());
        model.addAttribute("superUsers", getManagedUsers().size());
        model.addAttribute("snapshot", reportingService.getFinancialSnapshot());
        return "admin/dashboard";
    }

    @GetMapping("/enrollments")
    public String enrollmentApprovals(Model model) {
        List<Enrollment> pending = enrollmentRepository.findByStatusOrderByRegisteredAtDesc(Enrollment.EnrollmentStatus.PENDING);
        model.addAttribute("pendingEnrollments", pending);
        return "admin/enrollments";
    }

    @PostMapping("/enrollments/{id}/approve")
    public String approve(@PathVariable Long id,
                          @RequestParam(required = false) String note,
                          RedirectAttributes redirectAttributes) {
        try {
            adminWorkflowService.approveEnrollment(id, note);
            redirectAttributes.addFlashAttribute("successMessage", "Application approved. Status will become ENROLLED after first semester fee is paid.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/enrollments";
    }

    @PostMapping("/enrollments/{id}/reject")
    public String reject(@PathVariable Long id,
                         @RequestParam(required = false) String note,
                         RedirectAttributes redirectAttributes) {
        try {
            adminWorkflowService.rejectEnrollment(id, note);
            redirectAttributes.addFlashAttribute("successMessage", "Enrollment rejected and seat released.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/enrollments";
    }

    @GetMapping("/users")
    public String listSuperUsers(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("users", getManagedUsers());
        model.addAttribute("currentUserId", userDetails != null && userDetails.getUser() != null ? userDetails.getUser().getId() : -1);
        return "admin/users/list";
    }

    @GetMapping("/users/new")
    public String createUserForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("departments", getActiveDepartmentNames());
        String selectedRole = "AUTHORITY_STAFF";
        if (isSuperAdmin(userDetails.getUser())) {
            selectedRole = "AUTHORITY_ADMIN";
        }
        model.addAttribute("selectedRole", selectedRole);
        model.addAttribute("isSuperAdmin", isSuperAdmin(userDetails.getUser()));
        return "admin/users/form";
    }

    @PostMapping("/users")
    public String createUser(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @ModelAttribute User user,
                             @RequestParam("role") String role,
                             RedirectAttributes redirectAttributes) {
        if (!MANAGED_ROLES.contains(role)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid role");
            return "redirect:/admin/users";
        }
        if (SUPER_ADMIN_ROLE.equals(role) && !isSuperAdmin(userDetails.getUser())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only super admin can create another super admin.");
            return "redirect:/admin/users";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmailVerified(true);
        user.setMobileVerified(true);
        user.getRoles().clear();
        user.getRoles().add(role);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "User created.");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        model.addAttribute("user", user);
        model.addAttribute("selectedRole", user.getRoles().stream().findFirst().orElse("AUTHORITY_STAFF"));
        model.addAttribute("departments", getActiveDepartmentNames());
        return "admin/users/form";
    }

    @PostMapping("/users/{id}")
    public String updateUser(@AuthenticationPrincipal CustomUserDetails currentUser,
                             @PathVariable Long id,
                             @RequestParam String fullName,
                             @RequestParam String email,
                             @RequestParam String mobileNumber,
                             @RequestParam String department,
                             @RequestParam String role,
                             @RequestParam(required = false) String password,
                             RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!MANAGED_ROLES.contains(role)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid role");
            return "redirect:/admin/users";
        }
        if (isSuperAdmin(user) && !isSuperAdmin(currentUser.getUser())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only super admin can modify super admin accounts.");
            return "redirect:/admin/users";
        }
        if (isSelf(currentUser.getUser(), user) && !isSuperAdmin(currentUser.getUser()) && !SUPER_ADMIN_ROLE.equals(role)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot demote yourself from super admin role.");
            return "redirect:/admin/users";
        }
        user.setFullName(fullName);
        user.setEmail(email);
        user.setMobileNumber(mobileNumber);
        user.setDepartment(department);
        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }
        user.getRoles().clear();
        user.getRoles().add(role);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "User updated.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@AuthenticationPrincipal CustomUserDetails currentUser,
                             @PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (isSuperAdmin(user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete super admin.");
            return "redirect:/admin/users";
        }
        if (isSelf(currentUser.getUser(), user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete yourself.");
            return "redirect:/admin/users";
        }
        userRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/deactivate")
    public String deactivateUser(@AuthenticationPrincipal CustomUserDetails currentUser,
                                 @PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (isSuperAdmin(user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot deactivate super admin.");
            return "redirect:/admin/users";
        }
        if (isSelf(currentUser.getUser(), user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot deactivate yourself.");
            return "redirect:/admin/users";
        }
        user.setActive(false);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "User deactivated.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/activate")
    public String activateUser(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setActive(true);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "User activated.");
        return "redirect:/admin/users";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("snapshot", reportingService.getFinancialSnapshot());
        model.addAttribute("unpaidRows", reportingService.getUnpaidStudentsReport());
        model.addAttribute("payments", reportingService.getReconciliationReport());
        return "admin/reports";
    }

    @GetMapping("/departments")
    public String departments(Model model) {
        model.addAttribute("departments", departmentRepository.findAll().stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .toList());
        return "admin/departments";
    }

    @PostMapping("/departments")
    public String createDepartment(@RequestParam String name, RedirectAttributes redirectAttributes) {
        String cleaned = name == null ? "" : name.trim();
        if (cleaned.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Department name is required.");
            return "redirect:/admin/departments";
        }
        if (departmentRepository.findByNameIgnoreCase(cleaned).isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Department already exists.");
            return "redirect:/admin/departments";
        }
        Department department = new Department();
        department.setName(cleaned);
        department.setActive(true);
        departmentRepository.save(department);
        redirectAttributes.addFlashAttribute("successMessage", "Department added.");
        return "redirect:/admin/departments";
    }

    @PostMapping("/departments/{id}/deactivate")
    public String deactivateDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));
        department.setActive(false);
        departmentRepository.save(department);
        redirectAttributes.addFlashAttribute("successMessage", "Department deactivated.");
        return "redirect:/admin/departments";
    }

    @RequestMapping(value = {"/departments/{id}/activate", "/departments/{id}/active"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String activateDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));
        department.setActive(true);
        departmentRepository.save(department);
        redirectAttributes.addFlashAttribute("successMessage", "Department activated.");
        return "redirect:/admin/departments";
    }

    private boolean hasRole(User user, String role) {
        return user.getRoles().stream().anyMatch(r -> r.equalsIgnoreCase(role));
    }

    private boolean isSuperAdmin(User user) {
        return hasRole(user, SUPER_ADMIN_ROLE);
    }

    private boolean isSelf(User currentUser, User target) {
        return currentUser != null && target != null && currentUser.getId() != null && currentUser.getId().equals(target.getId());
    }

    private List<User> getManagedUsers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(MANAGED_ROLES::contains))
                .toList();
    }

    private List<String> getActiveDepartmentNames() {
        return departmentRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(Department::getName)
                .toList();
    }
}
