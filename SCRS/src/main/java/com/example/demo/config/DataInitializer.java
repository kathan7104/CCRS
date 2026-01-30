package com.example.demo.config;

import com.example.demo.entity.User;
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
    private final PasswordEncoder passwordEncoder;

    @Value("${ccrs.dev.create-authority:false}")
    private boolean createAuthority;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
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
}
