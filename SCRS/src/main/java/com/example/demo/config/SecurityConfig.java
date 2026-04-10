/*
 * File: src/main/java/com/example/demo/config/SecurityConfig.java
 * Role: Config
 * MVC Fit: Spring configuration and bean wiring.
 * Connects To: Bootstraps app behavior
 */

package com.example.demo.config;
import com.example.demo.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.servlet.http.HttpServletRequest;
// Class Summary: Config class that defines Spring configuration and beans.
// @Configuration marks this class as a source of Spring bean definitions.
@Configuration
// @EnableWebSecurity turns on Spring Security configuration.
@EnableWebSecurity
// @EnableMethodSecurity enables method-level security annotations.
@EnableMethodSecurity
public class SecurityConfig {
// Field: stores userDetailsService for this class.
    private final CustomUserDetailsService userDetailsService;
    private final com.example.demo.security.PreLoginRoleValidationFilter preLoginRoleValidationFilter;
// Constructor: Spring injects dependencies here.
    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          com.example.demo.security.PreLoginRoleValidationFilter preLoginRoleValidationFilter) {
        this.userDetailsService = userDetailsService;
        this.preLoginRoleValidationFilter = preLoginRoleValidationFilter;
    }
// @Bean tells Spring to manage the returned object as a bean.
    @Bean
// Configuration method: defines how Spring should create/manage a bean.
    public PasswordEncoder passwordEncoder() {
        // 1. Send the result back to the screen
        return new BCryptPasswordEncoder();
    }
// @Bean tells Spring to manage the returned object as a bean.
    @Bean
// Configuration method: defines how Spring should create/manage a bean.
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        // 1. Send the result back to the screen
        return provider;
    }
// @Bean tells Spring to manage the returned object as a bean.
    @Bean
// Configuration method: defines how Spring should create/manage a bean.
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // 1. Send the result back to the screen
        return config.getAuthenticationManager();
    }
// @Bean tells Spring to manage the returned object as a bean.
    @Bean
// Configuration method: defines how Spring should create/manage a bean.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/", "/authenroll").hasAuthority("ROLE_STUDENT")
                .requestMatchers("/payments/**", "/payment/**").hasAuthority("ROLE_STUDENT")
                .requestMatchers("/admin/**").hasAnyAuthority("ROLE_AUTHORITY_ADMIN", "ROLE_AUTHORITY_SUPER_ADMIN")
                .requestMatchers("/director/**").hasAuthority("ROLE_AUTHORITY_DIRECTOR")
                .requestMatchers("/staff/**").hasAuthority("ROLE_AUTHORITY_STAFF")
                .requestMatchers("/faculty/**").hasAuthority("ROLE_AUTHORITY_FACULTY")
                .requestMatchers("/dashboard/authority").hasAnyAuthority("ROLE_AUTHORITY_ADMIN", "ROLE_AUTHORITY_DIRECTOR", "ROLE_AUTHORITY_STAFF", "ROLE_AUTHORITY_FACULTY")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .successHandler(new com.example.demo.security.CustomAuthenticationSuccessHandler())
                .failureUrl("/auth/login?error")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            )
            .addFilterBefore(preLoginRoleValidationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
            .logout(logout -> logout
                .logoutRequestMatcher(this::logoutRequestMatcher)
                .logoutSuccessUrl("/auth/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );
        // 1. Send the result back to the screen
        return http.build();
    }
// Configuration method: defines how Spring should create/manage a bean.
    private boolean logoutRequestMatcher(HttpServletRequest request) {
        // 1. Send the result back to the screen
        return "POST".equalsIgnoreCase(request.getMethod())
                && "/auth/logout".equals(request.getRequestURI());
    }
}
