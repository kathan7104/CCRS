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
@Component
public class PreLoginRoleValidationFilter extends OncePerRequestFilter {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PreLoginRoleValidationFilter.class);
    private final UserRepository userRepository;
    public PreLoginRoleValidationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Check a rule -> decide what to do next
        if (!HttpMethod.POST.name().equalsIgnoreCase(request.getMethod()) || !"/auth/login".equals(request.getServletPath())) {
            filterChain.doFilter(request, response);
            // 2. Send the result back to the screen
            return;
        }
        String loginType = request.getParameter("loginType");
        // 3. Check a rule -> decide what to do next
        if (!StringUtils.hasText(loginType)) {
            loginType = "STUDENT"; // default
        }
        String username = request.getParameter("username");
        // 4. Check a rule -> decide what to do next
        if (!StringUtils.hasText(username)) {
            filterChain.doFilter(request, response);
            // 5. Send the result back to the screen
            return;
        }
        // 6. Get or save data in the database
        Optional<User> userOpt = userRepository.findByEmail(username)
                // 7. Get or save data in the database
                .or(() -> userRepository.findByMobileNumber(username));
        // 8. Check a rule -> decide what to do next
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean hasStudent = user.getRoles().stream().anyMatch(r -> r.equalsIgnoreCase("STUDENT") || r.equalsIgnoreCase("ROLE_STUDENT"));
            boolean hasAuthority = user.getRoles().stream().anyMatch(r -> r.toUpperCase().startsWith("AUTHORITY") || r.toUpperCase().startsWith("ROLE_AUTHORITY"));
            // 9. Check a rule -> decide what to do next
            if ("STUDENT".equalsIgnoreCase(loginType) && !hasStudent) {
                // 10. Note: write a log so we can track it
                log.info("Blocking login attempt for username={} as loginType=STUDENT but user lacks STUDENT role", username);
                response.sendRedirect("/auth/login?error&type=student&m=wrongRole");
                // 11. Send the result back to the screen
                return;
            }
            // 12. Check a rule -> decide what to do next
            if ("AUTHORITY".equalsIgnoreCase(loginType) && !hasAuthority) {
                // 13. Note: write a log so we can track it
                log.info("Blocking login attempt for username={} as loginType=AUTHORITY but user lacks AUTHORITY role", username);
                response.sendRedirect("/auth/login?error&type=authority&m=wrongRole");
                // 14. Send the result back to the screen
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}