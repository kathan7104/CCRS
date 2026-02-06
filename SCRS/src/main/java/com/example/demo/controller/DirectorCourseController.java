package com.example.demo.controller;
import com.example.demo.entity.Course;
import com.example.demo.repository.CourseRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Web MVC controller for Director Course endpoints.
 */
@Controller
@RequestMapping("/director/courses")
public class DirectorCourseController {
    private final CourseRepository courseRepository;
    public DirectorCourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }
/**
 * Web MVC controller for Director Course endpoints.
 */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        return "director/courses/list";
    }
/**
 * Web MVC controller for Director Course endpoints.
 */
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("allCourses", courseRepository.findAll());
        return "director/courses/form";
    }
/**
 * Web MVC controller for Director Course endpoints.
 */
    @PostMapping
    public String create(@ModelAttribute Course course,
                         @RequestParam(name = "prerequisiteIds", required = false) List<Long> prerequisiteIds,
                         RedirectAttributes redirectAttributes) {
        applyPrerequisites(course, prerequisiteIds);
        normalizeCapacity(course);
        courseRepository.save(course);
        redirectAttributes.addFlashAttribute("successMessage", "Course created successfully.");
        return "redirect:/director/courses";
    }
/**
 * Web MVC controller for Director Course endpoints.
 */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
        List<Course> allCourses = courseRepository.findAll();
        allCourses.removeIf(c -> c.getId().equals(id));
        model.addAttribute("course", course);
        model.addAttribute("allCourses", allCourses);
        return "director/courses/form";
    }
/**
 * Web MVC controller for Director Course endpoints.
 */
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute Course form,
                         @RequestParam(name = "prerequisiteIds", required = false) List<Long> prerequisiteIds,
                         RedirectAttributes redirectAttributes) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
        course.setCode(form.getCode());
        course.setName(form.getName());
        course.setDepartment(form.getDepartment());
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
        courseRepository.save(course);
        redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully.");
        return "redirect:/director/courses";
    }
/**
 * Web MVC controller for Director Course endpoints.
 */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        courseRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully.");
        return "redirect:/director/courses";
    }
    private void applyPrerequisites(Course course, List<Long> prerequisiteIds) {
        if (prerequisiteIds == null || prerequisiteIds.isEmpty()) {
            course.setPrerequisites(new HashSet<>());
            return;
        }
        Set<Course> prereqs = new HashSet<>(courseRepository.findAllById(prerequisiteIds));
        course.setPrerequisites(prereqs);
    }
    private void normalizeCapacity(Course course) {
        if (course.getCapacity() == null || course.getCapacity() < 0) {
            course.setCapacity(0);
        }
        if (course.getRemainingSeats() == null) {
            course.setRemainingSeats(course.getCapacity());
        }
        if (course.getRemainingSeats() < 0) {
            course.setRemainingSeats(0);
        }
        if (course.getRemainingSeats() > course.getCapacity()) {
            course.setRemainingSeats(course.getCapacity());
        }
    }
}
