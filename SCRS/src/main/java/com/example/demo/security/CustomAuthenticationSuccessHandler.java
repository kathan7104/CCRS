// Package declaration: groups related classes in a namespace.
package com.example.demo.security;

// Import statement: brings a class into scope by name.
import jakarta.servlet.ServletException;
// Import statement: brings a class into scope by name.
import jakarta.servlet.http.HttpServletRequest;
// Import statement: brings a class into scope by name.
import jakarta.servlet.http.HttpServletResponse;
// Import statement: brings a class into scope by name.
import org.springframework.security.core.Authentication;
// Import statement: brings a class into scope by name.
import org.springframework.security.core.GrantedAuthority;
// Import statement: brings a class into scope by name.
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
// Import statement: brings a class into scope by name.
import org.springframework.stereotype.Component;

// Import statement: brings a class into scope by name.
import java.io.IOException;
// Import statement: brings a class into scope by name.
import java.util.Collection;

// Comment: explains code for readers.
/**
 // Comment: explains code for readers.
 * Custom success handler that redirects users after successful login based on their role
 // Comment: explains code for readers.
 * and the selected login type on the login form. Ensures a user cannot log in via the
 // Comment: explains code for readers.
 * wrong login type (e.g., student login for an authority account) by validating the
 // Comment: explains code for readers.
 * selected type against the authenticated user's granted authorities.
 // Comment: explains code for readers.
 */
// Annotation: adds metadata used by frameworks/tools.
@Component
// Class declaration: defines a new type.
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    // Comment: explains code for readers.
    // Called after a successful authentication
    // Annotation: adds metadata used by frameworks/tools.
    @Override
    // Field declaration: defines a member variable.
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        // Opens a new code block.
                                        Authentication authentication) throws IOException, ServletException {
        // Comment: explains code for readers.
        // loginType is an optional parameter posted from the login form: "STUDENT" or "AUTHORITY"
        // Statement: String loginType = request.getParameter("loginType");
        String loginType = request.getParameter("loginType");
        // Opens a method/constructor/block.
        if (loginType == null || loginType.isBlank()) {
            // Statement: loginType = "STUDENT"; // default
            loginType = "STUDENT"; // default
        // Closes the current code block.
        }

        // Statement: Collection<? extends GrantedAuthority> authorities = authentication.getAuthor...
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        // Statement: boolean hasStudent = authorities.stream().anyMatch(a -> a.getAuthority().equa...
        boolean hasStudent = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
        // Comment: explains code for readers.
        // treat any authority role that starts with ROLE_AUTHORITY (e.g., ROLE_AUTHORITY_DIRECTOR)
        // Statement: boolean hasAuthority = authorities.stream().anyMatch(a -> a.getAuthority().st...
        boolean hasAuthority = authorities.stream().anyMatch(a -> a.getAuthority().startsWith("ROLE_AUTHORITY"));

        // Comment: explains code for readers.
        // If the selected type does not match available roles, deny by logging the user out
        // Comment: explains code for readers.
        // and redirecting back to the login page with an informative message. This prevents
        // Comment: explains code for readers.
        // a user with correct credentials from being silently logged in via the wrong form.
        // Opens a method/constructor/block.
        if ("AUTHORITY".equalsIgnoreCase(loginType) && !hasAuthority) {
            // Comment: explains code for readers.
            // clear authentication and invalidate session
            // Statement: org.springframework.security.web.authentication.logout.SecurityContextLogoutH...
            org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler logoutHandler =
                    // Creates a new object instance.
                    new org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler();
            // Statement: logoutHandler.logout(request, response, authentication);
            logoutHandler.logout(request, response, authentication);
            // Comment: explains code for readers.
            // clear any leftover context as a safety measure
            // Statement: org.springframework.security.core.context.SecurityContextHolder.clearContext();
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
            // Statement: response.sendRedirect("/auth/login?error&type=authority&m=wrongRole");
            response.sendRedirect("/auth/login?error&type=authority&m=wrongRole");
            // Return: exits the method without a value.
            return;
        // Closes the current code block.
        }
        // Opens a method/constructor/block.
        if ("STUDENT".equalsIgnoreCase(loginType) && !hasStudent) {
            // Statement: org.springframework.security.web.authentication.logout.SecurityContextLogoutH...
            org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler logoutHandler =
                    // Creates a new object instance.
                    new org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler();
            // Statement: logoutHandler.logout(request, response, authentication);
            logoutHandler.logout(request, response, authentication);
            // Statement: org.springframework.security.core.context.SecurityContextHolder.clearContext();
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
            // Statement: response.sendRedirect("/auth/login?error&type=student&m=wrongRole");
            response.sendRedirect("/auth/login?error&type=student&m=wrongRole");
            // Return: exits the method without a value.
            return;
        // Closes the current code block.
        }

        // Comment: explains code for readers.
        // Redirect based on actual role preference.
        // Opens a method/constructor/block.
        if (hasAuthority && "AUTHORITY".equalsIgnoreCase(loginType)) {
            // Statement: response.sendRedirect("/dashboard/authority");
            response.sendRedirect("/dashboard/authority");
            // Return: exits the method without a value.
            return;
        // Closes the current code block.
        }
        // Comment: explains code for readers.
        // Default student dashboard
        // Statement: response.sendRedirect("/dashboard");
        response.sendRedirect("/dashboard");
    // Closes the current code block.
    }
// Closes the current code block.
}
