/*
 * File: src/main/java/com/example/demo/security/CustomUserDetailsService.java
 * Role: Security
 * MVC Fit: Spring Security customization and filters.
 * Connects To: Protects requests and authentication
 */

package com.example.demo.security;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
// Class Summary: Security class that customizes Spring Security behavior.
// @Service marks the business logic layer for Spring to manage as a bean.
@Service
public class CustomUserDetailsService implements UserDetailsService {
// Field: stores userRepository for this class.
    private final UserRepository userRepository;
// Constructor: Spring injects dependencies here.
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
// Method: performs a focused unit of work in this class.
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Get or save data in the database
        User user = userRepository.findByEmail(username)
                // 2. Get or save data in the database
                .or(() -> userRepository.findByMobileNumber(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        // 3. Send the result back to the screen
        return new CustomUserDetails(user);
    }
}
