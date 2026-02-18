package com.example.demo.config;
import com.example.demo.entity.Course;
import com.example.demo.entity.Department;
import com.example.demo.entity.User;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
@Configuration
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;
    @Value("${ccrs.dev.create-authority:false}")
    private boolean createAuthority;
    public DataInitializer(UserRepository userRepository,
                           CourseRepository courseRepository,
                           DepartmentRepository departmentRepository,
                           EnrollmentRepository enrollmentRepository,
                           PasswordEncoder passwordEncoder,
                           JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public void run(String... args) throws Exception {
        patchLegacyCourseSchema();
        seedDepartments();
        resetAndCreateSampleCourses();
        // 1. Check a rule -> decide what to do next
        if (!createAuthority) return;
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
        // 8. Check a rule -> decide what to do next
        if (userRepository.findByEmail(facultyEmail).isEmpty()) {
            User f = new User();
            f.setEmail(facultyEmail);
            f.setMobileNumber("9000000003");
            f.setFullName("Faculty Demo (DEMO)");
            f.setDepartment("Engineering");
            // 9. Security: hide the password before saving
            f.setPassword(passwordEncoder.encode("Faculty123!"));
            f.getRoles().add("AUTHORITY_FACULTY");
            f.setEmailVerified(true);
            f.setMobileVerified(true);
            // 10. Get or save data in the database
            userRepository.save(f);
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
        executeSql("ALTER TABLE courses ADD COLUMN IF NOT EXISTS program_name VARCHAR(100) NULL");
        executeSql("ALTER TABLE courses ADD COLUMN IF NOT EXISTS batch_year INT NULL");
        executeSql("ALTER TABLE courses ADD COLUMN IF NOT EXISTS duration_semesters INT NULL");
        executeSql("ALTER TABLE courses ADD COLUMN IF NOT EXISTS teaching_schema_id BIGINT NULL");
        executeSql("UPDATE courses SET program_name = COALESCE(NULLIF(program_name, ''), code)");
        executeSql("UPDATE courses SET batch_year = COALESCE(batch_year, YEAR(created_at), YEAR(CURDATE()))");
        executeSql("UPDATE courses SET duration_semesters = COALESCE(duration_semesters, CASE WHEN duration_years IS NOT NULL THEN duration_years * 2 ELSE 6 END)");
        executeSql("ALTER TABLE courses MODIFY COLUMN program_name VARCHAR(100) NOT NULL");
        executeSql("ALTER TABLE courses MODIFY COLUMN batch_year INT NOT NULL");
        executeSql("ALTER TABLE courses MODIFY COLUMN duration_semesters INT NOT NULL");
        // Keep old column nullable so inserts that don't mention it still work.
        executeSql("ALTER TABLE courses MODIFY COLUMN duration_years INT NULL");
        executeSql("CREATE INDEX IF NOT EXISTS idx_teaching_schema_department_program ON teaching_schemas (department, program_name)");
    }
    private void seedDepartments() {
        for (String name : DepartmentCatalog.departments()) {
            if (departmentRepository.findByNameIgnoreCase(name).isPresent()) {
                continue;
            }
            Department department = new Department();
            department.setName(name);
            department.setActive(true);
            departmentRepository.save(department);
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
        // 1. Get or save data in the database
        enrollmentRepository.deleteAll();
        // 2. Get or save data in the database
        courseRepository.deleteAll();
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
    private void createCourse(String code, String name, String department, String programName, int batchYear, int capacity, int remainingSeats, int credits, int fee,
                              String programLevel, int durationSemesters, String requiredQualification) {
        Course c = new Course();
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
}
