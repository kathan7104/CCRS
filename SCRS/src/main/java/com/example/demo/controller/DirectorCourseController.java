package com.example.demo.controller;
import com.example.demo.entity.Course;
import com.example.demo.repository.CourseRepository;
import com.example.demo.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Controller
@RequestMapping("/director/courses")
public class DirectorCourseController {
    private final CourseRepository courseRepository;
    public DirectorCourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }
    @GetMapping
    public String list(Model model, @AuthenticationPrincipal CustomUserDetails principal) {
        String department = resolveDepartment(principal);
        // 1. Put data on the page so the user can see it
        model.addAttribute("courses", courseRepository.findAll().stream()
                .filter(c -> department.equalsIgnoreCase(c.getDepartment()))
                .toList());
        // 2. Send the result back to the screen
        return "director/courses/list";
    }
    @GetMapping("/new")
    public String createForm(Model model, @AuthenticationPrincipal CustomUserDetails principal) {
        String department = resolveDepartment(principal);
        Course course = new Course();
        course.setDepartment(department);
        // 1. Put data on the page so the user can see it
        model.addAttribute("course", course);
        // 2. Put data on the page so the user can see it
        model.addAttribute("allCourses", courseRepository.findAll().stream()
                .filter(c -> department.equalsIgnoreCase(c.getDepartment()))
                .toList());
        // 3. Send the result back to the screen
        return "director/courses/form";
    }
    @PostMapping
    public String create(@ModelAttribute Course course,
                         @RequestParam(name = "prerequisiteIds", required = false) List<Long> prerequisiteIds,
                         @AuthenticationPrincipal CustomUserDetails principal,
                         RedirectAttributes redirectAttributes) {
        course.setDepartment(resolveDepartment(principal));
        applyPrerequisites(course, prerequisiteIds);
        normalizeCapacity(course);
        // 1. Get or save data in the database
        courseRepository.save(course);
        // 2. Show a one-time message on the next page
        redirectAttributes.addFlashAttribute("successMessage", "Course created successfully.");
        // 3. Send the result back to the screen
        return "redirect:/director/courses";
    }
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, @AuthenticationPrincipal CustomUserDetails principal) {
        // 1. Get or save data in the database
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
        String department = resolveDepartment(principal);
        if (!department.equalsIgnoreCase(course.getDepartment())) {
            throw new IllegalArgumentException("Access denied for this course.");
        }
        // 2. Get or save data in the database
        List<Course> allCourses = courseRepository.findAll();
        allCourses.removeIf(c -> !department.equalsIgnoreCase(c.getDepartment()));
        allCourses.removeIf(c -> c.getId().equals(id));
        // 3. Put data on the page so the user can see it
        model.addAttribute("course", course);
        // 4. Put data on the page so the user can see it
        model.addAttribute("allCourses", allCourses);
        // 5. Send the result back to the screen
        return "director/courses/form";
    }
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute Course form,
                         @RequestParam(name = "prerequisiteIds", required = false) List<Long> prerequisiteIds,
                         @AuthenticationPrincipal CustomUserDetails principal,
                         RedirectAttributes redirectAttributes) {
        // 1. Get or save data in the database
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
        String department = resolveDepartment(principal);
        if (!department.equalsIgnoreCase(course.getDepartment())) {
            throw new IllegalArgumentException("Access denied for this course.");
        }
        course.setCode(form.getCode());
        course.setName(form.getName());
        course.setDepartment(department);
        course.setCapacity(form.getCapacity());
        course.setRemainingSeats(form.getRemainingSeats());
        course.setCredits(form.getCredits());
        course.setFee(form.getFee());
        course.setProgramLevel(form.getProgramLevel());
        course.setLevel(form.getLevel());
        course.setDurationYears(form.getDurationYears());
        course.setRequiredQualification(form.getRequiredQualification());
        applyPrerequisites(course, prerequisiteIds);
        normalizeCapacity(course);
        // 2. Get or save data in the database
        courseRepository.save(course);
        // 3. Show a one-time message on the next page
        redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully.");
        // 4. Send the result back to the screen
        return "redirect:/director/courses";
    }
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal CustomUserDetails principal,
                         RedirectAttributes redirectAttributes) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
        if (!resolveDepartment(principal).equalsIgnoreCase(course.getDepartment())) {
            throw new IllegalArgumentException("Access denied for this course.");
        }
        // 1. Get or save data in the database
        courseRepository.delete(course);
        // 2. Show a one-time message on the next page
        redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully.");
        // 3. Send the result back to the screen
        return "redirect:/director/courses";
    }
    private void applyPrerequisites(Course course, List<Long> prerequisiteIds) {
        // 1. Check a rule -> decide what to do next
        if (prerequisiteIds == null || prerequisiteIds.isEmpty()) {
            course.setPrerequisites(new HashSet<>());
            // 2. Send the result back to the screen
            return;
        }
        // 3. Get or save data in the database
        Set<Course> prereqs = new HashSet<>(courseRepository.findAllById(prerequisiteIds));
        course.setPrerequisites(prereqs);
    }
    private void normalizeCapacity(Course course) {
        // 1. Check a rule -> decide what to do next
        if (course.getCapacity() == null || course.getCapacity() < 0) {
            course.setCapacity(0);
        }
        // 2. Check a rule -> decide what to do next
        if (course.getRemainingSeats() == null) {
            course.setRemainingSeats(course.getCapacity());
        }
        // 3. Check a rule -> decide what to do next
        if (course.getRemainingSeats() < 0) {
            course.setRemainingSeats(0);
        }
        // 4. Check a rule -> decide what to do next
        if (course.getRemainingSeats() > course.getCapacity()) {
            course.setRemainingSeats(course.getCapacity());
        }
    }
    private String resolveDepartment(CustomUserDetails principal) {
        String department = principal.getUser().getDepartment();
        return department == null || department.isBlank() ? "Engineering" : department;
    }
}
