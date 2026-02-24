package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.FacultySubjectAssignment;
import com.example.demo.entity.Subject;
import com.example.demo.entity.TeachingSchema;
import com.example.demo.entity.User;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.FacultySubjectAssignmentRepository;
import com.example.demo.repository.SubjectRepository;
import com.example.demo.repository.TeachingSchemaRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.TeachingSchemaSubjectIngestionService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/director")
public class DirectorManagementController {
    private static final Set<String> MANAGED_ROLES = Set.of("STUDENT", "AUTHORITY_FACULTY", "ROLE_STUDENT", "ROLE_AUTHORITY_FACULTY");
    private static final String FACULTY_ROLE = "AUTHORITY_FACULTY";

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final FacultySubjectAssignmentRepository facultySubjectAssignmentRepository;
    private final SubjectRepository subjectRepository;
    private final TeachingSchemaRepository teachingSchemaRepository;
    private final TeachingSchemaSubjectIngestionService teachingSchemaSubjectIngestionService;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    public DirectorManagementController(UserRepository userRepository,
                                        CourseRepository courseRepository,
                                        DepartmentRepository departmentRepository,
                                        EnrollmentRepository enrollmentRepository,
                                        FacultySubjectAssignmentRepository facultySubjectAssignmentRepository,
                                        SubjectRepository subjectRepository,
                                        TeachingSchemaRepository teachingSchemaRepository,
                                        TeachingSchemaSubjectIngestionService teachingSchemaSubjectIngestionService,
                                        PasswordEncoder passwordEncoder,
                                        JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.facultySubjectAssignmentRepository = facultySubjectAssignmentRepository;
        this.subjectRepository = subjectRepository;
        this.teachingSchemaRepository = teachingSchemaRepository;
        this.teachingSchemaSubjectIngestionService = teachingSchemaSubjectIngestionService;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        String department = resolveDepartment(principal);
        model.addAttribute("department", department);
        model.addAttribute("userName", principal.getUser().getFullName());
        model.addAttribute("courseCount", courseRepository.findAll().stream()
                .filter(c -> department.equalsIgnoreCase(c.getDepartment()))
                .count());
        model.addAttribute("studentCount", userRepository.findAll().stream()
                .filter(u -> hasRole(u, "STUDENT"))
                .filter(u -> department.equalsIgnoreCase(normalize(u.getDepartment())))
                .count());
        model.addAttribute("facultyCount", userRepository.findAll().stream()
                .filter(u -> hasRole(u, FACULTY_ROLE))
                .filter(User::isActive)
                .count());
        model.addAttribute("assignmentCount", countAllAssignments());
        return "director/dashboard";
    }

    @GetMapping("/users")
    public String users(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        String department = resolveDepartment(principal);
        List<User> faculty = userRepository.findAll().stream()
                .filter(u -> hasRole(u, FACULTY_ROLE))
                .toList();
        List<User> students = userRepository.findAll().stream()
                .filter(u -> hasRole(u, "STUDENT"))
                .toList();
        List<DirectorUserRow> rows = new ArrayList<>();

        for (User f : faculty) {
            rows.add(new DirectorUserRow(f, null, null));
        }

        for (User student : students) {
            Enrollment latestEnrollment = findLatestActiveEnrollment(student.getId());

            boolean belongsToDepartment = department.equalsIgnoreCase(normalize(student.getDepartment()));
            if (!belongsToDepartment && latestEnrollment == null) {
                continue;
            }

            String enrolledCourse = null;
            String enrolledDepartment = null;
            if (latestEnrollment != null) {
                String code = normalize(latestEnrollment.getCourse().getCode());
                String name = normalize(latestEnrollment.getCourse().getName());
                enrolledCourse = (code + " - " + name).trim();
                enrolledDepartment = latestEnrollment.getCourse().getDepartment();
            }
            rows.add(new DirectorUserRow(student, enrolledDepartment, enrolledCourse));
        }

        rows.sort(Comparator.comparing(r -> normalize(r.user().getFullName()), String.CASE_INSENSITIVE_ORDER));
        model.addAttribute("userRows", rows);
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
        if (!FACULTY_ROLE.equals(normalizeRole(role))) {
            redirectAttributes.addFlashAttribute("errorMessage", "Director can add only faculty users.");
            return "redirect:/director/users";
        }
        user.setDepartment(normalize(department).isBlank() ? resolveDepartment(principal) : normalize(department));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().clear();
        user.getRoles().add(normalizeRole(role));
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
        if (!MANAGED_ROLES.contains(role) && !MANAGED_ROLES.contains("ROLE_" + normalizeRole(role))) {
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
        if (!existingRole.isBlank() && !normalizeRole(existingRole).equals(normalizeRole(role))) {
            redirectAttributes.addFlashAttribute("errorMessage", "Role change is not allowed here.");
            return "redirect:/director/users";
        }
        user.setFullName(fullName);
        user.setEmail(email);
        user.setMobileNumber(mobileNumber);
        if (FACULTY_ROLE.equals(normalizeRole(role)) && department != null && !department.isBlank()) {
            user.setDepartment(normalize(department));
        }
        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }
        user.getRoles().clear();
        user.getRoles().add(normalizeRole(role));
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
        userRepository.saveAndFlush(user);
        redirectAttributes.addFlashAttribute("successMessage", "User deactivated.");
        return "redirect:/director/users";
    }

    @PostMapping("/users/{id}/activate")
    public String activateUser(@PathVariable Long id,
                               @AuthenticationPrincipal CustomUserDetails principal,
                               RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!canManageUser(resolveDepartment(principal), user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot activate this user.");
            return "redirect:/director/users";
        }
        if (!isManagedUser(user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot activate this user role.");
            return "redirect:/director/users";
        }
        user.setActive(true);
        userRepository.saveAndFlush(user);
        redirectAttributes.addFlashAttribute("successMessage", "User activated.");
        return "redirect:/director/users";
    }

    @GetMapping("/assignments")
    public String assignments(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        String department = resolveDepartment(principal);
        int syncedCount = syncSubjectsFromAllCourseSchemas();
        List<User> faculty = userRepository.findAll().stream()
                .filter(u -> hasRole(u, FACULTY_ROLE))
                .sorted(Comparator.comparing(f -> normalize(f.getFullName()), String.CASE_INSENSITIVE_ORDER))
                .toList();
        List<Subject> subjects = subjectRepository.findAll().stream()
                .sorted(Comparator.comparing((Subject s) -> normalize(s.getDepartment()), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(s -> normalize(s.getProgramName()), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(s -> s.getSemester() == null ? Integer.MAX_VALUE : s.getSemester())
                        .thenComparing(s -> normalize(s.getSubjectCode()), String.CASE_INSENSITIVE_ORDER))
                .toList();
        model.addAttribute("faculty", faculty);
        model.addAttribute("subjects", subjects);
        model.addAttribute("assignmentRows", fetchAssignmentRows());
        model.addAttribute("schemaSyncCount", syncedCount);
        model.addAttribute("department", department);
        return "director/assignments/list";
    }

    @PostMapping("/assignments")
    public String assignSubject(@AuthenticationPrincipal CustomUserDetails principal,
                                @RequestParam Long facultyId,
                                @RequestParam("subjectIds") List<Long> subjectIds,
                                RedirectAttributes redirectAttributes) {
        User faculty = userRepository.findById(facultyId).orElseThrow(() -> new IllegalArgumentException("Faculty not found"));
        if (!hasRole(faculty, FACULTY_ROLE)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Selected user is not faculty.");
            return "redirect:/director/assignments";
        }
        if (!faculty.isActive()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Selected faculty is inactive. Activate first and retry.");
            return "redirect:/director/assignments";
        }
        if (subjectIds == null || subjectIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Select at least one subject.");
            return "redirect:/director/assignments";
        }

        List<Subject> subjects = subjectRepository.findAllById(subjectIds);
        if (subjects.size() != subjectIds.size()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Some selected subjects were not found.");
            return "redirect:/director/assignments";
        }

        String facultyDepartment = normalize(faculty.getDepartment());
        List<Subject> invalidSubjects = subjects.stream()
                .filter(s -> !facultyDepartment.equalsIgnoreCase(normalize(s.getDepartment())))
                .toList();
        if (!invalidSubjects.isEmpty()) {
            String invalidCodes = invalidSubjects.stream()
                    .map(Subject::getSubjectCode)
                    .collect(Collectors.joining(", "));
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Faculty and subject must be in the same department. Invalid: " + invalidCodes);
            return "redirect:/director/assignments";
        }

        int created = 0;
        int skipped = 0;
        for (Subject subject : subjects) {
            if (facultySubjectAssignmentRepository.existsByFacultyIdAndSubjectId(facultyId, subject.getId())) {
                skipped++;
                continue;
            }
            FacultySubjectAssignment assignment = new FacultySubjectAssignment();
            assignment.setFaculty(faculty);
            assignment.setSubject(subject);
            facultySubjectAssignmentRepository.save(assignment);
            created++;
        }
        redirectAttributes.addFlashAttribute("successMessage",
                "Assignments processed. Added: " + created + ", skipped existing: " + skipped + ".");
        return "redirect:/director/assignments";
    }

    @PostMapping("/assignments/{id}/delete")
    public String removeAssignment(@PathVariable Long id,
                                   @AuthenticationPrincipal CustomUserDetails principal,
                                   RedirectAttributes redirectAttributes) {
        FacultySubjectAssignment assignment = facultySubjectAssignmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
        facultySubjectAssignmentRepository.delete(assignment);
        redirectAttributes.addFlashAttribute("successMessage", "Assignment removed.");
        return "redirect:/director/assignments";
    }

    private String resolveDepartment(CustomUserDetails principal) {
        String department = normalize(principal.getUser().getDepartment());
        return department == null || department.isBlank() ? "Engineering" : department;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isManagedUser(User user) {
        return user.getRoles().stream().map(this::normalizeRole).anyMatch(r -> Set.of("STUDENT", FACULTY_ROLE).contains(r));
    }

    private boolean canManageUser(String directorDepartment, User user) {
        if (hasRole(user, FACULTY_ROLE)) {
            return true;
        }
        if (directorDepartment.equalsIgnoreCase(normalize(user.getDepartment()))) {
            return true;
        }
        return hasAnyActiveEnrollment(user.getId());
    }

    private boolean hasAnyActiveEnrollment(Long studentId) {
        return findLatestActiveEnrollment(studentId) != null;
    }

    private Enrollment findLatestActiveEnrollment(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .filter(e -> e.getCourse() != null)
                .filter(e -> !Enrollment.EnrollmentStatus.CANCELLED.equals(e.getStatus()))
                .max(Comparator.comparing(Enrollment::getRegisteredAt))
                .orElse(null);
    }

    private List<String> getActiveDepartmentNames() {
        return departmentRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(d -> d.getName())
                .toList();
    }

    private boolean hasRole(User user, String role) {
        String normalized = normalizeRole(role);
        return user.getRoles().stream()
                .map(this::normalizeRole)
                .anyMatch(normalized::equalsIgnoreCase);
    }

    private String normalizeRole(String role) {
        if (role == null) {
            return "";
        }
        String value = role.trim();
        if (value.startsWith("ROLE_")) {
            value = value.substring("ROLE_".length());
        }
        return value;
    }

    private int countAllAssignments() {
        Integer count = jdbcTemplate.queryForObject("select count(*) from faculty_subject_assignments", Integer.class);
        return count == null ? 0 : count;
    }

    private List<AssignmentTableRow> fetchAssignmentRows() {
        return jdbcTemplate.query(
                """
                select
                  fsa.id,
                  u.full_name as faculty_name,
                  u.department as faculty_department,
                  s.program_name as program_name,
                  s.subject_code as subject_code,
                  s.subject_name as subject_name,
                  s.semester as semester,
                  s.department as subject_department,
                  fsa.assigned_at as assigned_at
                from faculty_subject_assignments fsa
                join users u on u.id = fsa.faculty_id
                join subjects s on s.id = fsa.subject_id
                order by fsa.assigned_at desc
                """,
                (rs, rowNum) -> new AssignmentTableRow(
                        rs.getLong("id"),
                        rs.getString("faculty_name"),
                        rs.getString("faculty_department"),
                        rs.getString("program_name"),
                        rs.getString("subject_code"),
                        rs.getString("subject_name"),
                        rs.getObject("semester") == null ? null : rs.getInt("semester"),
                        rs.getString("subject_department"),
                        rs.getTimestamp("assigned_at") == null ? null : rs.getTimestamp("assigned_at").toLocalDateTime()
                )
        );
    }

    private int syncSubjectsFromAllCourseSchemas() {
        int syncedCount = 0;
        List<Course> coursesWithSchema = courseRepository.findAll().stream()
                .filter(c -> c.getTeachingSchema() != null)
                .toList();

        for (Course course : coursesWithSchema) {
            if (course.getTeachingSchema() == null || course.getTeachingSchema().getId() == null) {
                continue;
            }
            if (subjectRepository.existsByTeachingSchemaId(course.getTeachingSchema().getId())) {
                continue;
            }
            String filePath = normalize(course.getTeachingSchema().getFilePath());
            if (filePath.isBlank()) {
                continue;
            }
            Path path = resolveSchemaPath(filePath);
            if (!Files.exists(path)) {
                continue;
            }
            try {
                syncedCount += teachingSchemaSubjectIngestionService.ingestSubjects(
                        course.getTeachingSchema(),
                        path,
                        course.getTeachingSchema().getFileName()
                );
            } catch (Throwable ignored) {
                // Keep assignment page available even if one schema cannot be parsed.
            }
        }

        // Also sync standalone/existing schema documents even if no current course is linked.
        for (var schema : teachingSchemaRepository.findAll()) {
            if (schema == null || schema.getId() == null) {
                continue;
            }
            String filePath = normalize(schema.getFilePath());
            if (filePath.isBlank()) {
                continue;
            }
            Path path = resolveSchemaPath(filePath);
            if (!Files.exists(path)) {
                continue;
            }
            try {
                syncedCount += teachingSchemaSubjectIngestionService.ingestSubjects(schema, path, schema.getFileName());
            } catch (Throwable ignored) {
                // Keep assignment page available even if one schema cannot be parsed.
            }
        }

        // Hard fallback: register+ingest schema files directly from uploads directory,
        // even when DB linkage is missing or stale.
        syncedCount += syncFromSchemaFilesDirectory();
        return syncedCount;
    }

    private int syncFromSchemaFilesDirectory() {
        int synced = 0;
        Path dir = Paths.get("uploads", "teaching-schemas").toAbsolutePath().normalize();
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            return 0;
        }
        try (var files = Files.list(dir)) {
            List<Path> schemaFiles = files
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String name = normalize(path.getFileName().toString()).toLowerCase(Locale.ROOT);
                        return name.endsWith(".pdf") || name.endsWith(".doc") || name.endsWith(".docx");
                    })
                    .toList();
            for (Path file : schemaFiles) {
                String absPath = file.toAbsolutePath().normalize().toString();
                TeachingSchema schema = teachingSchemaRepository.findByFilePath(absPath).orElseGet(() -> {
                    String fileName = file.getFileName().toString();
                    String program = inferProgramFromFileName(fileName);
                    String department = inferDepartmentFromProgram(program);
                    int nextVersion = teachingSchemaRepository
                            .findTopByDepartmentIgnoreCaseAndProgramNameIgnoreCaseOrderBySchemaVersionDesc(department, program)
                            .map(s -> s.getSchemaVersion() + 1)
                            .orElse(1);
                    TeachingSchema created = new TeachingSchema();
                    created.setDepartment(department);
                    created.setProgramName(program);
                    created.setSchemaVersion(nextVersion);
                    created.setFileName(fileName);
                    created.setFilePath(absPath);
                    return teachingSchemaRepository.save(created);
                });
                try {
                    synced += teachingSchemaSubjectIngestionService.ingestSubjects(schema, file, file.getFileName().toString());
                } catch (Throwable ignored) {
                    // Continue with next file.
                }
            }
        } catch (Exception ignored) {
            return synced;
        }
        return synced;
    }

    private String inferProgramFromFileName(String fileName) {
        String upper = normalize(fileName).toUpperCase(Locale.ROOT);
        List<String> knownPrograms = List.of("BCA", "MCA", "BBA", "MBA", "BTECH", "MTECH", "BHM", "BCOM", "MCOM");
        for (String program : knownPrograms) {
            if (upper.contains(program)) {
                return program;
            }
        }
        return "BCA";
    }

    private String inferDepartmentFromProgram(String program) {
        return switch (normalize(program).toUpperCase(Locale.ROOT)) {
            case "BCA", "MCA" -> "Computer Applications";
            case "BBA", "MBA" -> "Management";
            case "BTECH", "MTECH" -> "Engineering";
            case "BHM" -> "Hospitality";
            case "BCOM", "MCOM" -> "Commerce";
            default -> "Computer Applications";
        };
    }

    private Path resolveSchemaPath(String rawPath) {
        Path candidate = Path.of(rawPath);
        if (candidate.isAbsolute()) {
            return candidate.normalize();
        }
        Path direct = candidate.toAbsolutePath().normalize();
        if (Files.exists(direct)) {
            return direct;
        }
        Path uploadsFallback = Path.of("uploads", "teaching-schemas")
                .toAbsolutePath()
                .normalize()
                .resolve(candidate.getFileName() == null ? "" : candidate.getFileName().toString())
                .normalize();
        if (Files.exists(uploadsFallback)) {
            return uploadsFallback;
        }
        return direct;
    }

    public record DirectorUserRow(User user, String enrolledDepartment, String enrolledCourse) {
    }

    public record AssignmentTableRow(Long id,
                                     String facultyName,
                                     String facultyDepartment,
                                     String programName,
                                     String subjectCode,
                                     String subjectName,
                                     Integer semester,
                                     String subjectDepartment,
                                     java.time.LocalDateTime assignedAt) {
    }
}
