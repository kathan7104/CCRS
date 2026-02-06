package com.example.demo.config;
import com.example.demo.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.servlet.http.HttpServletRequest;


/**
 * Configuration for Security.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final com.example.demo.security.PreLoginRoleValidationFilter preLoginRoleValidationFilter;
/**
 * Configuration for Security.
 */
    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          com.example.demo.security.PreLoginRoleValidationFilter preLoginRoleValidationFilter) {
        this.userDetailsService = userDetailsService;
        this.preLoginRoleValidationFilter = preLoginRoleValidationFilter;
    }
/**
 * Configuration for Security.
 */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
/**
 * Configuration for Security.
 */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
/**
 * Configuration for Security.
 */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
/**
 * Configuration for Security.
 */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/authenroll").hasAuthority("ROLE_STUDENT")
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
        return http.build();
    }
    private boolean logoutRequestMatcher(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && "/auth/logout".equals(request.getRequestURI());
    }
}
