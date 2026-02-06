package com.example.demo.controller;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;


/**
 * Web MVC controller for Dev endpoints.
 */
@RestController
@RequestMapping("/dev")
@ConditionalOnProperty(name = "ccrs.dev.create-authority", havingValue = "true")
public class DevController {
    private final AuthenticationManager authenticationManager;
    public DevController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
/**
 * Web MVC controller for Dev endpoints.
 */
/**
 * Web MVC controller for Dev endpoints.
 */
    @PostMapping("/auth-check")
    public ResponseEntity<?> authCheck(@RequestParam String username, @RequestParam String password) {
        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            Authentication auth = authenticationManager.authenticate(token);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "username", username,
                    "authorities", auth.getAuthorities().toString()
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("success", false, "error", "Bad credentials"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
