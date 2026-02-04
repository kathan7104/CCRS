// Package declaration: groups related classes in a namespace.
package com.example.demo.controller;

// Import statement: brings a class into scope by name.
import com.example.demo.entity.Course;
// Import statement: brings a class into scope by name.
import com.example.demo.repository.CourseRepository;
// Import statement: brings a class into scope by name.
import com.example.demo.security.CustomUserDetails;
// Import statement: brings a class into scope by name.
import com.example.demo.service.EnrollmentService;
// Import statement: brings a class into scope by name.
import org.springframework.security.core.annotation.AuthenticationPrincipal;
// Import statement: brings a class into scope by name.
import org.springframework.stereotype.Controller;
// Import statement: brings a class into scope by name.
import org.springframework.ui.Model;
// Import statement: brings a class into scope by name.
import org.springframework.web.bind.annotation.*;
// Import statement: brings a class into scope by name.
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Import statement: brings a class into scope by name.
import java.io.IOException;
// Import statement: brings a class into scope by name.
import java.nio.file.Files;
// Import statement: brings a class into scope by name.
import java.nio.file.Path;
// Import statement: brings a class into scope by name.
import java.nio.file.Paths;
// Import statement: brings a class into scope by name.
import java.nio.file.StandardCopyOption;
// Import statement: brings a class into scope by name.
import java.time.LocalDate;
// Import statement: brings a class into scope by name.
import java.util.List;
// Import statement: brings a class into scope by name.
import java.util.UUID;

// Import statement: brings a class into scope by name.
import org.springframework.web.multipart.MultipartFile;

// Annotation: adds metadata used by frameworks/tools.
@Controller
// Annotation: adds metadata used by frameworks/tools.
@RequestMapping("/courses")
// Class declaration: defines a new type.
public class CourseController {

    // Field declaration: defines a member variable.
    private final CourseRepository courseRepository;
    // Field declaration: defines a member variable.
    private final EnrollmentService enrollmentService;
    // Field declaration: defines a member variable.
    private static final String UPLOAD_DIR = "uploads/marksheets/";

    // Opens a method/constructor/block.
    public CourseController(CourseRepository courseRepository, EnrollmentService enrollmentService) {
        // Uses current object (this) to access a field or method.
        this.courseRepository = courseRepository;
        // Uses current object (this) to access a field or method.
        this.enrollmentService = enrollmentService;
    // Closes the current code block.
    }

    // Annotation: adds metadata used by frameworks/tools.
    @GetMapping
    // Opens a method/constructor/block.
    public String listCourses(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        // Statement: List<Course> courses = courseRepository.findAll();
        List<Course> courses = courseRepository.findAll();
        // Statement: model.addAttribute("courses", courses);
        model.addAttribute("courses", courses);
        // Statement: boolean isAuthority = false;
        boolean isAuthority = false;
        // Statement: boolean isStudent = false;
        boolean isStudent = false;
        // Opens a method/constructor/block.
        if (userDetails != null) {
            // Statement: model.addAttribute("userName", userDetails.getUser().getFullName());
            model.addAttribute("userName", userDetails.getUser().getFullName());
        // Closes the current code block.
        }
        // Opens a method/constructor/block.
        if (userDetails != null) {
            // Statement: isAuthority = userDetails.getAuthorities().stream()
            isAuthority = userDetails.getAuthorities().stream()
                    // Statement: .anyMatch(a -> a.getAuthority().startsWith("ROLE_AUTHORITY"));
                    .anyMatch(a -> a.getAuthority().startsWith("ROLE_AUTHORITY"));
            // Statement: isStudent = userDetails.getAuthorities().stream()
            isStudent = userDetails.getAuthorities().stream()
                    // Statement: .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
                    .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
        // Closes the current code block.
        }
        // Statement: model.addAttribute("isAuthority", isAuthority);
        model.addAttribute("isAuthority", isAuthority);
        // Statement: model.addAttribute("isStudent", isStudent);
        model.addAttribute("isStudent", isStudent);
        // Return: sends a value back to the caller.
        return "courses/list";
    // Closes the current code block.
    }

    // Annotation: adds metadata used by frameworks/tools.
    @GetMapping("/{id}")
    // Opens a method/constructor/block.
    public String courseDetails(@PathVariable Long id, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        // Statement: Course course = courseRepository.findById(id)
        Course course = courseRepository.findById(id)
                // Statement: .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
        // Statement: model.addAttribute("course", course);
        model.addAttribute("course", course);
        // Statement: boolean isStudent = userDetails != null && userDetails.getAuthorities().stream()
        boolean isStudent = userDetails != null && userDetails.getAuthorities().stream()
                // Statement: .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
        // Statement: model.addAttribute("isStudent", isStudent);
        model.addAttribute("isStudent", isStudent);
        // Return: sends a value back to the caller.
        return "courses/detail";
    // Closes the current code block.
    }

    // Annotation: adds metadata used by frameworks/tools.
    @GetMapping("/{id}/enroll")
    // Opens a method/constructor/block.
    public String showEnrollmentForm(@PathVariable Long id, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        // Statement: boolean isStudent = userDetails != null && userDetails.getAuthorities().stream()
        boolean isStudent = userDetails != null && userDetails.getAuthorities().stream()
                // Statement: .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
        // Opens a method/constructor/block.
        if (!isStudent) {
            // Return: sends a value back to the caller.
            return "redirect:/courses";
        // Closes the current code block.
        }
        // Statement: Course course = courseRepository.findById(id)
        Course course = courseRepository.findById(id)
                // Statement: .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
        
        // Statement: model.addAttribute("course", course);
        model.addAttribute("course", course);
        // Statement: model.addAttribute("studentName", userDetails.getUser().getFullName());
        model.addAttribute("studentName", userDetails.getUser().getFullName());
        // Statement: model.addAttribute("studentEmail", userDetails.getUsername());
        model.addAttribute("studentEmail", userDetails.getUsername());
        
        // Return: sends a value back to the caller.
        return "courses/enroll";
    // Closes the current code block.
    }

    // Annotation: adds metadata used by frameworks/tools.
    @PostMapping("/{id}/enroll")
    // Field declaration: defines a member variable.
    public String processEnrollment(@PathVariable Long id, 
                                   // Annotation: adds metadata used by frameworks/tools.
                                   @RequestParam("fullName") String fullName,
                                   // Annotation: adds metadata used by frameworks/tools.
                                   @RequestParam("dob") String dobStr,
                                   // Annotation: adds metadata used by frameworks/tools.
                                   @RequestParam("pastMarks") Double pastMarks,
                                   // Annotation: adds metadata used by frameworks/tools.
                                   @RequestParam("highestQualification") String highestQualification,
                                   // Annotation: adds metadata used by frameworks/tools.
                                   @RequestParam("boardUniversity") String boardUniversity,
                                   // Annotation: adds metadata used by frameworks/tools.
                                   @RequestParam("passingYear") Integer passingYear,
                                   // Annotation: adds metadata used by frameworks/tools.
                                   @RequestParam("marksheetFile") MultipartFile marksheetFile,
                                   // Annotation: adds metadata used by frameworks/tools.
                                   @RequestParam(required = false) String comments,
                                   // Annotation: adds metadata used by frameworks/tools.
                                   @AuthenticationPrincipal CustomUserDetails userDetails,
                                   // Opens a new code block.
                                   RedirectAttributes redirectAttributes) {
        // Statement: boolean isStudent = userDetails != null && userDetails.getAuthorities().stream()
        boolean isStudent = userDetails != null && userDetails.getAuthorities().stream()
                // Statement: .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
        // Opens a method/constructor/block.
        if (!isStudent) {
            // Statement: redirectAttributes.addFlashAttribute("errorMessage", "Only students can apply...
            redirectAttributes.addFlashAttribute("errorMessage", "Only students can apply for courses.");
            // Return: sends a value back to the caller.
            return "redirect:/courses";
        // Closes the current code block.
        }
        // Opens a new code block.
        try {
            // Comment: explains code for readers.
            // Save file
            // Statement: String fileName = UUID.randomUUID().toString() + "_" + marksheetFile.getOrigi...
            String fileName = UUID.randomUUID().toString() + "_" + marksheetFile.getOriginalFilename();
            // Statement: Path uploadPath = Paths.get(UPLOAD_DIR);
            Path uploadPath = Paths.get(UPLOAD_DIR);
            // Opens a method/constructor/block.
            if (!Files.exists(uploadPath)) {
                // Statement: Files.createDirectories(uploadPath);
                Files.createDirectories(uploadPath);
            // Closes the current code block.
            }
            // Statement: Path filePath = uploadPath.resolve(fileName);
            Path filePath = uploadPath.resolve(fileName);
            // Statement: Files.copy(marksheetFile.getInputStream(), filePath, StandardCopyOption.REPLA...
            Files.copy(marksheetFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Statement: enrollmentService.enrollStudent(userDetails.getUsername(), id, comments,
            enrollmentService.enrollStudent(userDetails.getUsername(), id, comments, 
                // Statement: fullName, LocalDate.parse(dobStr), pastMarks, highestQualification, boardUniv...
                fullName, LocalDate.parse(dobStr), pastMarks, highestQualification, boardUniversity, passingYear, filePath.toString());
            
            // Statement: redirectAttributes.addFlashAttribute("successMessage", "Successfully enrolled...
            redirectAttributes.addFlashAttribute("successMessage", "Successfully enrolled in " + id + ". Application under review.");
            // Return: sends a value back to the caller.
            return "redirect:/courses";
        // Opens a method/constructor/block.
        } catch (IOException e) {
             // Statement: redirectAttributes.addFlashAttribute("errorMessage", "File upload failed: " +...
             redirectAttributes.addFlashAttribute("errorMessage", "File upload failed: " + e.getMessage());
             // Return: sends a value back to the caller.
             return "redirect:/courses/" + id + "/enroll";
        // Opens a method/constructor/block.
        } catch (Exception e) {
            // Statement: redirectAttributes.addFlashAttribute("errorMessage", "Enrollment failed: " + ...
            redirectAttributes.addFlashAttribute("errorMessage", "Enrollment failed: " + e.getMessage());
            // Return: sends a value back to the caller.
            return "redirect:/courses/" + id + "/enroll";
        // Closes the current code block.
        }
    // Closes the current code block.
    }
// Closes the current code block.
}
