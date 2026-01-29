package com.example.demo.controller;

import com.example.demo.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
/**
 * Controller for the authenticated user dashboard.
 * Adds simple user info to the model for display.
 */
public class DashboardController {

    // Show dashboard page and populate user name/email when available.
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails != null) {
            model.addAttribute("userName", userDetails.getUser().getFullName());
            model.addAttribute("userEmail", userDetails.getUsername());
        }
        return "dashboard";
    }
}
