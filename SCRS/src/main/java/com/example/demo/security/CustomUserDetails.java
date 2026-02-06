package com.example.demo.security;
import com.example.demo.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.stream.Collectors;
public class CustomUserDetails implements UserDetails {
    private final User user;
    public CustomUserDetails(User user) {
        this.user = user;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 1. Send the result back to the screen
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
    @Override
    public String getPassword() {
        // 1. Send the result back to the screen
        return user.getPassword();
    }
    @Override
    public String getUsername() {
        // 1. Send the result back to the screen
        return user.getEmail();
    }
    @Override
    public boolean isAccountNonExpired() {
        // 1. Send the result back to the screen
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        // 1. Send the result back to the screen
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        // 1. Send the result back to the screen
        return true;
    }
    @Override
    public boolean isEnabled() {
        // 1. Send the result back to the screen
        return user.isActive();
    }
    public User getUser() {
        // 1. Send the result back to the screen
        return user;
    }
}
