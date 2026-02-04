// Package declaration: groups related classes in a namespace.
package com.example.demo.controller;

// Import statement: brings a class into scope by name.
import org.springframework.stereotype.Controller;
// Import statement: brings a class into scope by name.
import org.springframework.web.bind.annotation.GetMapping;

// Comment: explains code for readers.
/**
 // Comment: explains code for readers.
 * Simple controller for the application root.
 // Comment: explains code for readers.
 * Redirects to the login page.
 // Comment: explains code for readers.
 */
// Annotation: adds metadata used by frameworks/tools.
@Controller
// Class declaration: defines a new type.
public class HomeController {

    // Comment: explains code for readers.
    // Redirect root path to login page.
    // Annotation: adds metadata used by frameworks/tools.
    @GetMapping("/")
    // Opens a method/constructor/block.
    public String home() {
        // Return: sends a value back to the caller.
        return "redirect:/auth/login";
    // Closes the current code block.
    }
// Closes the current code block.
}
