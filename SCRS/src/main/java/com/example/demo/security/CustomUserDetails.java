package com.example.demo.security;

import com.example.demo.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Wrapper around `User` to provide Spring Security `UserDetails`.
 * Converts user roles into granted authorities and exposes user account data.
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // Convert user roles to Spring Security authorities.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    // Return the encrypted password for authentication checks.
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // The username used by the app is the user's email address.
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Enabled if the user record is active.
    @Override
    public boolean isEnabled() {
        return user.isActive();
    }

    // Provide access to the underlying User entity when needed.
    public User getUser() {
        return user;
    }
}
