package com.example.demo.config;
import com.example.demo.entity.Course;
import com.example.demo.entity.User;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${ccrs.dev.create-authority:false}")
    private boolean createAuthority;
    public DataInitializer(UserRepository userRepository, CourseRepository courseRepository, EnrollmentRepository enrollmentRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void run(String... args) throws Exception {
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
    private void resetAndCreateSampleCourses() {
        // 1. Get or save data in the database
        enrollmentRepository.deleteAll();
        // 2. Get or save data in the database
        courseRepository.deleteAll();
        createCourse("BBA", "Bachelor of Business Administration", "Management", 120, 120, 3, 120000, "UG", 3, "12th pass (Commerce/Any stream) with minimum 50%");
        createCourse("BCA", "Bachelor of Computer Applications", "Computer Applications", 120, 120, 3, 130000, "UG", 3, "12th pass with Mathematics/Computer Science (50%+)");
        createCourse("BHM", "Bachelor of Hotel Management", "Hospitality", 90, 90, 3, 110000, "UG", 3, "12th pass (any stream) with minimum 45%");
        createCourse("BTECH-CSE", "B.Tech in Computer Science Engineering", "Engineering", 180, 180, 4, 220000, "UG", 4, "12th pass (PCM) with minimum 60%");
        createCourse("BTECH-IT", "B.Tech in Information Technology", "Engineering", 180, 180, 4, 210000, "UG", 4, "12th pass (PCM) with minimum 60%");
        createCourse("MBA", "Master of Business Administration", "Management", 120, 120, 3, 250000, "PG", 2, "Graduation in any discipline with minimum 50%");
        createCourse("MCA", "Master of Computer Applications", "Computer Applications", 120, 120, 3, 180000, "PG", 2, "Graduation with Mathematics/CS/IT (50%+)");
        createCourse("MTECH-CSE", "M.Tech in Computer Science Engineering", "Engineering", 60, 60, 3, 260000, "PG", 2, "B.Tech/BE in CSE/IT (60%+)");
        System.out.println("Reset and created sample courses.");
    }
    private void createCourse(String code, String name, String department, int capacity, int remainingSeats, int credits, int fee,
                              String programLevel, int durationYears, String requiredQualification) {
        Course c = new Course();
        c.setCode(code);
        c.setName(name);
        c.setDepartment(department);
        c.setCredits(credits);
        c.setFee(fee);
        c.setCapacity(capacity);
        c.setRemainingSeats(remainingSeats);
        c.setProgramLevel(programLevel);
        c.setLevel(programLevel);
        c.setDurationYears(durationYears);
        c.setRequiredQualification(requiredQualification);
        // 1. Get or save data in the database
        courseRepository.save(c);
    }
}
