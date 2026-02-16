package com.example.demo.security;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Collection;
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String loginType = request.getParameter("loginType");
        // 1. Check a rule -> decide what to do next
        if (loginType == null || loginType.isBlank()) {
            loginType = "STUDENT"; // default
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean hasStudent = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
        boolean hasAuthority = authorities.stream().anyMatch(a -> a.getAuthority().startsWith("ROLE_AUTHORITY"));
        // 2. Check a rule -> decide what to do next
        if ("AUTHORITY".equalsIgnoreCase(loginType) && !hasAuthority) {
            org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler logoutHandler =
                    new org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler();
            logoutHandler.logout(request, response, authentication);
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
            response.sendRedirect("/auth/login?error&type=authority&m=wrongRole");
            // 3. Send the result back to the screen
            return;
        }
        // 4. Check a rule -> decide what to do next
        if ("STUDENT".equalsIgnoreCase(loginType) && !hasStudent) {
            org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler logoutHandler =
                    new org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler();
            logoutHandler.logout(request, response, authentication);
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
            response.sendRedirect("/auth/login?error&type=student&m=wrongRole");
            // 5. Send the result back to the screen
            return;
        }
        // 6. Check a rule -> decide what to do next
        if (hasAuthority && "AUTHORITY".equalsIgnoreCase(loginType)) {
            boolean isAdmin = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_AUTHORITY_ADMIN"));
            boolean isDirector = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_AUTHORITY_DIRECTOR"));
            boolean isStaff = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_AUTHORITY_STAFF"));
            if (isAdmin) {
                response.sendRedirect("/admin/dashboard");
                return;
            }
            if (isDirector) {
                response.sendRedirect("/director/dashboard");
                return;
            }
            if (isStaff) {
                response.sendRedirect("/staff/dashboard");
                return;
            }
            response.sendRedirect("/dashboard/authority");
            // 7. Send the result back to the screen
            return;
        }
        response.sendRedirect("/dashboard");
    }
}
