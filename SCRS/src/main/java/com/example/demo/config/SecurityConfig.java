// Package declaration: groups related classes in a namespace.
package com.example.demo.config;

// Import statement: brings a class into scope by name.
import com.example.demo.security.CustomUserDetailsService;
// Import statement: brings a class into scope by name.
import org.springframework.context.annotation.Bean;
// Import statement: brings a class into scope by name.
import org.springframework.context.annotation.Configuration;
// Import statement: brings a class into scope by name.
import org.springframework.security.authentication.AuthenticationManager;
// Import statement: brings a class into scope by name.
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// Import statement: brings a class into scope by name.
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// Import statement: brings a class into scope by name.
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// Import statement: brings a class into scope by name.
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// Import statement: brings a class into scope by name.
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// Import statement: brings a class into scope by name.
import org.springframework.security.crypto.password.PasswordEncoder;
// Import statement: brings a class into scope by name.
import org.springframework.security.web.SecurityFilterChain;

// Import statement: brings a class into scope by name.
import jakarta.servlet.http.HttpServletRequest;

// Comment: explains code for readers.
/**
 // Comment: explains code for readers.
 * Security configuration for the application.
 // Comment: explains code for readers.
 * Defines password encoder, authentication provider and HTTP security rules.
 // Comment: explains code for readers.
 */
// Annotation: adds metadata used by frameworks/tools.
@Configuration
// Annotation: adds metadata used by frameworks/tools.
@EnableWebSecurity
// Class declaration: defines a new type.
public class SecurityConfig {

    // Field declaration: defines a member variable.
    private final CustomUserDetailsService userDetailsService;
    // Field declaration: defines a member variable.
    private final com.example.demo.security.PreLoginRoleValidationFilter preLoginRoleValidationFilter;

    // Field declaration: defines a member variable.
    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          // Opens a new code block.
                          com.example.demo.security.PreLoginRoleValidationFilter preLoginRoleValidationFilter) {
        // Uses current object (this) to access a field or method.
        this.userDetailsService = userDetailsService;
        // Uses current object (this) to access a field or method.
        this.preLoginRoleValidationFilter = preLoginRoleValidationFilter;
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // BCrypt password encoder bean used for hashing user passwords.
    // Annotation: adds metadata used by frameworks/tools.
    @Bean
    // Opens a method/constructor/block.
    public PasswordEncoder passwordEncoder() {
        // Return: sends a value back to the caller.
        return new BCryptPasswordEncoder();
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Authentication provider that uses our CustomUserDetailsService.
    // Annotation: adds metadata used by frameworks/tools.
    @Bean
    // Opens a method/constructor/block.
    public DaoAuthenticationProvider authenticationProvider() {
        // Statement: DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetail...
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        // Statement: provider.setPasswordEncoder(passwordEncoder());
        provider.setPasswordEncoder(passwordEncoder());
        // Return: sends a value back to the caller.
        return provider;
    // Closes the current code block.
    }

    // Annotation: adds metadata used by frameworks/tools.
    @Bean
    // Opens a method/constructor/block.
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // Return: sends a value back to the caller.
        return config.getAuthenticationManager();
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Configure HTTP security: public paths, form login and logout.
    // Annotation: adds metadata used by frameworks/tools.
    @Bean
    // Opens a method/constructor/block.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Statement: http
        http
            // Statement: .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            // Statement: .authenticationProvider(authenticationProvider())
            .authenticationProvider(authenticationProvider())
            // Statement: .authorizeHttpRequests(auth -> auth
            .authorizeHttpRequests(auth -> auth
                // Statement: .requestMatchers("/", "/auth/**", "/css/**", "/js/**", "/images/**", "/error"...
                .requestMatchers("/", "/auth/**", "/css/**", "/js/**", "/images/**", "/error", "/h2-console/**").permitAll()
                // Statement: .requestMatchers("/director/**").hasAuthority("ROLE_AUTHORITY_DIRECTOR")
                .requestMatchers("/director/**").hasAuthority("ROLE_AUTHORITY_DIRECTOR")
                // Statement: .requestMatchers("/courses/*/enroll").hasAuthority("ROLE_STUDENT")
                .requestMatchers("/courses/*/enroll").hasAuthority("ROLE_STUDENT")
                // Statement: .anyRequest().authenticated()
                .anyRequest().authenticated()
            // Statement: )
            )
            // Statement: .formLogin(form -> form
            .formLogin(form -> form
                // Statement: .loginPage("/auth/login")
                .loginPage("/auth/login")
                // Statement: .loginProcessingUrl("/auth/login")
                .loginProcessingUrl("/auth/login")
                // Comment: explains code for readers.
                // use a custom success handler to redirect users based on role and selected login type
                // Statement: .successHandler(new com.example.demo.security.CustomAuthenticationSuccessHand...
                .successHandler(new com.example.demo.security.CustomAuthenticationSuccessHandler())
                // Statement: .failureUrl("/auth/login?error")
                .failureUrl("/auth/login?error")
                // Statement: .usernameParameter("username")
                .usernameParameter("username")
                // Statement: .passwordParameter("password")
                .passwordParameter("password")
                // Statement: .permitAll()
                .permitAll()
            // Statement: )
            )
            // Comment: explains code for readers.
            // add pre-login validation filter before the username/password processing filter
            // Statement: .addFilterBefore(preLoginRoleValidationFilter, org.springframework.security.w...
            .addFilterBefore(preLoginRoleValidationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
            // Statement: .logout(logout -> logout
            .logout(logout -> logout
                // Statement: .logoutRequestMatcher(this::logoutRequestMatcher)
                .logoutRequestMatcher(this::logoutRequestMatcher)
                // Statement: .logoutSuccessUrl("/auth/login?logout")
                .logoutSuccessUrl("/auth/login?logout")
                // Statement: .invalidateHttpSession(true)
                .invalidateHttpSession(true)
                // Statement: .deleteCookies("JSESSIONID")
                .deleteCookies("JSESSIONID")
                // Statement: .permitAll()
                .permitAll()
            // Statement: );
            );

        // Return: sends a value back to the caller.
        return http.build();
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Custom matcher to ensure logout only responds to POST /auth/logout
    // Opens a method/constructor/block.
    private boolean logoutRequestMatcher(HttpServletRequest request) {
        // Return: sends a value back to the caller.
        return "POST".equalsIgnoreCase(request.getMethod())
                // Statement: && "/auth/logout".equals(request.getRequestURI());
                && "/auth/logout".equals(request.getRequestURI());
    // Closes the current code block.
    }
// Closes the current code block.
}
