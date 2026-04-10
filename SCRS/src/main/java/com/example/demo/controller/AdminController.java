/*
 * File: src/main/java/com/example/demo/controller/AdminController.java
 * Role: Controller
 * MVC Fit: Handles HTTP requests in the MVC layer.
 * Connects To: Client -> Controller -> Service -> Repository -> Database -> Response
 */

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

// Class Summary: Controller class that handles HTTP requests in the MVC layer.
// @Controller marks this class as an MVC controller that returns views.
@Controller
// @RequestMapping defines a common URL prefix for all endpoints in this controller.
@RequestMapping("/admin")
public class AdminController {
// Field: stores SUPER_ADMIN_ROLE for this class.
    private static final String SUPER_ADMIN_ROLE = "AUTHORITY_SUPER_ADMIN";
// Field: stores MANAGED_ROLES for this class.
// Endpoint handler for REQUEST /admin: reads inputs, calls service, returns a view/JSON.
    private static final Set<String> MANAGED_ROLES = Set.of("AUTHORITY_ADMIN", "AUTHORITY_DIRECTOR", "AUTHORITY_STAFF", SUPER_ADMIN_ROLE);

// Field: stores enrollmentRepository for this class.
    private final EnrollmentRepository enrollmentRepository;
// Field: stores departmentRepository for this class.
    private final DepartmentRepository departmentRepository;
// Field: stores userRepository for this class.
    private final UserRepository userRepository;
// Field: stores adminWorkflowService for this class.
    private final AdminWorkflowService adminWorkflowService;
// Field: stores reportingService for this class.
    private final ReportingService reportingService;
// Field: stores passwordEncoder for this class.
    private final PasswordEncoder passwordEncoder;

// Constructor: Spring injects dependencies here.
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

// @GetMapping handles HTTP GET requests for the given path.
    @GetMapping("/dashboard")
// Endpoint handler: validates input, calls service layer, and returns a response/view.
    public String dashboard(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute("userName", userDetails.getUser().getFullName());
        model.addAttribute("pendingApprovals", enrollmentRepository.findByStatusOrderByRegisteredAtDesc(Enrollment.EnrollmentStatus.PENDING).size());
        model.addAttribute("superUsers", getManagedUsers().size());
        model.addAttribute("snapshot", reportingService.getFinancialSnapshot());
        return "admin/dashboard";
    }

// @GetMapping handles HTTP GET requests for the given path.
    @GetMapping("/enrollments")
// Endpoint handler: validates input, calls service layer, and returns a response/view.
    public String enrollmentApprovals(Model model) {
        List<Enrollment> pending = enrollmentRepository.findByStatusOrderByRegisteredAtDesc(Enrollment.EnrollmentStatus.PENDING);
        model.addAttribute("pendingEnrollments", pending);
        List<Enrollment> enrolled = enrollmentRepository.findByStatusOrderByRegisteredAtDesc(Enrollment.EnrollmentStatus.ENROLLED);
        model.addAttribute("enrolledEnrollments", enrolled);
        return "admin/enrollments";
    }

// @PostMapping handles HTTP POST requests for the given path.
    @PostMapping("/enrollments/{id}/approve")
// Endpoint handler for POST /enrollments/{id}/approve: reads inputs, calls service, returns a view/JSON.
    public String approve(@PathVariable Long id,
// @RequestParam binds a query parameter or form field to a method parameter.
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

// @PostMapping handles HTTP POST requests for the given path.
    @PostMapping("/enrollments/{id}/reject")
// Endpoint handler for POST /enrollments/{id}/reject: reads inputs, calls service, returns a view/JSON.
    public String reject(@PathVariable Long id,
// @RequestParam binds a query parameter or form field to a method parameter.
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

// @GetMapping handles HTTP GET requests for the given path.
    @GetMapping("/users")
// Endpoint handler: validates input, calls service layer, and returns a response/view.
    public String listSuperUsers(Model model, @AuthenticationPrincipal CustomUserDetails principal) {
        model.addAttribute("users", getManagedUsers());
        model.addAttribute("currentUserId", principal.getUser().getId());
        model.addAttribute("currentIsSuperAdmin", hasRole(principal.getUser(), SUPER_ADMIN_ROLE));
        model.addAttribute("superAdminRole", SUPER_ADMIN_ROLE);
        return "admin/users/list";
    }

// @GetMapping handles HTTP GET requests for the given path.
    @GetMapping("/users/new")
// Endpoint handler: validates input, calls service layer, and returns a response/view.
    public String createUserForm(Model model, @AuthenticationPrincipal CustomUserDetails principal) {
        model.addAttribute("user", new User());
        model.addAttribute("selectedRole", "AUTHORITY_STAFF");
        model.addAttribute("departments", getActiveDepartmentNames());
        model.addAttribute("showSuperAdminRole", hasRole(principal.getUser(), SUPER_ADMIN_ROLE));
        return "admin/users/form";
    }

// @PostMapping handles HTTP POST requests for the given path.
    @PostMapping("/users")
// Endpoint handler for POST /users: reads inputs, calls service, returns a view/JSON.
    public String createUser(@AuthenticationPrincipal CustomUserDetails principal,
// @ModelAttribute binds form fields to an object and adds it to the model.
                             @ModelAttribute User user,
// @RequestParam binds a query parameter or form field to a method parameter.
                             @RequestParam("role") String role,
                             RedirectAttributes redirectAttributes) {
        if (!MANAGED_ROLES.contains(role)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid role");
            return "redirect:/admin/users";
        }
        if (SUPER_ADMIN_ROLE.equals(role)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Super admin accounts cannot be created here.");
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

// @GetMapping handles HTTP GET requests for the given path.
    @GetMapping("/users/{id}/edit")
// Endpoint handler for GET /users/{id}/edit: reads inputs, calls service, returns a view/JSON.
    public String editUserForm(@PathVariable Long id,
                               @AuthenticationPrincipal CustomUserDetails principal,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        boolean isTargetSuperAdmin = hasRole(user, SUPER_ADMIN_ROLE);
        if (isTargetSuperAdmin && !hasRole(principal.getUser(), SUPER_ADMIN_ROLE)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot edit the super admin account.");
            return "redirect:/admin/users";
        }
        model.addAttribute("user", user);
        model.addAttribute("selectedRole", user.getRoles().stream().findFirst().orElse("AUTHORITY_STAFF"));
        model.addAttribute("departments", getActiveDepartmentNames());
        model.addAttribute("showSuperAdminRole", isTargetSuperAdmin || hasRole(principal.getUser(), SUPER_ADMIN_ROLE));
        return "admin/users/form";
    }

// @PostMapping handles HTTP POST requests for the given path.
    @PostMapping("/users/{id}")
// Endpoint handler for POST /users/{id}: reads inputs, calls service, returns a view/JSON.
    public String updateUser(@PathVariable Long id,
                             @AuthenticationPrincipal CustomUserDetails principal,
// @RequestParam binds a query parameter or form field to a method parameter.
                             @RequestParam String fullName,
// @RequestParam binds a query parameter or form field to a method parameter.
                             @RequestParam String email,
// @RequestParam binds a query parameter or form field to a method parameter.
                             @RequestParam String mobileNumber,
// @RequestParam binds a query parameter or form field to a method parameter.
                             @RequestParam String department,
// @RequestParam binds a query parameter or form field to a method parameter.
                             @RequestParam String role,
// @RequestParam binds a query parameter or form field to a method parameter.
                             @RequestParam(required = false) String password,
                             RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!MANAGED_ROLES.contains(role)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid role");
            return "redirect:/admin/users";
        }
        boolean isTargetSuperAdmin = hasRole(user, SUPER_ADMIN_ROLE);
        if (SUPER_ADMIN_ROLE.equals(role) && !isTargetSuperAdmin) {
            redirectAttributes.addFlashAttribute("errorMessage", "Super admin role cannot be assigned here.");
            return "redirect:/admin/users";
        }
        if (isTargetSuperAdmin && !hasRole(principal.getUser(), SUPER_ADMIN_ROLE)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot edit the super admin account.");
            return "redirect:/admin/users";
        }
        if (isTargetSuperAdmin && !SUPER_ADMIN_ROLE.equals(role)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Super admin role cannot be changed.");
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

// @PostMapping handles HTTP POST requests for the given path.
    @PostMapping("/users/{id}/delete")
// Endpoint handler for POST /users/{id}/delete: reads inputs, calls service, returns a view/JSON.
    public String deleteUser(@PathVariable Long id,
                             @AuthenticationPrincipal CustomUserDetails principal,
                             RedirectAttributes redirectAttributes) {
        if (principal != null && principal.getUser().getId().equals(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot delete your own account.");
            return "redirect:/admin/users";
        }
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (hasRole(user, SUPER_ADMIN_ROLE)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Super admin accounts cannot be deleted.");
            return "redirect:/admin/users";
        }
        userRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted.");
        return "redirect:/admin/users";
    }

// @GetMapping handles HTTP GET requests for the given path.
    @GetMapping("/reports")
// Endpoint handler: validates input, calls service layer, and returns a response/view.
    public String reports(Model model) {
        model.addAttribute("snapshot", reportingService.getFinancialSnapshot());
        model.addAttribute("unpaidRows", reportingService.getUnpaidStudentsReport());
        model.addAttribute("payments", reportingService.getReconciliationReport());
        return "admin/reports";
    }

// @GetMapping handles HTTP GET requests for the given path.
    @GetMapping("/departments")
// Endpoint handler: validates input, calls service layer, and returns a response/view.
    public String departments(Model model) {
        model.addAttribute("departments", departmentRepository.findAll().stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .toList());
        return "admin/departments";
    }

// @PostMapping handles HTTP POST requests for the given path.
    @PostMapping("/departments")
// Endpoint handler: validates input, calls service layer, and returns a response/view.
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

// @PostMapping handles HTTP POST requests for the given path.
    @PostMapping("/departments/{id}/deactivate")
// Endpoint handler: validates input, calls service layer, and returns a response/view.
    public String deactivateDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));
        department.setActive(false);
        departmentRepository.save(department);
        redirectAttributes.addFlashAttribute("successMessage", "Department deactivated.");
        return "redirect:/admin/departments";
    }

// @RequestMapping defines a common URL prefix for all endpoints in this controller.
    @RequestMapping(value = {"/departments/{id}/activate", "/departments/{id}/active"}, method = {RequestMethod.POST, RequestMethod.GET})
// Endpoint handler: validates input, calls service layer, and returns a response/view.
    public String activateDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));
        department.setActive(true);
        departmentRepository.save(department);
        redirectAttributes.addFlashAttribute("successMessage", "Department activated.");
        return "redirect:/admin/departments";
    }

// Endpoint handler: validates input, calls service layer, and returns a response/view.
    private List<User> getManagedUsers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(MANAGED_ROLES::contains))
                .toList();
    }

// Endpoint handler: validates input, calls service layer, and returns a response/view.
    private List<String> getActiveDepartmentNames() {
        return departmentRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(Department::getName)
                .toList();
    }

// Endpoint handler: validates input, calls service layer, and returns a response/view.
    private boolean hasRole(User user, String role) {
        if (user == null || role == null) {
            return false;
        }
        return user.getRoles().stream().anyMatch(r -> r.equalsIgnoreCase(role));
    }
}
