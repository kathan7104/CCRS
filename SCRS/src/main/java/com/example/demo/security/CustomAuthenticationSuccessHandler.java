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
 * Custom success handler that redirects users after successful login based on their role
 * and the selected login type on the login form. Ensures a user cannot log in via the
 * wrong login type (e.g., student login for an authority account) by validating the
 * selected type against the authenticated user's granted authorities.
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    // Called after a successful authentication
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // loginType is an optional parameter posted from the login form: "STUDENT" or "AUTHORITY"
        String loginType = request.getParameter("loginType");
        if (loginType == null || loginType.isBlank()) {
            loginType = "STUDENT"; // default
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean hasStudent = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
        // treat any authority role that starts with ROLE_AUTHORITY (e.g., ROLE_AUTHORITY_DIRECTOR)
        boolean hasAuthority = authorities.stream().anyMatch(a -> a.getAuthority().startsWith("ROLE_AUTHORITY"));

        // If the selected type does not match available roles, deny by logging the user out
        // and redirecting back to the login page with an informative message. This prevents
        // a user with correct credentials from being silently logged in via the wrong form.
        if ("AUTHORITY".equalsIgnoreCase(loginType) && !hasAuthority) {
            // clear authentication and invalidate session
            org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler logoutHandler =
                    new org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler();
            logoutHandler.logout(request, response, authentication);
            // clear any leftover context as a safety measure
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

        // Redirect based on actual role preference.
        if (hasAuthority && "AUTHORITY".equalsIgnoreCase(loginType)) {
            response.sendRedirect("/dashboard/authority");
            return;
        }
        // Default student dashboard
        response.sendRedirect("/dashboard");
    }
}
