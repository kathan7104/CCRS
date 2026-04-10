/*
 * File: src/main/java/com/example/demo/controller/DevController.java
 * Role: Controller
 * MVC Fit: Handles HTTP requests in the MVC layer.
 * Connects To: Client -> Controller -> Service -> Repository -> Database -> Response
 */

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
// Class Summary: Controller class that handles HTTP requests in the MVC layer.
// @RestController marks this class as a REST controller (JSON responses by default).
@RestController
// @RequestMapping defines a common URL prefix for all endpoints in this controller.
@RequestMapping("/dev")
@ConditionalOnProperty(name = "ccrs.dev.create-authority", havingValue = "true")
public class DevController {
// Field: stores authenticationManager for this class.
    private final AuthenticationManager authenticationManager;
// Constructor: Spring injects dependencies here.
    public DevController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
// @PostMapping handles HTTP POST requests for the given path.
    @PostMapping("/auth-check")
// Endpoint handler: validates input, calls service layer, and returns a response/view.
    public ResponseEntity<?> authCheck(@RequestParam String username, @RequestParam String password) {
        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            Authentication auth = authenticationManager.authenticate(token);
            // 1. Send the result back to the screen
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "username", username,
                    "authorities", auth.getAuthorities().toString()
            ));
        } catch (BadCredentialsException e) {
            // 2. Send the result back to the screen
            return ResponseEntity.status(401).body(Map.of("success", false, "error", "Bad credentials"));
        } catch (Exception e) {
            // 3. Send the result back to the screen
            return ResponseEntity.status(400).body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
