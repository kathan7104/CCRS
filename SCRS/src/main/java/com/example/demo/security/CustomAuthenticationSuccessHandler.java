package com.example.demo.security;
import com.example.demo.service.StudentAccessService;
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
    private final StudentAccessService studentAccessService;

    public CustomAuthenticationSuccessHandler(StudentAccessService studentAccessService) {
        this.studentAccessService = studentAccessService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String loginType = request.getParameter("loginType");
        if (loginType == null || loginType.isBlank()) {
            loginType = "STUDENT"; // default
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean hasStudent = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
        boolean hasAuthority = authorities.stream().anyMatch(a -> a.getAuthority().startsWith("ROLE_AUTHORITY"));

        if ("AUTHORITY".equalsIgnoreCase(loginType) && !hasAuthority) {
            var logoutHandler = new org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler();
            logoutHandler.logout(request, response, authentication);
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
            response.sendRedirect("/auth/login?error=authority&type=authority&m=wrongRole");
            return;
        }

        if ("STUDENT".equalsIgnoreCase(loginType) && !hasStudent) {
            var logoutHandler = new org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler();
            logoutHandler.logout(request, response, authentication);
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
            response.sendRedirect("/auth/login?error=student&type=student&m=wrongRole");
            return;
        }

        if (hasStudent) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails userDetails) {
                if (!studentAccessService.isStudentAllowed(userDetails.getUser())) {
                    var logoutHandler = new org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler();
                    logoutHandler.logout(request, response, authentication);
                    org.springframework.security.core.context.SecurityContextHolder.clearContext();
                    response.sendRedirect("/auth/login?error=student&type=student&m=registrationClosed");
                    return;
                }
            }
        }

        if (hasAuthority && "AUTHORITY".equalsIgnoreCase(loginType)) {
            boolean isAdmin = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_AUTHORITY_ADMIN"));
            boolean isDirector = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_AUTHORITY_DIRECTOR"));
            boolean isStaff = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_AUTHORITY_STAFF"));
            boolean isFaculty = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_AUTHORITY_FACULTY"));
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
            if (isFaculty) {
                response.sendRedirect("/faculty/roster");
                return;
            }
            response.sendRedirect("/dashboard/authority");
            return;
        }
        response.sendRedirect("/dashboard");
    }
}
