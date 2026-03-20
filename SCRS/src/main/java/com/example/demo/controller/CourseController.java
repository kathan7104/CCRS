package com.example.demo.controller;
import com.example.demo.entity.Course;
import com.example.demo.entity.EnrollmentDocument;
import com.example.demo.repository.CourseRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.EnrollmentService;
import com.example.demo.service.StudentAccessService;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
@Controller
@RequestMapping("/courses")
public class CourseController {
    private final CourseRepository courseRepository;
    private final EnrollmentService enrollmentService;
    private final StudentAccessService studentAccessService;
    private final String documentUploadDir;
    private static final long MAX_SINGLE_DOCUMENT_SIZE_BYTES = 20L * 1024L * 1024L; // 20MB
    public CourseController(CourseRepository courseRepository,
                            EnrollmentService enrollmentService,
                            StudentAccessService studentAccessService,
                            @Value("${ccrs.upload.documents-dir:uploads/documents}") String documentUploadDir) {
        this.courseRepository = courseRepository;
        this.enrollmentService = enrollmentService;
        this.studentAccessService = studentAccessService;
        this.documentUploadDir = documentUploadDir;
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
            model.addAttribute("userName", userDetails.getUser().getFullName());
        }
        if (userDetails != null) {
            isAuthority = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().startsWith("ROLE_AUTHORITY"));
            isStudent = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
        }
        boolean hasStudentAcademicAccess = !isStudent;
        if (isStudent && userDetails != null) {
            hasStudentAcademicAccess = studentAccessService.hasActiveEnrollment(userDetails.getUser());
        }
        model.addAttribute("isAuthority", isAuthority);
        model.addAttribute("isStudent", isStudent);
        model.addAttribute("hasStudentAcademicAccess", hasStudentAcademicAccess);
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
        boolean hasStudentAcademicAccess = !isStudent;
        if (isStudent && userDetails != null) {
            hasStudentAcademicAccess = studentAccessService.hasActiveEnrollment(userDetails.getUser());
        }
        model.addAttribute("isStudent", isStudent);
        model.addAttribute("hasStudentAcademicAccess", hasStudentAcademicAccess);
        return "courses/detail";
    }
    @GetMapping("/{id}/enroll")
    public String showEnrollmentForm(@PathVariable Long id,
                                     Model model,
                                     @AuthenticationPrincipal CustomUserDetails userDetails,
                                     RedirectAttributes redirectAttributes) {
        boolean isStudent = userDetails != null && userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
        // 1. Check a rule -> decide what to do next
        if (!isStudent) {
            // 2. Send the result back to the screen
            return "redirect:/courses";
        }
        try {
            enrollmentService.validateCanApplyForCourse(userDetails.getUsername(), id);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/courses";
        }
        // 3. Get or save data in the database
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
        // 4. Put data on the page so the user can see it
        model.addAttribute("course", course);
        model.addAttribute("documentTypeOptions", documentTypeOptions());
        model.addAttribute("requiredDocumentTypes", course.getRequiredDocumentTypeList());
        model.addAttribute("requiredDocumentLabels", course.getRequiredDocumentTypeList().stream()
                .map(type -> documentTypeOptions().getOrDefault(type, type))
                .toList());
        model.addAttribute("prerequisites", course.getPrerequisites().stream().toList());
        // 5. Put data on the page so the user can see it
        model.addAttribute("studentName", userDetails.getUser().getFullName());
        // 6. Put data on the page so the user can see it
        model.addAttribute("studentEmail", userDetails.getUsername());
        model.addAttribute("hasStudentAcademicAccess", studentAccessService.hasActiveEnrollment(userDetails.getUser()));
        // 7. Send the result back to the screen
        return "courses/enroll";
    }
    @PostMapping("/{id}/enroll")
    public String processEnrollment(@PathVariable Long id, 
                                   @RequestParam("fullName") String fullName,
                                   @RequestParam("dob") String dobStr,
                                   @RequestParam("gender") String gender,
                                   @RequestParam("caste") String caste,
                                   @RequestParam("pastMarks") String pastMarksStr,
                                   @RequestParam("highestQualification") String highestQualification,
                                   @RequestParam("boardUniversity") String boardUniversity,
                                   @RequestParam("passingYear") String passingYearStr,
                                   @RequestParam("documentTypes") List<String> documentTypes,
                                   @RequestParam("documentFiles") List<MultipartFile> documentFiles,
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
            LocalDate dob = LocalDate.parse(dobStr);
            Double pastMarks = Double.parseDouble(pastMarksStr);
            Integer passingYear = Integer.parseInt(passingYearStr);
            validateEnrollmentForm(fullName, dob, gender, caste, pastMarks, highestQualification, boardUniversity, passingYear);

            Path uploadPath = Paths.get(documentUploadDir).toAbsolutePath().normalize();
            // 4. Check a rule -> decide what to do next
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            List<EnrollmentService.DocumentPayload> documents = new ArrayList<>();
            boolean hasAtLeastOneDocument = false;
            int rowCount = Math.min(documentTypes.size(), documentFiles.size());
            for (int i = 0; i < rowCount; i++) {
                String documentTypeRaw = documentTypes.get(i) == null ? "" : documentTypes.get(i).trim();
                MultipartFile file = documentFiles.get(i);
                boolean hasType = !documentTypeRaw.isEmpty();
                boolean hasFile = file != null && !file.isEmpty();
                if (!hasType && !hasFile) {
                    continue;
                }
                if (!hasType || !hasFile) {
                    throw new IllegalArgumentException("Each document row must include both document type and file.");
                }
                String originalFileName = file.getOriginalFilename();
                if (originalFileName == null || originalFileName.trim().isEmpty()) {
                    throw new IllegalArgumentException("Uploaded file name cannot be empty.");
                }
                String safeOriginalName = Paths.get(originalFileName).getFileName().toString();
                validateUploadedDocument(file, safeOriginalName);
                String fileName = UUID.randomUUID() + "_" + safeOriginalName;
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                EnrollmentDocument.DocumentType documentType;
                try {
                    documentType = EnrollmentDocument.DocumentType.valueOf(documentTypeRaw.toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("Invalid document type selected: " + documentTypeRaw);
                }
                documents.add(new EnrollmentService.DocumentPayload(
                        documentType,
                        safeOriginalName,
                        filePath.toString(),
                        file.getContentType()
                ));
                hasAtLeastOneDocument = true;
            }
            if (!hasAtLeastOneDocument) {
                throw new IllegalArgumentException("Please add at least one document to upload.");
            }
            validateRequiredCourseDocuments(id, documents);
            // 5. Ask the service to do the main work
            enrollmentService.enrollStudent(userDetails.getUsername(), id, comments, 
                fullName, dob, gender, caste, pastMarks, highestQualification, boardUniversity, passingYear, documents);
            // 6. Show a one-time message on the next page
            redirectAttributes.addFlashAttribute("successMessage", "Application submitted successfully. Track status in My Applications.");
            // 7. Send the result back to the screen
            return "redirect:/dashboard";
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

    private void validateEnrollmentForm(String fullName,
                                        LocalDate dob,
                                        String gender,
                                        String caste,
                                        Double pastMarks,
                                        String highestQualification,
                                        String boardUniversity,
                                        Integer passingYear) {
        if (fullName == null || fullName.trim().isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (dob == null || dob.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth is invalid.");
        }
        if (gender == null || gender.trim().isBlank()) {
            throw new IllegalArgumentException("Gender is required.");
        }
        if (caste == null || caste.trim().isBlank()) {
            throw new IllegalArgumentException("Caste category is required.");
        }
        if (pastMarks == null || pastMarks < 0 || pastMarks > 100) {
            throw new IllegalArgumentException("Past marks must be between 0 and 100.");
        }
        if (highestQualification == null || highestQualification.trim().isBlank()) {
            throw new IllegalArgumentException("Highest qualification is required.");
        }
        if (boardUniversity == null || boardUniversity.trim().isBlank()) {
            throw new IllegalArgumentException("Board/University is required.");
        }
        int currentYear = LocalDate.now().getYear();
        if (passingYear == null || passingYear < 1990 || passingYear > currentYear) {
            throw new IllegalArgumentException("Passing year must be between 1990 and " + currentYear + ".");
        }
    }

    private void validateUploadedDocument(MultipartFile file, String safeOriginalName) {
        if (file.getSize() > MAX_SINGLE_DOCUMENT_SIZE_BYTES) {
            throw new IllegalArgumentException("Each file must be 20MB or smaller.");
        }
        String lower = safeOriginalName.toLowerCase(Locale.ROOT);
        boolean supported = lower.endsWith(".pdf")
                || lower.endsWith(".jpg")
                || lower.endsWith(".jpeg")
                || lower.endsWith(".png");
        if (!supported) {
            throw new IllegalArgumentException("Unsupported file type for " + safeOriginalName
                    + ". Only PDF, JPG, JPEG, and PNG are allowed.");
        }
    }

    private void validateRequiredCourseDocuments(Long courseId, List<EnrollmentService.DocumentPayload> documents) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + courseId));
        List<String> required = course.getRequiredDocumentTypeList();
        if (required.isEmpty()) {
            return;
        }
        Set<String> uploaded = documents.stream()
                .map(EnrollmentService.DocumentPayload::documentType)
                .map(Enum::name)
                .collect(Collectors.toSet());
        List<String> missing = required.stream()
                .filter(type -> !uploaded.contains(type))
                .toList();
        if (!missing.isEmpty()) {
            String labels = missing.stream()
                    .map(type -> documentTypeOptions().getOrDefault(type, type))
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Missing required documents: " + labels);
        }
    }

    private Map<String, String> documentTypeOptions() {
        Map<String, String> options = new LinkedHashMap<>();
        options.put("SSC_MARKSHEET", "SSC Marksheet");
        options.put("HSC_MARKSHEET", "HSC Marksheet");
        options.put("SCHOOL_LEAVING_CERTIFICATE", "School Leaving Certificate");
        options.put("BACHELOR_SEMESTER_MARKSHEET", "Bachelor Semester Marksheet");
        options.put("DEGREE_CERTIFICATE", "Degree Certificate");
        options.put("MARKSHEET", "Other Marksheet");
        options.put("ID_PROOF", "ID Proof");
        options.put("ADDRESS_PROOF", "Address Proof");
        options.put("PASSPORT_PHOTO", "Passport Photo");
        options.put("CASTE_CERTIFICATE", "Caste Certificate");
        options.put("INCOME_CERTIFICATE", "Income Certificate");
        options.put("TRANSFER_CERTIFICATE", "Transfer Certificate");
        options.put("OTHER", "Other");
        return options;
    }
}




