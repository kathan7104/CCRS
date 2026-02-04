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

/**
 * Dev helper to create a sample authority account for testing.
 * Disabled by default. Enable by setting `ccrs.dev.create-authority=true` in application.properties.
 */
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
        // Reset and create sample courses
        resetAndCreateSampleCourses();

        if (!createAuthority) return;

        // Director (static test account)
        String directorEmail = "director@college.edu";
        if (userRepository.findByEmail(directorEmail).isEmpty()) {
            User u = new User();
            u.setEmail(directorEmail);
            u.setMobileNumber("9000000001");
            u.setFullName("College Director (DEMO)");
            u.setPassword(passwordEncoder.encode("Director123!"));
            u.getRoles().add("AUTHORITY_DIRECTOR");
            u.setEmailVerified(true);
            u.setMobileVerified(true);
            userRepository.save(u);
            System.out.println("Created demo director account: " + directorEmail + " (password: Director123!)");
        }

        // Admin staff (managed by director)
        String adminEmail = "admin@college.edu";
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User a = new User();
            a.setEmail(adminEmail);
            a.setMobileNumber("9000000002");
            a.setFullName("College Admin (DEMO)");
            a.setPassword(passwordEncoder.encode("Admin123!"));
            a.getRoles().add("AUTHORITY_ADMIN");
            a.setEmailVerified(true);
            a.setMobileVerified(true);
            userRepository.save(a);
            System.out.println("Created demo admin account: " + adminEmail + " (password: Admin123!)");
        }

        // Faculty / Teachers (managed by admin)
        String facultyEmail = "faculty@college.edu";
        if (userRepository.findByEmail(facultyEmail).isEmpty()) {
            User f = new User();
            f.setEmail(facultyEmail);
            f.setMobileNumber("9000000003");
            f.setFullName("Faculty Demo (DEMO)");
            f.setPassword(passwordEncoder.encode("Faculty123!"));
            f.getRoles().add("AUTHORITY_FACULTY");
            f.setEmailVerified(true);
            f.setMobileVerified(true);
            userRepository.save(f);
            System.out.println("Created demo faculty account: " + facultyEmail + " (password: Faculty123!)");
        }
    }

    private void resetAndCreateSampleCourses() {
        enrollmentRepository.deleteAll();
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
        courseRepository.save(c);
    }
}
