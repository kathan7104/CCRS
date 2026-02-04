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
    public String listCourses(Model model) {
        List<Course> courses = courseRepository.findAll();
        model.addAttribute("courses", courses);
        return "courses/list";
    }

    @GetMapping("/{id}")
    public String courseDetails(@PathVariable Long id, Model model) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
        model.addAttribute("course", course);
        return "courses/detail";
    }

    @GetMapping("/{id}/enroll")
    public String showEnrollmentForm(@PathVariable Long id, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
        
        model.addAttribute("course", course);
        model.addAttribute("studentName", userDetails.getUser().getFullName());
        model.addAttribute("studentEmail", userDetails.getUsername());
        
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
        try {
            // Save file
            String fileName = UUID.randomUUID().toString() + "_" + marksheetFile.getOriginalFilename();
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(marksheetFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            enrollmentService.enrollStudent(userDetails.getUsername(), id, comments, 
                fullName, LocalDate.parse(dobStr), pastMarks, highestQualification, boardUniversity, passingYear, filePath.toString());
            
            redirectAttributes.addFlashAttribute("successMessage", "Successfully enrolled in " + id + ". Application under review.");
            return "redirect:/courses";
        } catch (IOException e) {
             redirectAttributes.addFlashAttribute("errorMessage", "File upload failed: " + e.getMessage());
             return "redirect:/courses/" + id + "/enroll";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Enrollment failed: " + e.getMessage());
            return "redirect:/courses/" + id + "/enroll";
        }
    }
}
