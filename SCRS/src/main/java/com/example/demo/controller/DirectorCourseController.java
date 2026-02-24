package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.entity.TeachingSchema;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.TeachingSchemaRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.TeachingSchemaSubjectIngestionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/director/courses")
public class DirectorCourseController {
    private static final String TEACHING_SCHEMA_UPLOAD_DIR = "uploads/teaching-schemas/";
    private static final List<String> PROGRAM_NAMES = List.of(
            "BCA",
            "MCA",
            "BBA",
            "MBA",
            "BTECH",
            "MTECH",
            "BHM",
            "BCOM",
            "MCOM"
    );
    private static final Pattern TRAILING_NUMBER_PATTERN = Pattern.compile("-(\\d+)$");

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final TeachingSchemaRepository teachingSchemaRepository;
    private final TeachingSchemaSubjectIngestionService teachingSchemaSubjectIngestionService;

    public DirectorCourseController(CourseRepository courseRepository,
                                    DepartmentRepository departmentRepository,
                                    TeachingSchemaRepository teachingSchemaRepository,
                                    TeachingSchemaSubjectIngestionService teachingSchemaSubjectIngestionService) {
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
        this.teachingSchemaRepository = teachingSchemaRepository;
        this.teachingSchemaSubjectIngestionService = teachingSchemaSubjectIngestionService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        return "director/courses/list";
    }

    @GetMapping("/new")
    public String createForm(Model model, @AuthenticationPrincipal CustomUserDetails principal) {
        Course course = new Course();
        course.setDepartment(resolveDepartment(principal));
        course.setProgramName("BCA");
        course.setBatchYear(LocalDate.now().getYear());
        course.setProgramLevel("UG");
        course.setLevel("UG");
        course.setDurationSemesters(6);
        model.addAttribute("course", course);
        loadFormOptions(model, course, null);
        return "director/courses/form";
    }

    @PostMapping
    public String create(@ModelAttribute Course course,
                         @RequestParam(name = "requiredDocumentTypes", required = false) List<String> requiredDocumentTypes,
                         @RequestParam(name = "existingTeachingSchemaId", required = false) Long existingTeachingSchemaId,
                         @RequestParam(name = "teachingSchemaFile", required = false) MultipartFile teachingSchemaFile,
                         RedirectAttributes redirectAttributes) {
        try {
            course.setDepartment(cleanText(course.getDepartment()));
            course.setProgramName(cleanText(course.getProgramName()));
            course.setName(course.getProgramName());
            if (course.getBatchYear() == null) {
                course.setBatchYear(LocalDate.now().getYear());
            }
            course.setCode(generateNextCourseCode(course.getName(), course.getBatchYear()));
            course.setProgramLevel(cleanText(course.getProgramLevel()));
            course.setLevel(cleanText(course.getLevel()));
            course.setTeachingSchema(resolveTeachingSchema(course, existingTeachingSchemaId, teachingSchemaFile));
            course.setRequiredDocumentTypeList(normalizeDocumentTypes(requiredDocumentTypes));
            normalizeCapacity(course);
            courseRepository.save(course);
            ensureSubjectsExtracted(course.getTeachingSchema());
            redirectAttributes.addFlashAttribute("successMessage", "Course created successfully.");
            return "redirect:/director/courses";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create course: " + rootCauseMessage(e));
            return "redirect:/director/courses/new";
        } catch (Throwable t) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create course: " + rootCauseMessage(t));
            return "redirect:/director/courses/new";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
        List<Course> allCourses = courseRepository.findAll();
        allCourses.removeIf(c -> c.getId().equals(id));
        model.addAttribute("course", course);
        loadFormOptions(model, course, allCourses);
        return "director/courses/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute Course form,
                         @RequestParam(name = "requiredDocumentTypes", required = false) List<String> requiredDocumentTypes,
                         @RequestParam(name = "existingTeachingSchemaId", required = false) Long existingTeachingSchemaId,
                         @RequestParam(name = "teachingSchemaFile", required = false) MultipartFile teachingSchemaFile,
                         RedirectAttributes redirectAttributes) {
        try {
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
            course.setDepartment(cleanText(form.getDepartment()));
            course.setProgramName(cleanText(form.getProgramName()));
            course.setName(course.getProgramName());
            course.setBatchYear(form.getBatchYear());
            if (course.getBatchYear() == null) {
                course.setBatchYear(LocalDate.now().getYear());
            }
            if (!isCodeMatchingCourseAndBatch(course.getCode(), course.getName(), course.getBatchYear())) {
                course.setCode(generateNextCourseCode(course.getName(), course.getBatchYear()));
            }
            course.setCapacity(form.getCapacity());
            course.setRemainingSeats(form.getRemainingSeats());
            course.setCredits(form.getCredits());
            course.setFee(form.getFee());
            course.setProgramLevel(cleanText(form.getProgramLevel()));
            course.setLevel(cleanText(form.getLevel()));
            course.setDurationSemesters(form.getDurationSemesters());
            course.setRequiredQualification(form.getRequiredQualification());
            course.setTeachingSchema(resolveTeachingSchema(course, existingTeachingSchemaId, teachingSchemaFile));
            course.setRequiredDocumentTypeList(normalizeDocumentTypes(requiredDocumentTypes));
            normalizeCapacity(course);
            courseRepository.save(course);
            ensureSubjectsExtracted(course.getTeachingSchema());
            redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully.");
            return "redirect:/director/courses";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update course: " + rootCauseMessage(e));
            return "redirect:/director/courses/" + id + "/edit";
        } catch (Throwable t) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update course: " + rootCauseMessage(t));
            return "redirect:/director/courses/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
        courseRepository.delete(course);
        redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully.");
        return "redirect:/director/courses";
    }

    private void loadFormOptions(Model model, Course course, List<Course> explicitAllCourses) {
        List<Course> allCourses = explicitAllCourses != null ? explicitAllCourses : courseRepository.findAll().stream()
                .filter(c -> nullSafe(c.getDepartment()).equalsIgnoreCase(nullSafe(course.getDepartment())))
                .toList();
        model.addAttribute("allCourses", allCourses);
        model.addAttribute("departments", getActiveDepartmentNames());
        model.addAttribute("programNames", resolveProgramOptions());
        model.addAttribute("documentTypeOptions", documentTypeOptions());
        model.addAttribute("teachingSchemas", teachingSchemaRepository.findAll().stream()
                .sorted((a, b) -> {
                    int programCompare = nullSafe(a.getProgramName()).compareToIgnoreCase(nullSafe(b.getProgramName()));
                    if (programCompare != 0) {
                        return programCompare;
                    }
                    int versionA = a.getSchemaVersion() == null ? 0 : a.getSchemaVersion();
                    int versionB = b.getSchemaVersion() == null ? 0 : b.getSchemaVersion();
                    return Integer.compare(versionB, versionA);
                })
                .toList());
    }

    private TeachingSchema resolveTeachingSchema(Course course,
                                                 Long existingTeachingSchemaId,
                                                 MultipartFile teachingSchemaFile) throws IOException {
        if (teachingSchemaFile != null && !teachingSchemaFile.isEmpty()) {
            String originalFileName = teachingSchemaFile.getOriginalFilename();
            if (originalFileName == null || originalFileName.isBlank()) {
                throw new IllegalArgumentException("Teaching schema file name is invalid.");
            }
            String lowerFileName = originalFileName.toLowerCase();
            if (!(lowerFileName.endsWith(".pdf")
                    || lowerFileName.endsWith(".doc")
                    || lowerFileName.endsWith(".docx"))) {
                throw new IllegalArgumentException("Teaching schema must be a PDF, DOC, or DOCX file.");
            }
            String department = cleanText(course.getDepartment());
            String programName = cleanText(course.getProgramName());
            int nextVersion = teachingSchemaRepository
                    .findTopByDepartmentIgnoreCaseAndProgramNameIgnoreCaseOrderBySchemaVersionDesc(department, programName)
                    .map(s -> s.getSchemaVersion() + 1)
                    .orElse(1);
            Path uploadPath = Paths.get(TEACHING_SCHEMA_UPLOAD_DIR);
            uploadPath = uploadPath.toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String safeOriginalName = Paths.get(originalFileName).getFileName().toString();
            String storedName = UUID.randomUUID() + "_" + safeOriginalName;
            Path storedPath = uploadPath.resolve(storedName);
            Files.copy(teachingSchemaFile.getInputStream(), storedPath, StandardCopyOption.REPLACE_EXISTING);

            TeachingSchema schema = new TeachingSchema();
            schema.setDepartment(department);
            schema.setProgramName(programName);
            schema.setSchemaVersion(nextVersion);
            schema.setFileName(safeOriginalName);
            schema.setFilePath(storedPath.toAbsolutePath().normalize().toString());
            TeachingSchema savedSchema = teachingSchemaRepository.save(schema);
            try {
                teachingSchemaSubjectIngestionService.ingestSubjects(savedSchema, storedPath, safeOriginalName);
            } catch (Throwable ignored) {
                // Keep course creation/update flow working even when parser runtime libraries are unavailable.
            }
            return savedSchema;
        }

        if (existingTeachingSchemaId != null) {
            TeachingSchema existing = teachingSchemaRepository.findById(existingTeachingSchemaId)
                    .orElseThrow(() -> new IllegalArgumentException("Selected teaching schema not found."));
            if (!existing.getDepartment().equalsIgnoreCase(nullSafe(course.getDepartment()))
                    || !existing.getProgramName().equalsIgnoreCase(nullSafe(course.getProgramName()))) {
                throw new IllegalArgumentException("Selected teaching schema does not match selected department/program.");
            }
            return existing;
        }

        return null;
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
        if (course.getDurationSemesters() == null || course.getDurationSemesters() < 1) {
            course.setDurationSemesters(1);
        }
        if (course.getBatchYear() == null || course.getBatchYear() < 1900 || course.getBatchYear() > 3000) {
            course.setBatchYear(LocalDate.now().getYear());
        }
    }

    private String resolveDepartment(CustomUserDetails principal) {
        if (principal == null || principal.getUser() == null) {
            return "Computer Applications";
        }
        String department = principal.getUser().getDepartment();
        return department == null || department.isBlank() ? "Computer Applications" : department;
    }

    private String cleanText(String value) {
        return value == null ? "" : value.trim();
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    private List<String> getActiveDepartmentNames() {
        return departmentRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(d -> d.getName())
                .toList();
    }

    private List<String> resolveProgramOptions() {
        Set<String> values = new HashSet<>(PROGRAM_NAMES);
        courseRepository.findAll().stream()
                .map(Course::getProgramName)
                .map(this::cleanText)
                .filter(s -> !s.isBlank())
                .forEach(values::add);
        teachingSchemaRepository.findAll().stream()
                .map(TeachingSchema::getProgramName)
                .map(this::cleanText)
                .filter(s -> !s.isBlank())
                .forEach(values::add);
        List<String> ordered = new ArrayList<>(values);
        ordered.sort(String.CASE_INSENSITIVE_ORDER);
        return ordered;
    }

    private List<String> normalizeDocumentTypes(List<String> rawValues) {
        if (rawValues == null || rawValues.isEmpty()) {
            return List.of();
        }
        Set<String> allowed = documentTypeOptions().keySet();
        List<String> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (String raw : rawValues) {
            String value = raw == null ? "" : raw.trim().toUpperCase(Locale.ROOT);
            if (value.isBlank() || !allowed.contains(value) || seen.contains(value)) {
                continue;
            }
            result.add(value);
            seen.add(value);
        }
        return result;
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

    private boolean isCodeMatchingCourseAndBatch(String currentCode, String courseName, Integer batchYear) {
        if (batchYear == null) {
            return false;
        }
        String prefix = codePrefixFromName(courseName) + "-" + batchYear + "-";
        return currentCode != null && currentCode.startsWith(prefix);
    }

    private void ensureSubjectsExtracted(TeachingSchema schema) {
        if (schema == null || schema.getId() == null) {
            return;
        }
        String filePath = nullSafe(schema.getFilePath()).trim();
        if (filePath.isBlank()) {
            return;
        }
        Path path = resolveSchemaPath(filePath);
        if (!Files.exists(path)) {
            return;
        }
        try {
            teachingSchemaSubjectIngestionService.ingestSubjects(schema, path, schema.getFileName());
        } catch (Throwable ignored) {
            // Keep course save successful even if extraction fails here.
        }
    }

    private String generateNextCourseCode(String courseName, Integer batchYear) {
        if (batchYear == null) {
            throw new IllegalArgumentException("Batch year is required to generate course code.");
        }
        String prefix = codePrefixFromName(courseName);
        String codePrefix = prefix + "-" + batchYear + "-";
        int nextNumber = courseRepository.findAll().stream()
                .map(Course::getCode)
                .filter(code -> code != null && code.startsWith(codePrefix))
                .mapToInt(this::extractTrailingNumber)
                .max()
                .orElse(0) + 1;
        return codePrefix + String.format("%03d", nextNumber);
    }

    private int extractTrailingNumber(String code) {
        if (code == null) {
            return 0;
        }
        Matcher matcher = TRAILING_NUMBER_PATTERN.matcher(code);
        if (!matcher.find()) {
            return 0;
        }
        try {
            return Integer.parseInt(matcher.group(1));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private String codePrefixFromName(String courseName) {
        String normalizedName = nullSafe(courseName).trim();
        if (normalizedName.isBlank()) {
            throw new IllegalArgumentException("Course name is required to generate course code.");
        }
        String[] words = normalizedName.toUpperCase().replaceAll("[^A-Z0-9 ]", " ").trim().split("\\s+");
        StringBuilder initials = new StringBuilder();
        for (String word : words) {
            if (!word.isBlank()) {
                initials.append(word.charAt(0));
            }
        }
        String prefix = initials.toString();
        if (prefix.isBlank()) {
            prefix = normalizedName.toUpperCase().replaceAll("[^A-Z0-9]", "");
        }
        return prefix.length() > 8 ? prefix.substring(0, 8) : prefix;
    }

    private String rootCauseMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        String msg = current.getMessage();
        if (msg == null || msg.isBlank()) {
            msg = throwable.getMessage();
        }
        if (msg == null || msg.isBlank()) {
            msg = current.getClass().getSimpleName();
        }
        return msg;
    }

    private Path resolveSchemaPath(String rawPath) {
        Path candidate = Paths.get(rawPath);
        if (candidate.isAbsolute()) {
            return candidate.normalize();
        }
        Path direct = candidate.toAbsolutePath().normalize();
        if (Files.exists(direct)) {
            return direct;
        }
        Path uploadsFallback = Paths.get(TEACHING_SCHEMA_UPLOAD_DIR)
                .toAbsolutePath()
                .normalize()
                .resolve(candidate.getFileName() == null ? "" : candidate.getFileName().toString())
                .normalize();
        if (Files.exists(uploadsFallback)) {
            return uploadsFallback;
        }
        return direct;
    }
}
