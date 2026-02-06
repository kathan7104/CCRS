package com.example.demo.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * Web MVC controller for Home endpoints.
 */
@Controller
public class HomeController {
/**
 * Web MVC controller for Home endpoints.
 */
    @GetMapping("/")
    public String home() {
        return "redirect:/auth/login";
    }
}
