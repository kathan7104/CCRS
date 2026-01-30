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

        String email = "authority@college.edu";
        if (userRepository.findByEmail(email).isEmpty()) {
            User u = new User();
            u.setEmail(email);
            u.setMobileNumber("9999999999");
            u.setFullName("College Authority");
            u.setPassword(passwordEncoder.encode("Authority123!"));
            u.getRoles().add("AUTHORITY");
            u.setEmailVerified(true);
            u.setMobileVerified(true);
            userRepository.save(u);
            System.out.println("Created demo authority user: " + email + " (password: Authority123!)");
        }
    }
}
