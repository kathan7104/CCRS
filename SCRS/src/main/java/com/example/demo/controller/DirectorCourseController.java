// Package declaration: groups related classes in a namespace.
package com.example.demo.controller;

// Import statement: brings a class into scope by name.
import com.example.demo.entity.Course;
// Import statement: brings a class into scope by name.
import com.example.demo.repository.CourseRepository;
// Import statement: brings a class into scope by name.
import org.springframework.stereotype.Controller;
// Import statement: brings a class into scope by name.
import org.springframework.ui.Model;
// Import statement: brings a class into scope by name.
import org.springframework.web.bind.annotation.*;
// Import statement: brings a class into scope by name.
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Import statement: brings a class into scope by name.
import java.util.HashSet;
// Import statement: brings a class into scope by name.
import java.util.List;
// Import statement: brings a class into scope by name.
import java.util.Set;

// Annotation: adds metadata used by frameworks/tools.
@Controller
// Annotation: adds metadata used by frameworks/tools.
@RequestMapping("/director/courses")
// Class declaration: defines a new type.
public class DirectorCourseController {

    // Field declaration: defines a member variable.
    private final CourseRepository courseRepository;

    // Opens a method/constructor/block.
    public DirectorCourseController(CourseRepository courseRepository) {
        // Uses current object (this) to access a field or method.
        this.courseRepository = courseRepository;
    // Closes the current code block.
    }

    // Annotation: adds metadata used by frameworks/tools.
    @GetMapping
    // Opens a method/constructor/block.
    public String list(Model model) {
        // Statement: model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("courses", courseRepository.findAll());
        // Return: sends a value back to the caller.
        return "director/courses/list";
    // Closes the current code block.
    }

    // Annotation: adds metadata used by frameworks/tools.
    @GetMapping("/new")
    // Opens a method/constructor/block.
    public String createForm(Model model) {
        // Statement: model.addAttribute("course", new Course());
        model.addAttribute("course", new Course());
        // Statement: model.addAttribute("allCourses", courseRepository.findAll());
        model.addAttribute("allCourses", courseRepository.findAll());
        // Return: sends a value back to the caller.
        return "director/courses/form";
    // Closes the current code block.
    }

    // Annotation: adds metadata used by frameworks/tools.
    @PostMapping
    // Field declaration: defines a member variable.
    public String create(@ModelAttribute Course course,
                         // Annotation: adds metadata used by frameworks/tools.
                         @RequestParam(name = "prerequisiteIds", required = false) List<Long> prerequisiteIds,
                         // Opens a new code block.
                         RedirectAttributes redirectAttributes) {
        // Statement: applyPrerequisites(course, prerequisiteIds);
        applyPrerequisites(course, prerequisiteIds);
        // Statement: normalizeCapacity(course);
        normalizeCapacity(course);
        // Statement: courseRepository.save(course);
        courseRepository.save(course);
        // Statement: redirectAttributes.addFlashAttribute("successMessage", "Course created succes...
        redirectAttributes.addFlashAttribute("successMessage", "Course created successfully.");
        // Return: sends a value back to the caller.
        return "redirect:/director/courses";
    // Closes the current code block.
    }

    // Annotation: adds metadata used by frameworks/tools.
    @GetMapping("/{id}/edit")
    // Opens a method/constructor/block.
    public String editForm(@PathVariable Long id, Model model) {
        // Statement: Course course = courseRepository.findById(id)
        Course course = courseRepository.findById(id)
                // Statement: .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
        // Statement: List<Course> allCourses = courseRepository.findAll();
        List<Course> allCourses = courseRepository.findAll();
        // Statement: allCourses.removeIf(c -> c.getId().equals(id));
        allCourses.removeIf(c -> c.getId().equals(id));
        // Statement: model.addAttribute("course", course);
        model.addAttribute("course", course);
        // Statement: model.addAttribute("allCourses", allCourses);
        model.addAttribute("allCourses", allCourses);
        // Return: sends a value back to the caller.
        return "director/courses/form";
    // Closes the current code block.
    }

    // Annotation: adds metadata used by frameworks/tools.
    @PostMapping("/{id}")
    // Field declaration: defines a member variable.
    public String update(@PathVariable Long id,
                         // Annotation: adds metadata used by frameworks/tools.
                         @ModelAttribute Course form,
                         // Annotation: adds metadata used by frameworks/tools.
                         @RequestParam(name = "prerequisiteIds", required = false) List<Long> prerequisiteIds,
                         // Opens a new code block.
                         RedirectAttributes redirectAttributes) {
        // Statement: Course course = courseRepository.findById(id)
        Course course = courseRepository.findById(id)
                // Statement: .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));

        // Statement: course.setCode(form.getCode());
        course.setCode(form.getCode());
        // Statement: course.setName(form.getName());
        course.setName(form.getName());
        // Statement: course.setDepartment(form.getDepartment());
        course.setDepartment(form.getDepartment());
        // Statement: course.setCapacity(form.getCapacity());
        course.setCapacity(form.getCapacity());
        // Statement: course.setRemainingSeats(form.getRemainingSeats());
        course.setRemainingSeats(form.getRemainingSeats());
        // Statement: course.setCredits(form.getCredits());
        course.setCredits(form.getCredits());
        // Statement: course.setFee(form.getFee());
        course.setFee(form.getFee());
        // Statement: course.setProgramLevel(form.getProgramLevel());
        course.setProgramLevel(form.getProgramLevel());
        // Statement: course.setLevel(form.getLevel());
        course.setLevel(form.getLevel());
        // Statement: course.setDurationYears(form.getDurationYears());
        course.setDurationYears(form.getDurationYears());
        // Statement: course.setRequiredQualification(form.getRequiredQualification());
        course.setRequiredQualification(form.getRequiredQualification());

        // Statement: applyPrerequisites(course, prerequisiteIds);
        applyPrerequisites(course, prerequisiteIds);
        // Statement: normalizeCapacity(course);
        normalizeCapacity(course);
        // Statement: courseRepository.save(course);
        courseRepository.save(course);

        // Statement: redirectAttributes.addFlashAttribute("successMessage", "Course updated succes...
        redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully.");
        // Return: sends a value back to the caller.
        return "redirect:/director/courses";
    // Closes the current code block.
    }

    // Annotation: adds metadata used by frameworks/tools.
    @PostMapping("/{id}/delete")
    // Opens a method/constructor/block.
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // Statement: courseRepository.deleteById(id);
        courseRepository.deleteById(id);
        // Statement: redirectAttributes.addFlashAttribute("successMessage", "Course deleted succes...
        redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully.");
        // Return: sends a value back to the caller.
        return "redirect:/director/courses";
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    private void applyPrerequisites(Course course, List<Long> prerequisiteIds) {
        // Opens a method/constructor/block.
        if (prerequisiteIds == null || prerequisiteIds.isEmpty()) {
            // Statement: course.setPrerequisites(new HashSet<>());
            course.setPrerequisites(new HashSet<>());
            // Return: exits the method without a value.
            return;
        // Closes the current code block.
        }
        // Statement: Set<Course> prereqs = new HashSet<>(courseRepository.findAllById(prerequisite...
        Set<Course> prereqs = new HashSet<>(courseRepository.findAllById(prerequisiteIds));
        // Statement: course.setPrerequisites(prereqs);
        course.setPrerequisites(prereqs);
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    private void normalizeCapacity(Course course) {
        // Opens a method/constructor/block.
        if (course.getCapacity() == null || course.getCapacity() < 0) {
            // Statement: course.setCapacity(0);
            course.setCapacity(0);
        // Closes the current code block.
        }
        // Opens a method/constructor/block.
        if (course.getRemainingSeats() == null) {
            // Statement: course.setRemainingSeats(course.getCapacity());
            course.setRemainingSeats(course.getCapacity());
        // Closes the current code block.
        }
        // Opens a method/constructor/block.
        if (course.getRemainingSeats() < 0) {
            // Statement: course.setRemainingSeats(0);
            course.setRemainingSeats(0);
        // Closes the current code block.
        }
        // Opens a method/constructor/block.
        if (course.getRemainingSeats() > course.getCapacity()) {
            // Statement: course.setRemainingSeats(course.getCapacity());
            course.setRemainingSeats(course.getCapacity());
        // Closes the current code block.
        }
    // Closes the current code block.
    }
// Closes the current code block.
}
