package com.example.demo.security;
import com.example.demo.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.stream.Collectors;


/**
 * Security component for Custom User.
 */
public class CustomUserDetails implements UserDetails {
    private final User user;
    public CustomUserDetails(User user) {
        this.user = user;
    }
/**
 * Security component for Custom User.
 */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
/**
 * Security component for Custom User.
 */
    @Override
    public String getPassword() {
        return user.getPassword();
    }
/**
 * Security component for Custom User.
 */
    @Override
    public String getUsername() {
        return user.getEmail();
    }
/**
 * Security component for Custom User.
 */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
/**
 * Security component for Custom User.
 */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
/**
 * Security component for Custom User.
 */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
/**
 * Security component for Custom User.
 */
    @Override
    public boolean isEnabled() {
        return user.isActive();
    }
/**
 * Security component for Custom User.
 */
    public User getUser() {
        return user;
    }
}
