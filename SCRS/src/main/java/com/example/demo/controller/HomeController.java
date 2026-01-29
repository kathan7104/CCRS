package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Simple controller for the application root.
 * Redirects to the login page.
 */
@Controller
public class HomeController {

    // Redirect root path to login page.
    @GetMapping("/")
    public String home() {
        return "redirect:/auth/login";
    }
}
