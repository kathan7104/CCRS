package com.example.demo.controller;
import com.example.demo.entity.Course;
import com.example.demo.repository.CourseRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.EnrollmentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
@Controller
@RequestMapping("/courses")
public class CourseController {
    private final CourseRepository courseRepository;
    private final EnrollmentService enrollmentService;
    private static final String UPLOAD_DIR = "uploads/marksheets/";
    public CourseController(CourseRepository courseRepository, EnrollmentService enrollmentService) {
        this.courseRepository = courseRepository;
        this.enrollmentService = enrollmentService;
    }
    @GetMapping
    public String listCourses(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 1. Get or save data in the database
        List<Course> courses = courseRepository.findAll();
        // 2. Put data on the page so the user can see it
        model.addAttribute("courses", courses);
        boolean isAuthority = false;
        boolean isStudent = false;
        // 3. Check a rule -> decide what to do next
        if (userDetails != null) {
            // 4. Put data on the page so the user can see it
            model.addAttribute("userName", userDetails.getUser().getFullName());
        }
        // 5. Check a rule -> decide what to do next
        if (userDetails != null) {
            isAuthority = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().startsWith("ROLE_AUTHORITY"));
            isStudent = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
        }
        // 6. Put data on the page so the user can see it
        model.addAttribute("isAuthority", isAuthority);
        // 7. Put data on the page so the user can see it
        model.addAttribute("isStudent", isStudent);
        // 8. Send the result back to the screen
        return "courses/list";
    }
    @GetMapping("/{id}")
    public String courseDetails(@PathVariable Long id, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 1. Get or save data in the database
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
        // 2. Put data on the page so the user can see it
        model.addAttribute("course", course);
        boolean isStudent = userDetails != null && userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
        // 3. Put data on the page so the user can see it
        model.addAttribute("isStudent", isStudent);
        // 4. Send the result back to the screen
        return "courses/detail";
    }
    @GetMapping("/{id}/enroll")
    public String showEnrollmentForm(@PathVariable Long id, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean isStudent = userDetails != null && userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
        // 1. Check a rule -> decide what to do next
        if (!isStudent) {
            // 2. Send the result back to the screen
            return "redirect:/courses";
        }
        // 3. Get or save data in the database
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
        // 4. Put data on the page so the user can see it
        model.addAttribute("course", course);
        // 5. Put data on the page so the user can see it
        model.addAttribute("studentName", userDetails.getUser().getFullName());
        // 6. Put data on the page so the user can see it
        model.addAttribute("studentEmail", userDetails.getUsername());
        // 7. Send the result back to the screen
        return "courses/enroll";
    }
    @PostMapping("/{id}/enroll")
    public String processEnrollment(@PathVariable Long id, 
                                   @RequestParam("fullName") String fullName,
                                   @RequestParam("dob") String dobStr,
                                   @RequestParam("pastMarks") Double pastMarks,
                                   @RequestParam("highestQualification") String highestQualification,
                                   @RequestParam("boardUniversity") String boardUniversity,
                                   @RequestParam("passingYear") Integer passingYear,
                                   @RequestParam("marksheetFile") MultipartFile marksheetFile,
                                   @RequestParam(required = false) String comments,
                                   @AuthenticationPrincipal CustomUserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {
        boolean isStudent = userDetails != null && userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
        // 1. Check a rule -> decide what to do next
        if (!isStudent) {
            // 2. Show a one-time message on the next page
            redirectAttributes.addFlashAttribute("errorMessage", "Only students can apply for courses.");
            // 3. Send the result back to the screen
            return "redirect:/courses";
        }
        try {
            String fileName = UUID.randomUUID().toString() + "_" + marksheetFile.getOriginalFilename();
            Path uploadPath = Paths.get(UPLOAD_DIR);
            // 4. Check a rule -> decide what to do next
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(marksheetFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            // 5. Ask the service to do the main work
            enrollmentService.enrollStudent(userDetails.getUsername(), id, comments, 
                fullName, LocalDate.parse(dobStr), pastMarks, highestQualification, boardUniversity, passingYear, filePath.toString());
            // 6. Show a one-time message on the next page
            redirectAttributes.addFlashAttribute("successMessage", "Successfully enrolled in " + id + ". Application under review.");
            // 7. Send the result back to the screen
            return "redirect:/courses";
        } catch (IOException e) {
             // 8. Show a one-time message on the next page
             redirectAttributes.addFlashAttribute("errorMessage", "File upload failed: " + e.getMessage());
             // 9. Send the result back to the screen
             return "redirect:/courses/" + id + "/enroll";
        } catch (Exception e) {
            // 10. Show a one-time message on the next page
            redirectAttributes.addFlashAttribute("errorMessage", "Enrollment failed: " + e.getMessage());
            // 11. Send the result back to the screen
            return "redirect:/courses/" + id + "/enroll";
        }
    }
}
