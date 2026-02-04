// Package declaration: groups related classes in a namespace.
package com.example.demo.controller;

// Import statement: brings a class into scope by name.
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// Import statement: brings a class into scope by name.
import org.springframework.http.ResponseEntity;
// Import statement: brings a class into scope by name.
import org.springframework.security.authentication.AuthenticationManager;
// Import statement: brings a class into scope by name.
import org.springframework.security.authentication.BadCredentialsException;
// Import statement: brings a class into scope by name.
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// Import statement: brings a class into scope by name.
import org.springframework.security.core.Authentication;
// Import statement: brings a class into scope by name.
import org.springframework.web.bind.annotation.PostMapping;
// Import statement: brings a class into scope by name.
import org.springframework.web.bind.annotation.RequestMapping;
// Import statement: brings a class into scope by name.
import org.springframework.web.bind.annotation.RequestParam;
// Import statement: brings a class into scope by name.
import org.springframework.web.bind.annotation.RestController;

// Import statement: brings a class into scope by name.
import java.util.Map;

// Comment: explains code for readers.
/**
 // Comment: explains code for readers.
 * Development-only controller to test authentication programmatically.
 // Comment: explains code for readers.
 * Enabled only when `ccrs.dev.create-authority=true` to avoid exposure in production.
 // Comment: explains code for readers.
 */
// Annotation: adds metadata used by frameworks/tools.
@RestController
// Annotation: adds metadata used by frameworks/tools.
@RequestMapping("/dev")
// Annotation: adds metadata used by frameworks/tools.
@ConditionalOnProperty(name = "ccrs.dev.create-authority", havingValue = "true")
// Class declaration: defines a new type.
public class DevController {

    // Field declaration: defines a member variable.
    private final AuthenticationManager authenticationManager;

    // Opens a method/constructor/block.
    public DevController(AuthenticationManager authenticationManager) {
        // Uses current object (this) to access a field or method.
        this.authenticationManager = authenticationManager;
    // Closes the current code block.
    }

    // Annotation: adds metadata used by frameworks/tools.
    @PostMapping("/auth-check")
    // Opens a method/constructor/block.
    public ResponseEntity<?> authCheck(@RequestParam String username, @RequestParam String password) {
        // Opens a new code block.
        try {
            // Statement: UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticatio...
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            // Statement: Authentication auth = authenticationManager.authenticate(token);
            Authentication auth = authenticationManager.authenticate(token);
            // Return: sends a value back to the caller.
            return ResponseEntity.ok(Map.of(
                    // Statement: "success", true,
                    "success", true,
                    // Statement: "username", username,
                    "username", username,
                    // Statement: "authorities", auth.getAuthorities().toString()
                    "authorities", auth.getAuthorities().toString()
            // Statement: ));
            ));
        // Opens a method/constructor/block.
        } catch (BadCredentialsException e) {
            // Return: sends a value back to the caller.
            return ResponseEntity.status(401).body(Map.of("success", false, "error", "Bad credentials"));
        // Opens a method/constructor/block.
        } catch (Exception e) {
            // Return: sends a value back to the caller.
            return ResponseEntity.status(400).body(Map.of("success", false, "error", e.getMessage()));
        // Closes the current code block.
        }
    // Closes the current code block.
    }
// Closes the current code block.
}
