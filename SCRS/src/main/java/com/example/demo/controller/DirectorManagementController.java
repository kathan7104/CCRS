package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.entity.FacultyCourseAssignment;
import com.example.demo.entity.User;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.FacultyCourseAssignmentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;

@Controller
@RequestMapping("/director")
public class DirectorManagementController {
    private static final Set<String> MANAGED_ROLES = Set.of("STUDENT", "AUTHORITY_FACULTY");
    private static final String FACULTY_ROLE = "AUTHORITY_FACULTY";

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final FacultyCourseAssignmentRepository facultyCourseAssignmentRepository;
    private final PasswordEncoder passwordEncoder;

    public DirectorManagementController(UserRepository userRepository,
                                        CourseRepository courseRepository,
                                        DepartmentRepository departmentRepository,
                                        FacultyCourseAssignmentRepository facultyCourseAssignmentRepository,
                                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
        this.facultyCourseAssignmentRepository = facultyCourseAssignmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        String department = resolveDepartment(principal);
        model.addAttribute("department", department);
        model.addAttribute("userName", principal.getUser().getFullName());
        model.addAttribute("courseCount", courseRepository.findAll().stream()
                .filter(c -> department.equalsIgnoreCase(c.getDepartment()))
                .count());
        model.addAttribute("studentCount", userRepository.findByDepartmentAndRole(department, "STUDENT").size());
        model.addAttribute("facultyCount", userRepository.findByRole(FACULTY_ROLE).size());
        model.addAttribute("assignmentCount", facultyCourseAssignmentRepository.count());
        return "director/dashboard";
    }

    @GetMapping("/users")
    public String users(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        String department = resolveDepartment(principal);
        List<User> faculty = userRepository.findByRole(FACULTY_ROLE);
        List<User> students = userRepository.findByDepartmentAndRole(department, "STUDENT");
        Map<Long, User> merged = new LinkedHashMap<>();
        faculty.forEach(u -> merged.put(u.getId(), u));
        students.forEach(u -> merged.put(u.getId(), u));
        List<User> users = merged.values().stream().toList();
        model.addAttribute("users", users);
        model.addAttribute("department", department);
        return "director/users/list";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("selectedRole", FACULTY_ROLE);
        model.addAttribute("departments", getActiveDepartmentNames());
        return "director/users/form";
    }

    @PostMapping("/users")
    public String createUser(@AuthenticationPrincipal CustomUserDetails principal,
                             @ModelAttribute User user,
                             @RequestParam("role") String role,
                             @RequestParam("department") String department,
                             RedirectAttributes redirectAttributes) {
        if (!FACULTY_ROLE.equals(role)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Director can add only faculty users.");
            return "redirect:/director/users";
        }
        user.setDepartment(nullSafe(department).isBlank() ? resolveDepartment(principal) : department);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().clear();
        user.getRoles().add(role);
        user.setEmailVerified(true);
        user.setMobileVerified(true);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "Department user created.");
        return "redirect:/director/users";
    }

    @GetMapping("/users/{id}/edit")
    public String editUser(@PathVariable Long id,
                           @AuthenticationPrincipal CustomUserDetails principal,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        String directorDepartment = resolveDepartment(principal);
        if (!canManageUser(directorDepartment, user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot edit this user.");
            return "redirect:/director/users";
        }
        if (!isManagedUser(user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot edit this user role.");
            return "redirect:/director/users";
        }
        model.addAttribute("user", user);
        model.addAttribute("selectedRole", user.getRoles().stream().findFirst().orElse("STUDENT"));
        model.addAttribute("departments", getActiveDepartmentNames());
        return "director/users/form";
    }

    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable Long id,
                             @AuthenticationPrincipal CustomUserDetails principal,
                             @RequestParam String fullName,
                             @RequestParam String email,
                             @RequestParam String mobileNumber,
                             @RequestParam String role,
                             @RequestParam(required = false) String department,
                             @RequestParam(required = false) String password,
                             RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!MANAGED_ROLES.contains(role)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid role");
            return "redirect:/director/users";
        }
        if (!isManagedUser(user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot edit this user role.");
            return "redirect:/director/users";
        }
        String directorDepartment = resolveDepartment(principal);
        if (!canManageUser(directorDepartment, user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot edit this user.");
            return "redirect:/director/users";
        }
        String existingRole = user.getRoles().stream().findFirst().orElse("");
        if (!existingRole.isBlank() && !existingRole.equals(role)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Role change is not allowed here.");
            return "redirect:/director/users";
        }
        user.setFullName(fullName);
        user.setEmail(email);
        user.setMobileNumber(mobileNumber);
        if (FACULTY_ROLE.equals(role) && department != null && !department.isBlank()) {
            user.setDepartment(department);
        }
        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }
        user.getRoles().clear();
        user.getRoles().add(role);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "Department user updated.");
        return "redirect:/director/users";
    }

    @PostMapping("/users/{id}/deactivate")
    public String deactivateUser(@PathVariable Long id,
                                 @AuthenticationPrincipal CustomUserDetails principal,
                                 RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!canManageUser(resolveDepartment(principal), user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot deactivate this user.");
            return "redirect:/director/users";
        }
        if (!isManagedUser(user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot deactivate this user role.");
            return "redirect:/director/users";
        }
        user.setActive(false);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "User deactivated.");
        return "redirect:/director/users";
    }

    @GetMapping("/assignments")
    public String assignments(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        List<User> faculty = userRepository.findByRole(FACULTY_ROLE).stream()
                .filter(User::isActive)
                .toList();
        List<Course> courses = courseRepository.findAll();
        model.addAttribute("faculty", faculty);
        model.addAttribute("courses", courses);
        model.addAttribute("assignments", facultyCourseAssignmentRepository.findAll());
        model.addAttribute("department", "All Departments");
        return "director/assignments/list";
    }

    @PostMapping("/assignments")
    public String assignCourse(@AuthenticationPrincipal CustomUserDetails principal,
                               @RequestParam Long facultyId,
                               @RequestParam Long courseId,
                               RedirectAttributes redirectAttributes) {
        User faculty = userRepository.findById(facultyId).orElseThrow(() -> new IllegalArgumentException("Faculty not found"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));
        if (!faculty.getRoles().contains(FACULTY_ROLE)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Selected user is not faculty.");
            return "redirect:/director/assignments";
        }
        if (facultyCourseAssignmentRepository.existsByFacultyIdAndCourseId(facultyId, courseId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Assignment already exists.");
            return "redirect:/director/assignments";
        }
        FacultyCourseAssignment assignment = new FacultyCourseAssignment();
        assignment.setFaculty(faculty);
        assignment.setCourse(course);
        facultyCourseAssignmentRepository.save(assignment);
        redirectAttributes.addFlashAttribute("successMessage", "Faculty assigned to course.");
        return "redirect:/director/assignments";
    }

    @PostMapping("/assignments/{id}/delete")
    public String removeAssignment(@PathVariable Long id,
                                   @AuthenticationPrincipal CustomUserDetails principal,
                                   RedirectAttributes redirectAttributes) {
        FacultyCourseAssignment assignment = facultyCourseAssignmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
        facultyCourseAssignmentRepository.delete(assignment);
        redirectAttributes.addFlashAttribute("successMessage", "Assignment removed.");
        return "redirect:/director/assignments";
    }

    private String resolveDepartment(CustomUserDetails principal) {
        String department = principal.getUser().getDepartment();
        return department == null || department.isBlank() ? "Engineering" : department;
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    private boolean isManagedUser(User user) {
        return user.getRoles().stream().anyMatch(MANAGED_ROLES::contains);
    }

    private boolean canManageUser(String directorDepartment, User user) {
        if (user.getRoles().contains(FACULTY_ROLE)) {
            return true;
        }
        return directorDepartment.equalsIgnoreCase(nullSafe(user.getDepartment()));
    }

    private List<String> getActiveDepartmentNames() {
        return departmentRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(d -> d.getName())
                .toList();
    }
}
