// Package declaration: groups related classes in a namespace.
package com.example.demo.security;

// Import statement: brings a class into scope by name.
import com.example.demo.entity.User;
// Import statement: brings a class into scope by name.
import com.example.demo.repository.UserRepository;
// Import statement: brings a class into scope by name.
import org.springframework.security.core.userdetails.UserDetails;
// Import statement: brings a class into scope by name.
import org.springframework.security.core.userdetails.UserDetailsService;
// Import statement: brings a class into scope by name.
import org.springframework.security.core.userdetails.UsernameNotFoundException;
// Import statement: brings a class into scope by name.
import org.springframework.stereotype.Service;

// Comment: explains code for readers.
/**
 // Comment: explains code for readers.
 * Service used by Spring Security to load user details by username (email or mobile).
 // Comment: explains code for readers.
 */
// Annotation: adds metadata used by frameworks/tools.
@Service
// Class declaration: defines a new type.
public class CustomUserDetailsService implements UserDetailsService {

    // Field declaration: defines a member variable.
    private final UserRepository userRepository;

    // Opens a method/constructor/block.
    public CustomUserDetailsService(UserRepository userRepository) {
        // Uses current object (this) to access a field or method.
        this.userRepository = userRepository;
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Load user by email first, then try mobile number if email not found.
    // Annotation: adds metadata used by frameworks/tools.
    @Override
    // Opens a method/constructor/block.
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Statement: User user = userRepository.findByEmail(username)
        User user = userRepository.findByEmail(username)
                // Statement: .or(() -> userRepository.findByMobileNumber(username))
                .or(() -> userRepository.findByMobileNumber(username))
                // Statement: .orElseThrow(() -> new UsernameNotFoundException("User not found: " + usernam...
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        // Return: sends a value back to the caller.
        return new CustomUserDetails(user);
    // Closes the current code block.
    }
// Closes the current code block.
}
