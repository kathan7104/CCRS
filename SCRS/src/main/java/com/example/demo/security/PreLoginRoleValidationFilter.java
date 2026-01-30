package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Pre-authentication filter that validates the selected loginType (STUDENT/AUTHORITY)
 * against the user's roles before authentication proceeds. If a mismatch is detected,
 * the filter redirects back to the login page and prevents authentication from happening.
 */
@Component
public class PreLoginRoleValidationFilter extends OncePerRequestFilter {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PreLoginRoleValidationFilter.class);

    private final UserRepository userRepository;

    public PreLoginRoleValidationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // only act on form login POST to /auth/login
        if (!HttpMethod.POST.name().equalsIgnoreCase(request.getMethod()) || !"/auth/login".equals(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        String loginType = request.getParameter("loginType");
        if (!StringUtils.hasText(loginType)) {
            loginType = "STUDENT"; // default
        }
        String username = request.getParameter("username");
        if (!StringUtils.hasText(username)) {
            // let authentication handle missing username
            filterChain.doFilter(request, response);
            return;
        }

        // find by email or mobile (email first)
        Optional<User> userOpt = userRepository.findByEmail(username)
                .or(() -> userRepository.findByMobileNumber(username));

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean hasStudent = user.getRoles().stream().anyMatch(r -> r.equalsIgnoreCase("STUDENT") || r.equalsIgnoreCase("ROLE_STUDENT"));
            boolean hasAuthority = user.getRoles().stream().anyMatch(r -> r.toUpperCase().startsWith("AUTHORITY") || r.toUpperCase().startsWith("ROLE_AUTHORITY"));

            if ("STUDENT".equalsIgnoreCase(loginType) && !hasStudent) {
                log.info("Blocking login attempt for username={} as loginType=STUDENT but user lacks STUDENT role", username);
                response.sendRedirect("/auth/login?error&type=student&m=wrongRole");
                return;
            }
            if ("AUTHORITY".equalsIgnoreCase(loginType) && !hasAuthority) {
                log.info("Blocking login attempt for username={} as loginType=AUTHORITY but user lacks AUTHORITY role", username);
                response.sendRedirect("/auth/login?error&type=authority&m=wrongRole");
                return;
            }
        }

        // proceed to authentication
        filterChain.doFilter(request, response);
    }
}