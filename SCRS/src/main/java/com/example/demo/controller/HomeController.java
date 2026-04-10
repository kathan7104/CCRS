/*
 * File: src/main/java/com/example/demo/controller/HomeController.java
 * Role: Controller
 * MVC Fit: Handles HTTP requests in the MVC layer.
 * Connects To: Client -> Controller -> Service -> Repository -> Database -> Response
 */

package com.example.demo.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
// Class Summary: Controller class that handles HTTP requests in the MVC layer.
// @Controller marks this class as an MVC controller that returns views.
@Controller
public class HomeController {
// @GetMapping handles HTTP GET requests for the given path.
    @GetMapping("/")
// Endpoint handler: validates input, calls service layer, and returns a response/view.
    public String home() {
        // 1. Send the result back to the screen
        return "redirect:/auth/login";
    }
}
