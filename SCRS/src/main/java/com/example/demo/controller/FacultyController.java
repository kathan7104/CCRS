/*
 * File: src/main/java/com/example/demo/controller/FacultyController.java
 * Role: Controller
 * MVC Fit: Handles HTTP requests in the MVC layer.
 * Connects To: Client -> Controller -> Service -> Repository -> Database -> Response
 */

package com.example.demo.controller;

import com.example.demo.dto.FacultySubjectRoster;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.FacultyRosterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

// Class Summary: Controller class that handles HTTP requests in the MVC layer.
// @Controller marks this class as an MVC controller that returns views.
@Controller
// @RequestMapping defines a common URL prefix for all endpoints in this controller.
@RequestMapping("/faculty")
public class FacultyController {
// Field: stores facultyRosterService for this class.
    private final FacultyRosterService facultyRosterService;

// Constructor: Spring injects dependencies here.
    public FacultyController(FacultyRosterService facultyRosterService) {
        this.facultyRosterService = facultyRosterService;
    }

// @GetMapping handles HTTP GET requests for the given path.
    @GetMapping("/roster")
// Endpoint handler for GET /roster: reads inputs, calls service, returns a view/JSON.
    public String roster(@AuthenticationPrincipal CustomUserDetails principal,
                         HttpServletRequest request,
                         Model model) {
        List<FacultySubjectRoster> subjectRosters = facultyRosterService.getFacultyRoster(principal.getUser());
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("userName", principal.getUser().getFullName());
        model.addAttribute("department", principal.getUser().getDepartment());
        model.addAttribute("subjectRosters", subjectRosters);
        return "faculty/roster";
    }
}
