// Package declaration: groups related classes in a namespace.
package com.example.demo.security;

// Import statement: brings a class into scope by name.
import com.example.demo.entity.User;
// Import statement: brings a class into scope by name.
import org.springframework.security.core.GrantedAuthority;
// Import statement: brings a class into scope by name.
import org.springframework.security.core.authority.SimpleGrantedAuthority;
// Import statement: brings a class into scope by name.
import org.springframework.security.core.userdetails.UserDetails;

// Import statement: brings a class into scope by name.
import java.util.Collection;
// Import statement: brings a class into scope by name.
import java.util.stream.Collectors;

// Comment: explains code for readers.
/**
 // Comment: explains code for readers.
 * Wrapper around `User` to provide Spring Security `UserDetails`.
 // Comment: explains code for readers.
 * Converts user roles into granted authorities and exposes user account data.
 // Comment: explains code for readers.
 */
// Class declaration: defines a new type.
public class CustomUserDetails implements UserDetails {

    // Field declaration: defines a member variable.
    private final User user;

    // Opens a method/constructor/block.
    public CustomUserDetails(User user) {
        // Uses current object (this) to access a field or method.
        this.user = user;
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Convert user roles to Spring Security authorities.
    // Annotation: adds metadata used by frameworks/tools.
    @Override
    // Opens a method/constructor/block.
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return: sends a value back to the caller.
        return user.getRoles().stream()
                // Statement: .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                // Statement: .collect(Collectors.toList());
                .collect(Collectors.toList());
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Return the encrypted password for authentication checks.
    // Annotation: adds metadata used by frameworks/tools.
    @Override
    // Opens a method/constructor/block.
    public String getPassword() {
        // Return: sends a value back to the caller.
        return user.getPassword();
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // The username used by the app is the user's email address.
    // Annotation: adds metadata used by frameworks/tools.
    @Override
    // Opens a method/constructor/block.
    public String getUsername() {
        // Return: sends a value back to the caller.
        return user.getEmail();
    // Closes the current code block.
    }

    // Annotation: adds metadata used by frameworks/tools.
    @Override
    // Opens a method/constructor/block.
    public boolean isAccountNonExpired() {
        // Return: sends a value back to the caller.
        return true;
    // Closes the current code block.
    }

    // Annotation: adds metadata used by frameworks/tools.
    @Override
    // Opens a method/constructor/block.
    public boolean isAccountNonLocked() {
        // Return: sends a value back to the caller.
        return true;
    // Closes the current code block.
    }

    // Annotation: adds metadata used by frameworks/tools.
    @Override
    // Opens a method/constructor/block.
    public boolean isCredentialsNonExpired() {
        // Return: sends a value back to the caller.
        return true;
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Enabled if the user record is active.
    // Annotation: adds metadata used by frameworks/tools.
    @Override
    // Opens a method/constructor/block.
    public boolean isEnabled() {
        // Return: sends a value back to the caller.
        return user.isActive();
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Provide access to the underlying User entity when needed.
    // Opens a method/constructor/block.
    public User getUser() {
        // Return: sends a value back to the caller.
        return user;
    // Closes the current code block.
    }
// Closes the current code block.
}
