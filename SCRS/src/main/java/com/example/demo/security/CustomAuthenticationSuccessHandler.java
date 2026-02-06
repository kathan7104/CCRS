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


/**
 * Security component for Custom Authentication Success.
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
/**
 * Security component for Custom Authentication Success.
 */
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
            org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler logoutHandler =
                    new org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler();
            logoutHandler.logout(request, response, authentication);
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
            response.sendRedirect("/auth/login?error&type=authority&m=wrongRole");
            return;
        }
        if ("STUDENT".equalsIgnoreCase(loginType) && !hasStudent) {
            org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler logoutHandler =
                    new org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler();
            logoutHandler.logout(request, response, authentication);
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
            response.sendRedirect("/auth/login?error&type=student&m=wrongRole");
            return;
        }
        if (hasAuthority && "AUTHORITY".equalsIgnoreCase(loginType)) {
            response.sendRedirect("/dashboard/authority");
            return;
        }
        response.sendRedirect("/dashboard");
    }
}
