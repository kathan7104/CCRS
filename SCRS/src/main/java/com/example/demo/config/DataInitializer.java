// Package declaration: groups related classes in a namespace.
package com.example.demo.config;

// Import statement: brings a class into scope by name.
import com.example.demo.entity.Course;
// Import statement: brings a class into scope by name.
import com.example.demo.entity.User;
// Import statement: brings a class into scope by name.
import com.example.demo.repository.CourseRepository;
// Import statement: brings a class into scope by name.
import com.example.demo.repository.EnrollmentRepository;
// Import statement: brings a class into scope by name.
import com.example.demo.repository.UserRepository;
// Import statement: brings a class into scope by name.
import org.springframework.beans.factory.annotation.Value;
// Import statement: brings a class into scope by name.
import org.springframework.boot.CommandLineRunner;
// Import statement: brings a class into scope by name.
import org.springframework.context.annotation.Configuration;
// Import statement: brings a class into scope by name.
import org.springframework.security.crypto.password.PasswordEncoder;

// Comment: explains code for readers.
/**
 // Comment: explains code for readers.
 * Dev helper to create a sample authority account for testing.
 // Comment: explains code for readers.
 * Disabled by default. Enable by setting `ccrs.dev.create-authority=true` in application.properties.
 // Comment: explains code for readers.
 */
// Annotation: adds metadata used by frameworks/tools.
@Configuration
// Class declaration: defines a new type.
public class DataInitializer implements CommandLineRunner {

    // Field declaration: defines a member variable.
    private final UserRepository userRepository;
    // Field declaration: defines a member variable.
    private final CourseRepository courseRepository;
    // Field declaration: defines a member variable.
    private final EnrollmentRepository enrollmentRepository;
    // Field declaration: defines a member variable.
    private final PasswordEncoder passwordEncoder;

    // Annotation: adds metadata used by frameworks/tools.
    @Value("${ccrs.dev.create-authority:false}")
    // Field declaration: defines a member variable.
    private boolean createAuthority;

    // Opens a method/constructor/block.
    public DataInitializer(UserRepository userRepository, CourseRepository courseRepository, EnrollmentRepository enrollmentRepository, PasswordEncoder passwordEncoder) {
        // Uses current object (this) to access a field or method.
        this.userRepository = userRepository;
        // Uses current object (this) to access a field or method.
        this.courseRepository = courseRepository;
        // Uses current object (this) to access a field or method.
        this.enrollmentRepository = enrollmentRepository;
        // Uses current object (this) to access a field or method.
        this.passwordEncoder = passwordEncoder;
    // Closes the current code block.
    }

    // Annotation: adds metadata used by frameworks/tools.
    @Override
    // Opens a method/constructor/block.
    public void run(String... args) throws Exception {
        // Comment: explains code for readers.
        // Reset and create sample courses
        // Statement: resetAndCreateSampleCourses();
        resetAndCreateSampleCourses();

        // Conditional: runs this block only if the condition is true.
        if (!createAuthority) return;

        // Comment: explains code for readers.
        // Director (static test account)
        // Statement: String directorEmail = "director@college.edu";
        String directorEmail = "director@college.edu";
        // Opens a method/constructor/block.
        if (userRepository.findByEmail(directorEmail).isEmpty()) {
            // Statement: User u = new User();
            User u = new User();
            // Statement: u.setEmail(directorEmail);
            u.setEmail(directorEmail);
            // Statement: u.setMobileNumber("9000000001");
            u.setMobileNumber("9000000001");
            // Statement: u.setFullName("College Director (DEMO)");
            u.setFullName("College Director (DEMO)");
            // Statement: u.setPassword(passwordEncoder.encode("Director123!"));
            u.setPassword(passwordEncoder.encode("Director123!"));
            // Statement: u.getRoles().add("AUTHORITY_DIRECTOR");
            u.getRoles().add("AUTHORITY_DIRECTOR");
            // Statement: u.setEmailVerified(true);
            u.setEmailVerified(true);
            // Statement: u.setMobileVerified(true);
            u.setMobileVerified(true);
            // Statement: userRepository.save(u);
            userRepository.save(u);
            // Statement: System.out.println("Created demo director account: " + directorEmail + " (pas...
            System.out.println("Created demo director account: " + directorEmail + " (password: Director123!)");
        // Closes the current code block.
        }

        // Comment: explains code for readers.
        // Admin staff (managed by director)
        // Statement: String adminEmail = "admin@college.edu";
        String adminEmail = "admin@college.edu";
        // Opens a method/constructor/block.
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            // Statement: User a = new User();
            User a = new User();
            // Statement: a.setEmail(adminEmail);
            a.setEmail(adminEmail);
            // Statement: a.setMobileNumber("9000000002");
            a.setMobileNumber("9000000002");
            // Statement: a.setFullName("College Admin (DEMO)");
            a.setFullName("College Admin (DEMO)");
            // Statement: a.setPassword(passwordEncoder.encode("Admin123!"));
            a.setPassword(passwordEncoder.encode("Admin123!"));
            // Statement: a.getRoles().add("AUTHORITY_ADMIN");
            a.getRoles().add("AUTHORITY_ADMIN");
            // Statement: a.setEmailVerified(true);
            a.setEmailVerified(true);
            // Statement: a.setMobileVerified(true);
            a.setMobileVerified(true);
            // Statement: userRepository.save(a);
            userRepository.save(a);
            // Statement: System.out.println("Created demo admin account: " + adminEmail + " (password:...
            System.out.println("Created demo admin account: " + adminEmail + " (password: Admin123!)");
        // Closes the current code block.
        }

        // Comment: explains code for readers.
        // Faculty / Teachers (managed by admin)
        // Statement: String facultyEmail = "faculty@college.edu";
        String facultyEmail = "faculty@college.edu";
        // Opens a method/constructor/block.
        if (userRepository.findByEmail(facultyEmail).isEmpty()) {
            // Statement: User f = new User();
            User f = new User();
            // Statement: f.setEmail(facultyEmail);
            f.setEmail(facultyEmail);
            // Statement: f.setMobileNumber("9000000003");
            f.setMobileNumber("9000000003");
            // Statement: f.setFullName("Faculty Demo (DEMO)");
            f.setFullName("Faculty Demo (DEMO)");
            // Statement: f.setPassword(passwordEncoder.encode("Faculty123!"));
            f.setPassword(passwordEncoder.encode("Faculty123!"));
            // Statement: f.getRoles().add("AUTHORITY_FACULTY");
            f.getRoles().add("AUTHORITY_FACULTY");
            // Statement: f.setEmailVerified(true);
            f.setEmailVerified(true);
            // Statement: f.setMobileVerified(true);
            f.setMobileVerified(true);
            // Statement: userRepository.save(f);
            userRepository.save(f);
            // Statement: System.out.println("Created demo faculty account: " + facultyEmail + " (passw...
            System.out.println("Created demo faculty account: " + facultyEmail + " (password: Faculty123!)");
        // Closes the current code block.
        }
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    private void resetAndCreateSampleCourses() {
        // Statement: enrollmentRepository.deleteAll();
        enrollmentRepository.deleteAll();
        // Statement: courseRepository.deleteAll();
        courseRepository.deleteAll();

        // Statement: createCourse("BBA", "Bachelor of Business Administration", "Management", 120,...
        createCourse("BBA", "Bachelor of Business Administration", "Management", 120, 120, 3, 120000, "UG", 3, "12th pass (Commerce/Any stream) with minimum 50%");
        // Statement: createCourse("BCA", "Bachelor of Computer Applications", "Computer Applicatio...
        createCourse("BCA", "Bachelor of Computer Applications", "Computer Applications", 120, 120, 3, 130000, "UG", 3, "12th pass with Mathematics/Computer Science (50%+)");
        // Statement: createCourse("BHM", "Bachelor of Hotel Management", "Hospitality", 90, 90, 3,...
        createCourse("BHM", "Bachelor of Hotel Management", "Hospitality", 90, 90, 3, 110000, "UG", 3, "12th pass (any stream) with minimum 45%");
        // Statement: createCourse("BTECH-CSE", "B.Tech in Computer Science Engineering", "Engineer...
        createCourse("BTECH-CSE", "B.Tech in Computer Science Engineering", "Engineering", 180, 180, 4, 220000, "UG", 4, "12th pass (PCM) with minimum 60%");
        // Statement: createCourse("BTECH-IT", "B.Tech in Information Technology", "Engineering", 1...
        createCourse("BTECH-IT", "B.Tech in Information Technology", "Engineering", 180, 180, 4, 210000, "UG", 4, "12th pass (PCM) with minimum 60%");
        // Statement: createCourse("MBA", "Master of Business Administration", "Management", 120, 1...
        createCourse("MBA", "Master of Business Administration", "Management", 120, 120, 3, 250000, "PG", 2, "Graduation in any discipline with minimum 50%");
        // Statement: createCourse("MCA", "Master of Computer Applications", "Computer Applications...
        createCourse("MCA", "Master of Computer Applications", "Computer Applications", 120, 120, 3, 180000, "PG", 2, "Graduation with Mathematics/CS/IT (50%+)");
        // Statement: createCourse("MTECH-CSE", "M.Tech in Computer Science Engineering", "Engineer...
        createCourse("MTECH-CSE", "M.Tech in Computer Science Engineering", "Engineering", 60, 60, 3, 260000, "PG", 2, "B.Tech/BE in CSE/IT (60%+)");

        // Statement: System.out.println("Reset and created sample courses.");
        System.out.println("Reset and created sample courses.");
    // Closes the current code block.
    }

    // Field declaration: defines a member variable.
    private void createCourse(String code, String name, String department, int capacity, int remainingSeats, int credits, int fee,
                              // Opens a new code block.
                              String programLevel, int durationYears, String requiredQualification) {
        // Statement: Course c = new Course();
        Course c = new Course();
        // Statement: c.setCode(code);
        c.setCode(code);
        // Statement: c.setName(name);
        c.setName(name);
        // Statement: c.setDepartment(department);
        c.setDepartment(department);
        // Statement: c.setCredits(credits);
        c.setCredits(credits);
        // Statement: c.setFee(fee);
        c.setFee(fee);
        // Statement: c.setCapacity(capacity);
        c.setCapacity(capacity);
        // Statement: c.setRemainingSeats(remainingSeats);
        c.setRemainingSeats(remainingSeats);
        // Statement: c.setProgramLevel(programLevel);
        c.setProgramLevel(programLevel);
        // Statement: c.setLevel(programLevel);
        c.setLevel(programLevel);
        // Statement: c.setDurationYears(durationYears);
        c.setDurationYears(durationYears);
        // Statement: c.setRequiredQualification(requiredQualification);
        c.setRequiredQualification(requiredQualification);
        // Statement: courseRepository.save(c);
        courseRepository.save(c);
    // Closes the current code block.
    }
// Closes the current code block.
}
