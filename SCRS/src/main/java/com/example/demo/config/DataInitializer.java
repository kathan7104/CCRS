package com.example.demo.config;
import com.example.demo.entity.Course;
import com.example.demo.entity.Department;
import com.example.demo.entity.FeeStructure;
import com.example.demo.entity.Subject;
import com.example.demo.entity.TeachingSchema;
import com.example.demo.entity.User;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.FacultySubjectAssignmentRepository;
import com.example.demo.repository.FeeStructureRepository;
import com.example.demo.repository.SubjectRepository;
import com.example.demo.repository.TeachingSchemaRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TeachingSchemaSubjectIngestionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
@Configuration
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SubjectRepository subjectRepository;
    private final TeachingSchemaRepository teachingSchemaRepository;
    private final TeachingSchemaSubjectIngestionService teachingSchemaSubjectIngestionService;
    private final FacultySubjectAssignmentRepository facultySubjectAssignmentRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;
    @Value("${ccrs.dev.create-authority:false}")
    private boolean createAuthority;

    @Value("${ccrs.dev.seed-sample-academics:false}")
    private boolean seedSampleAcademics;

    @Value("${ccrs.dev.seed-demo-faculty:false}")
    private boolean seedDemoFaculty;

    @Value("${ccrs.upload.teaching-schema-dir:uploads/teaching-schemas}")
    private String teachingSchemaUploadDir;
    public DataInitializer(UserRepository userRepository,
                           CourseRepository courseRepository,
                           DepartmentRepository departmentRepository,
                           EnrollmentRepository enrollmentRepository,
                           SubjectRepository subjectRepository,
                           TeachingSchemaRepository teachingSchemaRepository,
                           TeachingSchemaSubjectIngestionService teachingSchemaSubjectIngestionService,
                           FacultySubjectAssignmentRepository facultySubjectAssignmentRepository,
                           FeeStructureRepository feeStructureRepository,
                           PasswordEncoder passwordEncoder,
                           JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.subjectRepository = subjectRepository;
        this.teachingSchemaRepository = teachingSchemaRepository;
        this.teachingSchemaSubjectIngestionService = teachingSchemaSubjectIngestionService;
        this.facultySubjectAssignmentRepository = facultySubjectAssignmentRepository;
        this.feeStructureRepository = feeStructureRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public void run(String... args) throws Exception {
        patchLegacyCourseSchema();
        seedDepartments();
        if (seedSampleAcademics) {
            resetAndCreateSampleCourses();
            resetAndCreateSampleSubjects();
        }
        backfillTeachingSchemaSubjects();
        seedDefaultFeeStructure();
        if (seedDemoFaculty) {
            ensureDemoFaculty();
        }
        // 1. Check a rule -> decide what to do next
        if (!createAuthority) return;
        String superAdminEmail = "superadmin@college.edu";
        if (userRepository.findByEmail(superAdminEmail).isEmpty()) {
            User sa = new User();
            sa.setEmail(superAdminEmail);
            sa.setMobileNumber("9000000000");
            sa.setFullName("College Super Admin (DEMO)");
            sa.setDepartment("Central");
            sa.setPassword(passwordEncoder.encode("SuperAdmin123!"));
            sa.getRoles().add("AUTHORITY_SUPER_ADMIN");
            sa.setEmailVerified(true);
            sa.setMobileVerified(true);
            userRepository.save(sa);
            System.out.println("Created demo super admin account: " + superAdminEmail + " (password: SuperAdmin123!)");
        }
        String directorEmail = "director@college.edu";
        // 2. Check a rule -> decide what to do next
        if (userRepository.findByEmail(directorEmail).isEmpty()) {
            User u = new User();
            u.setEmail(directorEmail);
            u.setMobileNumber("9000000001");
            u.setFullName("College Director (DEMO)");
            u.setDepartment("Engineering");
            // 3. Security: hide the password before saving
            u.setPassword(passwordEncoder.encode("Director123!"));
            u.getRoles().add("AUTHORITY_DIRECTOR");
            u.setEmailVerified(true);
            u.setMobileVerified(true);
            // 4. Get or save data in the database
            userRepository.save(u);
            System.out.println("Created demo director account: " + directorEmail + " (password: Director123!)");
        }
        String adminEmail = "admin@college.edu";
        // 5. Check a rule -> decide what to do next
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User a = new User();
            a.setEmail(adminEmail);
            a.setMobileNumber("9000000002");
            a.setFullName("College Admin (DEMO)");
            a.setDepartment("Central");
            // 6. Security: hide the password before saving
            a.setPassword(passwordEncoder.encode("Admin123!"));
            a.getRoles().add("AUTHORITY_ADMIN");
            a.setEmailVerified(true);
            a.setMobileVerified(true);
            // 7. Get or save data in the database
            userRepository.save(a);
            System.out.println("Created demo admin account: " + adminEmail + " (password: Admin123!)");
        }
        String facultyEmail = "faculty@college.edu";
        if (userRepository.findByEmail(facultyEmail).isEmpty()) {
            System.out.println("Created demo faculty account: " + facultyEmail + " (password: Faculty123!)");
        }
        String staffEmail = "staff@college.edu";
        if (userRepository.findByEmail(staffEmail).isEmpty()) {
            User s = new User();
            s.setEmail(staffEmail);
            s.setMobileNumber("9000000004");
            s.setFullName("Account Staff (DEMO)");
            s.setDepartment("Accounts");
            s.setPassword(passwordEncoder.encode("Staff123!"));
            s.getRoles().add("AUTHORITY_STAFF");
            s.setEmailVerified(true);
            s.setMobileVerified(true);
            userRepository.save(s);
            System.out.println("Created demo account staff account: " + staffEmail + " (password: Staff123!)");
        }
    }
    private void patchLegacyCourseSchema() {
        // Make old MySQL schemas compatible with new Course fields.
        executeSql("CREATE TABLE IF NOT EXISTS departments (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL UNIQUE, " +
                "is_active BIT NOT NULL DEFAULT 1, " +
                "created_at DATETIME NOT NULL, " +
                "updated_at DATETIME" +
                ")");
        executeSql("CREATE TABLE IF NOT EXISTS teaching_schemas (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "department VARCHAR(100) NOT NULL, " +
                "program_name VARCHAR(100) NOT NULL, " +
                "schema_version INT NOT NULL, " +
                "file_name VARCHAR(255) NOT NULL, " +
                "file_path VARCHAR(500) NOT NULL, " +
                "uploaded_at DATETIME NOT NULL" +
                ")");
        executeSql("CREATE TABLE IF NOT EXISTS subjects (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "department VARCHAR(100) NOT NULL, " +
                "program_name VARCHAR(100) NOT NULL, " +
                "subject_code VARCHAR(50) NOT NULL, " +
                "subject_name VARCHAR(255) NOT NULL, " +
                "semester INT NULL, " +
                "credits INT NULL, " +
                "teaching_schema_id BIGINT NULL, " +
                "created_at DATETIME NOT NULL, " +
                "updated_at DATETIME NULL, " +
                "UNIQUE KEY uk_subject_department_code (department, subject_code)" +
                ")");
        executeSql("CREATE TABLE IF NOT EXISTS faculty_subject_assignments (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "faculty_id BIGINT NOT NULL, " +
                "subject_id BIGINT NOT NULL, " +
                "assigned_at DATETIME NOT NULL, " +
                "UNIQUE KEY uk_faculty_subject (faculty_id, subject_id)" +
                ")");
        executeSql("ALTER TABLE courses ADD COLUMN IF NOT EXISTS program_name VARCHAR(100) NULL");
        executeSql("ALTER TABLE courses ADD COLUMN IF NOT EXISTS batch_year INT NULL");
        executeSql("ALTER TABLE courses ADD COLUMN IF NOT EXISTS duration_semesters INT NULL");
        executeSql("ALTER TABLE courses ADD COLUMN IF NOT EXISTS teaching_schema_id BIGINT NULL");
        executeSql("ALTER TABLE courses ADD COLUMN IF NOT EXISTS required_document_types VARCHAR(500) NULL");
        executeSql("UPDATE courses SET program_name = COALESCE(NULLIF(program_name, ''), code)");
        executeSql("UPDATE courses SET batch_year = COALESCE(batch_year, YEAR(created_at), YEAR(CURDATE()))");
        executeSql("UPDATE courses SET duration_semesters = COALESCE(duration_semesters, CASE WHEN duration_years IS NOT NULL THEN duration_years * 2 ELSE 6 END)");
        executeSql("ALTER TABLE courses MODIFY COLUMN program_name VARCHAR(100) NOT NULL");
        executeSql("ALTER TABLE courses MODIFY COLUMN batch_year INT NOT NULL");
        executeSql("ALTER TABLE courses MODIFY COLUMN duration_semesters INT NOT NULL");
        // Keep old column nullable so inserts that don't mention it still work.
        executeSql("ALTER TABLE courses MODIFY COLUMN duration_years INT NULL");
        executeSql("CREATE INDEX IF NOT EXISTS idx_teaching_schema_department_program ON teaching_schemas (department, program_name)");
        executeSql("CREATE INDEX IF NOT EXISTS idx_subject_department ON subjects (department)");
        executeSql("ALTER TABLE payments ADD COLUMN IF NOT EXISTS gateway_order_id VARCHAR(255) NULL");
        executeSql("ALTER TABLE payments ADD COLUMN IF NOT EXISTS gateway_signature VARCHAR(500) NULL");
    }
    private void seedDepartments() {
        try {
            for (String name : DepartmentCatalog.departments()) {
                if (departmentRepository.findByNameIgnoreCase(name).isPresent()) {
                    continue;
                }
                Department department = new Department();
                department.setName(name);
                department.setActive(true);
                departmentRepository.save(department);
            }
        } catch (Exception ex) {
            System.out.println("Skipped department seed at startup: " + ex.getMessage());
        }
    }
    private void executeSql(String sql) {
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception ignored) {
            // Ignore compatibility SQL errors if DB is already in desired state.
        }
    }
    private void resetAndCreateSampleCourses() {
        int batchYear = LocalDate.now().getYear();
        createCourse("BBA-" + batchYear + "-001", "Business Fundamentals", "Management", "BBA", batchYear, 120, 120, 3, 120000, "UG", 6, "12th pass (Commerce/Any stream) with minimum 50%");
        createCourse("BCA-" + batchYear + "-001", "Introduction to Programming", "Computer Applications", "BCA", batchYear, 120, 120, 3, 130000, "UG", 6, "12th pass with Mathematics/Computer Science (50%+)");
        createCourse("BHM-" + batchYear + "-001", "Hospitality Operations Basics", "Hospitality", "BHM", batchYear, 90, 90, 3, 110000, "UG", 6, "12th pass (any stream) with minimum 45%");
        createCourse("BTECH-" + batchYear + "-001", "Engineering Mathematics I", "Engineering", "BTECH", batchYear, 180, 180, 4, 220000, "UG", 8, "12th pass (PCM) with minimum 60%");
        createCourse("BTECH-" + batchYear + "-002", "IT Systems Fundamentals", "Engineering", "BTECH", batchYear, 180, 180, 4, 210000, "UG", 8, "12th pass (PCM) with minimum 60%");
        createCourse("MBA-" + batchYear + "-001", "Management Principles", "Management", "MBA", batchYear, 120, 120, 3, 250000, "PG", 4, "Graduation in any discipline with minimum 50%");
        createCourse("MCA-" + batchYear + "-001", "Advanced Data Structures", "Computer Applications", "MCA", batchYear, 120, 120, 3, 180000, "PG", 4, "Graduation with Mathematics/CS/IT (50%+)");
        createCourse("MTECH-" + batchYear + "-001", "Research Methods in Computing", "Engineering", "MTECH", batchYear, 60, 60, 3, 260000, "PG", 4, "B.Tech/BE in CSE/IT (60%+)");
        System.out.println("Reset and created sample courses.");
    }
    private void resetAndCreateSampleSubjects() {
        // BCA
        createSubject("Computer Applications", "BCA", "BCA-S1-101", "Programming Fundamentals", 1, 4);
        createSubject("Computer Applications", "BCA", "BCA-S1-102", "Discrete Mathematics", 1, 3);
        createSubject("Computer Applications", "BCA", "BCA-S1-103", "Digital Computer Basics", 1, 3);
        createSubject("Computer Applications", "BCA", "BCA-S2-201", "Data Structures", 2, 4);
        createSubject("Computer Applications", "BCA", "BCA-S2-202", "Database Management Systems", 2, 4);
        createSubject("Computer Applications", "BCA", "BCA-S2-203", "Operating Systems", 2, 3);
        createSubject("Computer Applications", "BCA", "BCA-S3-301", "Java Programming", 3, 4);
        createSubject("Computer Applications", "BCA", "BCA-S3-302", "Web Technologies", 3, 4);

        // MCA
        createSubject("Computer Applications", "MCA", "MCA-S1-101", "Advanced Data Structures", 1, 4);
        createSubject("Computer Applications", "MCA", "MCA-S1-102", "Design and Analysis of Algorithms", 1, 4);
        createSubject("Computer Applications", "MCA", "MCA-S1-103", "Advanced DBMS", 1, 4);
        createSubject("Computer Applications", "MCA", "MCA-S2-201", "Cloud Computing", 2, 4);
        createSubject("Computer Applications", "MCA", "MCA-S2-202", "Machine Learning Basics", 2, 4);
        createSubject("Computer Applications", "MCA", "MCA-S2-203", "Software Project Management", 2, 3);

        // BBA
        createSubject("Management", "BBA", "BBA-S1-101", "Principles of Management", 1, 3);
        createSubject("Management", "BBA", "BBA-S1-102", "Business Communication", 1, 3);
        createSubject("Management", "BBA", "BBA-S1-103", "Financial Accounting", 1, 4);
        createSubject("Management", "BBA", "BBA-S2-201", "Human Resource Management", 2, 3);
        createSubject("Management", "BBA", "BBA-S2-202", "Marketing Management", 2, 4);
        createSubject("Management", "BBA", "BBA-S2-203", "Business Statistics", 2, 4);

        // MBA
        createSubject("Management", "MBA", "MBA-S1-101", "Managerial Economics", 1, 4);
        createSubject("Management", "MBA", "MBA-S1-102", "Organizational Behavior", 1, 3);
        createSubject("Management", "MBA", "MBA-S1-103", "Corporate Finance", 1, 4);
        createSubject("Management", "MBA", "MBA-S2-201", "Strategic Management", 2, 4);
        createSubject("Management", "MBA", "MBA-S2-202", "Business Analytics", 2, 4);
        createSubject("Management", "MBA", "MBA-S2-203", "Operations Strategy", 2, 3);

        // BTECH
        createSubject("Engineering", "BTECH", "BTECH-S1-101", "Engineering Mathematics I", 1, 4);
        createSubject("Engineering", "BTECH", "BTECH-S1-102", "Engineering Physics", 1, 4);
        createSubject("Engineering", "BTECH", "BTECH-S1-103", "Basic Electrical Engineering", 1, 3);
        createSubject("Engineering", "BTECH", "BTECH-S2-201", "Data Structures and Algorithms", 2, 4);
        createSubject("Engineering", "BTECH", "BTECH-S2-202", "Object Oriented Programming", 2, 4);
        createSubject("Engineering", "BTECH", "BTECH-S2-203", "Computer Organization", 2, 3);

        // MTECH
        createSubject("Engineering", "MTECH", "MTECH-S1-101", "Research Methodology", 1, 3);
        createSubject("Engineering", "MTECH", "MTECH-S1-102", "Advanced Computing Systems", 1, 4);
        createSubject("Engineering", "MTECH", "MTECH-S1-103", "High Performance Computing", 1, 4);
        createSubject("Engineering", "MTECH", "MTECH-S2-201", "Distributed Systems", 2, 4);
        createSubject("Engineering", "MTECH", "MTECH-S2-202", "AI for Engineers", 2, 4);
        createSubject("Engineering", "MTECH", "MTECH-S2-203", "Seminar and Review", 2, 2);

        // BHM
        createSubject("Hospitality", "BHM", "BHM-S1-101", "Front Office Operations", 1, 3);
        createSubject("Hospitality", "BHM", "BHM-S1-102", "Food Production Basics", 1, 4);
        createSubject("Hospitality", "BHM", "BHM-S1-103", "Hospitality Communication", 1, 3);
        createSubject("Hospitality", "BHM", "BHM-S2-201", "Housekeeping Management", 2, 3);
        createSubject("Hospitality", "BHM", "BHM-S2-202", "Food and Beverage Service", 2, 4);
        createSubject("Hospitality", "BHM", "BHM-S2-203", "Hospitality Marketing", 2, 3);

        System.out.println("Reset and created sample subjects with full details.");
    }

    private void seedDefaultFeeStructure() {
        if (feeStructureRepository.findFirstByActiveTrueOrderByEffectiveFromDesc().isPresent()) {
            return;
        }
        FeeStructure feeStructure = new FeeStructure();
        feeStructure.setName("Dummy Course Fee Aligned Plan");
        // Keep fee-structure add-ons zero so invoice totals follow dummy course fee values.
        feeStructure.setCostPerCredit(BigDecimal.ZERO);
        feeStructure.setLabFee(BigDecimal.ZERO);
        feeStructure.setDifferentialFee(BigDecimal.ZERO);
        feeStructure.setLatePenalty(BigDecimal.ZERO);
        feeStructure.setEffectiveFrom(LocalDate.now());
        feeStructure.setActive(true);
        feeStructureRepository.save(feeStructure);
        System.out.println("Created default fee structure: Dummy Course Fee Aligned Plan");
    }

    private void ensureDemoFaculty() {
        String facultyEmail = "faculty@college.edu";
        User faculty = userRepository.findByEmail(facultyEmail).orElseGet(User::new);
        faculty.setEmail(facultyEmail);
        faculty.setMobileNumber("9000000003");
        faculty.setFullName("Faculty Demo (DEMO)");
        faculty.setDepartment("Computer Applications");
        faculty.setPassword(passwordEncoder.encode("Faculty123!"));
        faculty.getRoles().clear();
        faculty.getRoles().add("AUTHORITY_FACULTY");
        faculty.setEmailVerified(true);
        faculty.setMobileVerified(true);
        faculty.setActive(true);
        userRepository.save(faculty);
        System.out.println("Ensured demo faculty account: " + facultyEmail + " (password: Faculty123!)");
    }

    private void createCourse(String code, String name, String department, String programName, int batchYear, int capacity, int remainingSeats, int credits, int fee,
                              String programLevel, int durationSemesters, String requiredQualification) {
        Course c = courseRepository.findByCode(code).orElseGet(Course::new);
        c.setCode(code);
        c.setName(name);
        c.setDepartment(department);
        c.setProgramName(programName);
        c.setBatchYear(batchYear);
        c.setCredits(credits);
        c.setFee(fee);
        c.setCapacity(capacity);
        c.setRemainingSeats(remainingSeats);
        c.setProgramLevel(programLevel);
        c.setLevel(programLevel);
        c.setDurationSemesters(durationSemesters);
        c.setRequiredQualification(requiredQualification);
        // 1. Get or save data in the database
        courseRepository.save(c);
    }

    private void createSubject(String department, String programName, String code, String name, int semester, int credits) {
        Subject subject = subjectRepository
                .findByDepartmentIgnoreCaseAndSubjectCodeIgnoreCase(department, code)
                .orElseGet(Subject::new);
        subject.setDepartment(department);
        subject.setProgramName(programName);
        subject.setSubjectCode(code);
        subject.setSubjectName(name);
        subject.setSemester(semester);
        subject.setCredits(credits);
        subjectRepository.save(subject);
    }

    private void backfillTeachingSchemaSubjects() {
        int totalSaved = 0;
        for (TeachingSchema schema : teachingSchemaRepository.findAll()) {
            if (schema == null || schema.getId() == null) {
                continue;
            }
            String rawPath = schema.getFilePath();
            if (rawPath == null || rawPath.isBlank()) {
                continue;
            }
            Path path = resolveSchemaPath(rawPath);
            if (!Files.exists(path)) {
                System.out.println("Teaching schema file not found for schema id " + schema.getId() + ": " + rawPath);
                continue;
            }
            try {
                int saved = teachingSchemaSubjectIngestionService.ingestSubjects(schema, path, schema.getFileName());
                totalSaved += saved;
                if (saved > 0) {
                    System.out.println("Schema sync: added/updated " + saved + " subjects for "
                            + schema.getProgramName() + " (schema id " + schema.getId() + ").");
                }
            } catch (Throwable ex) {
                System.out.println("Schema sync failed for schema id " + schema.getId() + ": " + ex.getMessage());
            }
        }
        System.out.println("Teaching schema backfill completed. Total subjects added/updated: " + totalSaved);
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
        Path uploadsFallback = Paths.get(teachingSchemaUploadDir)
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
