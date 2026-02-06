package com.example.demo.security;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * Security component for Custom User Details.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
/**
 * Security component for Custom User Details.
 */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByMobileNumber(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new CustomUserDetails(user);
    }
}
