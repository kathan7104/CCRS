package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.entity.FacultyCourseAssignment;
import com.example.demo.entity.User;
import com.example.demo.repository.CourseRepository;
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
import java.util.Set;

@Controller
@RequestMapping("/director")
public class DirectorManagementController {
    private static final Set<String> MANAGED_ROLES = Set.of("STUDENT", "AUTHORITY_FACULTY");

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final FacultyCourseAssignmentRepository facultyCourseAssignmentRepository;
    private final PasswordEncoder passwordEncoder;

    public DirectorManagementController(UserRepository userRepository,
                                        CourseRepository courseRepository,
                                        FacultyCourseAssignmentRepository facultyCourseAssignmentRepository,
                                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
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
        model.addAttribute("facultyCount", userRepository.findByDepartmentAndRole(department, "AUTHORITY_FACULTY").size());
        model.addAttribute("assignmentCount", facultyCourseAssignmentRepository.findByFacultyDepartmentIgnoreCase(department).size());
        return "director/dashboard";
    }

    @GetMapping("/users")
    public String users(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        String department = resolveDepartment(principal);
        List<User> users = userRepository.findByDepartmentIgnoreCase(department).stream()
                .filter(u -> u.getRoles().stream().anyMatch(MANAGED_ROLES::contains))
                .toList();
        model.addAttribute("users", users);
        model.addAttribute("department", department);
        return "director/users/list";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("selectedRole", "STUDENT");
        return "director/users/form";
    }

    @PostMapping("/users")
    public String createUser(@AuthenticationPrincipal CustomUserDetails principal,
                             @ModelAttribute User user,
                             @RequestParam("role") String role,
                             RedirectAttributes redirectAttributes) {
        if (!MANAGED_ROLES.contains(role)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid role");
            return "redirect:/director/users";
        }
        user.setDepartment(resolveDepartment(principal));
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
        if (!resolveDepartment(principal).equalsIgnoreCase(nullSafe(user.getDepartment()))) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot edit users outside your department.");
            return "redirect:/director/users";
        }
        model.addAttribute("user", user);
        model.addAttribute("selectedRole", user.getRoles().stream().findFirst().orElse("STUDENT"));
        return "director/users/form";
    }

    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable Long id,
                             @AuthenticationPrincipal CustomUserDetails principal,
                             @RequestParam String fullName,
                             @RequestParam String email,
                             @RequestParam String mobileNumber,
                             @RequestParam String role,
                             @RequestParam(required = false) String password,
                             RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!MANAGED_ROLES.contains(role)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid role");
            return "redirect:/director/users";
        }
        if (!resolveDepartment(principal).equalsIgnoreCase(nullSafe(user.getDepartment()))) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot edit users outside your department.");
            return "redirect:/director/users";
        }
        user.setFullName(fullName);
        user.setEmail(email);
        user.setMobileNumber(mobileNumber);
        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }
        user.getRoles().clear();
        user.getRoles().add(role);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "Department user updated.");
        return "redirect:/director/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id,
                             @AuthenticationPrincipal CustomUserDetails principal,
                             RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!resolveDepartment(principal).equalsIgnoreCase(nullSafe(user.getDepartment()))) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete users outside your department.");
            return "redirect:/director/users";
        }
        userRepository.delete(user);
        redirectAttributes.addFlashAttribute("successMessage", "Department user deleted.");
        return "redirect:/director/users";
    }

    @GetMapping("/assignments")
    public String assignments(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        String department = resolveDepartment(principal);
        List<User> faculty = userRepository.findByDepartmentAndRole(department, "AUTHORITY_FACULTY");
        List<Course> courses = courseRepository.findAll().stream()
                .filter(c -> department.equalsIgnoreCase(c.getDepartment()))
                .toList();
        model.addAttribute("faculty", faculty);
        model.addAttribute("courses", courses);
        model.addAttribute("assignments", facultyCourseAssignmentRepository.findByFacultyDepartmentIgnoreCase(department));
        model.addAttribute("department", department);
        return "director/assignments/list";
    }

    @PostMapping("/assignments")
    public String assignCourse(@AuthenticationPrincipal CustomUserDetails principal,
                               @RequestParam Long facultyId,
                               @RequestParam Long courseId,
                               RedirectAttributes redirectAttributes) {
        String department = resolveDepartment(principal);
        User faculty = userRepository.findById(facultyId).orElseThrow(() -> new IllegalArgumentException("Faculty not found"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));
        if (!department.equalsIgnoreCase(nullSafe(faculty.getDepartment())) || !department.equalsIgnoreCase(nullSafe(course.getDepartment()))) {
            redirectAttributes.addFlashAttribute("errorMessage", "Faculty and course must belong to your department.");
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
        if (!resolveDepartment(principal).equalsIgnoreCase(nullSafe(assignment.getFaculty().getDepartment()))) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot remove assignment outside your department.");
            return "redirect:/director/assignments";
        }
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
}
