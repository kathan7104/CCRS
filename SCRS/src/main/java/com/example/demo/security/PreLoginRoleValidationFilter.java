// Package declaration: groups related classes in a namespace.
package com.example.demo.security;

// Import statement: brings a class into scope by name.
import com.example.demo.entity.User;
// Import statement: brings a class into scope by name.
import com.example.demo.repository.UserRepository;
// Import statement: brings a class into scope by name.
import org.springframework.http.HttpMethod;
// Import statement: brings a class into scope by name.
import org.springframework.stereotype.Component;
// Import statement: brings a class into scope by name.
import org.springframework.util.StringUtils;
// Import statement: brings a class into scope by name.
import org.springframework.web.filter.OncePerRequestFilter;

// Import statement: brings a class into scope by name.
import jakarta.servlet.FilterChain;
// Import statement: brings a class into scope by name.
import jakarta.servlet.ServletException;
// Import statement: brings a class into scope by name.
import jakarta.servlet.http.HttpServletRequest;
// Import statement: brings a class into scope by name.
import jakarta.servlet.http.HttpServletResponse;
// Import statement: brings a class into scope by name.
import java.io.IOException;
// Import statement: brings a class into scope by name.
import java.util.Optional;

// Comment: explains code for readers.
/**
 // Comment: explains code for readers.
 * Pre-authentication filter that validates the selected loginType (STUDENT/AUTHORITY)
 // Comment: explains code for readers.
 * against the user's roles before authentication proceeds. If a mismatch is detected,
 // Comment: explains code for readers.
 * the filter redirects back to the login page and prevents authentication from happening.
 // Comment: explains code for readers.
 */
// Annotation: adds metadata used by frameworks/tools.
@Component
// Class declaration: defines a new type.
public class PreLoginRoleValidationFilter extends OncePerRequestFilter {

    // Method or constructor declaration.
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PreLoginRoleValidationFilter.class);

    // Field declaration: defines a member variable.
    private final UserRepository userRepository;

    // Opens a method/constructor/block.
    public PreLoginRoleValidationFilter(UserRepository userRepository) {
        // Uses current object (this) to access a field or method.
        this.userRepository = userRepository;
    // Closes the current code block.
    }

    // Annotation: adds metadata used by frameworks/tools.
    @Override
    // Opens a method/constructor/block.
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Comment: explains code for readers.
        // only act on form login POST to /auth/login
        // Opens a method/constructor/block.
        if (!HttpMethod.POST.name().equalsIgnoreCase(request.getMethod()) || !"/auth/login".equals(request.getServletPath())) {
            // Statement: filterChain.doFilter(request, response);
            filterChain.doFilter(request, response);
            // Return: exits the method without a value.
            return;
        // Closes the current code block.
        }

        // Statement: String loginType = request.getParameter("loginType");
        String loginType = request.getParameter("loginType");
        // Opens a method/constructor/block.
        if (!StringUtils.hasText(loginType)) {
            // Statement: loginType = "STUDENT"; // default
            loginType = "STUDENT"; // default
        // Closes the current code block.
        }
        // Statement: String username = request.getParameter("username");
        String username = request.getParameter("username");
        // Opens a method/constructor/block.
        if (!StringUtils.hasText(username)) {
            // Comment: explains code for readers.
            // let authentication handle missing username
            // Statement: filterChain.doFilter(request, response);
            filterChain.doFilter(request, response);
            // Return: exits the method without a value.
            return;
        // Closes the current code block.
        }

        // Comment: explains code for readers.
        // find by email or mobile (email first)
        // Statement: Optional<User> userOpt = userRepository.findByEmail(username)
        Optional<User> userOpt = userRepository.findByEmail(username)
                // Statement: .or(() -> userRepository.findByMobileNumber(username));
                .or(() -> userRepository.findByMobileNumber(username));

        // Opens a method/constructor/block.
        if (userOpt.isPresent()) {
            // Statement: User user = userOpt.get();
            User user = userOpt.get();
            // Statement: boolean hasStudent = user.getRoles().stream().anyMatch(r -> r.equalsIgnoreCas...
            boolean hasStudent = user.getRoles().stream().anyMatch(r -> r.equalsIgnoreCase("STUDENT") || r.equalsIgnoreCase("ROLE_STUDENT"));
            // Statement: boolean hasAuthority = user.getRoles().stream().anyMatch(r -> r.toUpperCase()...
            boolean hasAuthority = user.getRoles().stream().anyMatch(r -> r.toUpperCase().startsWith("AUTHORITY") || r.toUpperCase().startsWith("ROLE_AUTHORITY"));

            // Opens a method/constructor/block.
            if ("STUDENT".equalsIgnoreCase(loginType) && !hasStudent) {
                // Statement: log.info("Blocking login attempt for username={} as loginType=STUDENT but use...
                log.info("Blocking login attempt for username={} as loginType=STUDENT but user lacks STUDENT role", username);
                // Statement: response.sendRedirect("/auth/login?error&type=student&m=wrongRole");
                response.sendRedirect("/auth/login?error&type=student&m=wrongRole");
                // Return: exits the method without a value.
                return;
            // Closes the current code block.
            }
            // Opens a method/constructor/block.
            if ("AUTHORITY".equalsIgnoreCase(loginType) && !hasAuthority) {
                // Statement: log.info("Blocking login attempt for username={} as loginType=AUTHORITY but u...
                log.info("Blocking login attempt for username={} as loginType=AUTHORITY but user lacks AUTHORITY role", username);
                // Statement: response.sendRedirect("/auth/login?error&type=authority&m=wrongRole");
                response.sendRedirect("/auth/login?error&type=authority&m=wrongRole");
                // Return: exits the method without a value.
                return;
            // Closes the current code block.
            }
        // Closes the current code block.
        }

        // Comment: explains code for readers.
        // proceed to authentication
        // Statement: filterChain.doFilter(request, response);
        filterChain.doFilter(request, response);
    // Closes the current code block.
    }
// Closes the current code block.
}