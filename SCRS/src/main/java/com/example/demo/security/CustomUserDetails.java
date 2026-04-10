/*
 * File: src/main/java/com/example/demo/security/CustomUserDetails.java
 * Role: Security
 * MVC Fit: Spring Security customization and filters.
 * Connects To: Protects requests and authentication
 */

package com.example.demo.security;
import com.example.demo.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.stream.Collectors;
// Class Summary: Security class that customizes Spring Security behavior.
public class CustomUserDetails implements UserDetails {
// Field: stores user for this class.
    private final User user;
// Constructor: Spring injects dependencies here.
    public CustomUserDetails(User user) {
        this.user = user;
    }
    @Override
// Method: performs a focused unit of work in this class.
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 1. Send the result back to the screen
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
    @Override
// Method: performs a focused unit of work in this class.
    public String getPassword() {
        // 1. Send the result back to the screen
        return user.getPassword();
    }
    @Override
// Method: performs a focused unit of work in this class.
    public String getUsername() {
        // 1. Send the result back to the screen
        return user.getEmail();
    }
    @Override
// Method: performs a focused unit of work in this class.
    public boolean isAccountNonExpired() {
        // 1. Send the result back to the screen
        return true;
    }
    @Override
// Method: performs a focused unit of work in this class.
    public boolean isAccountNonLocked() {
        // 1. Send the result back to the screen
        return true;
    }
    @Override
// Method: performs a focused unit of work in this class.
    public boolean isCredentialsNonExpired() {
        // 1. Send the result back to the screen
        return true;
    }
    @Override
// Method: performs a focused unit of work in this class.
    public boolean isEnabled() {
        // 1. Send the result back to the screen
        return user.isActive();
    }
// Method: performs a focused unit of work in this class.
    public User getUser() {
        // 1. Send the result back to the screen
        return user;
    }
}
