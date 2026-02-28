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

@Controller
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyRosterService facultyRosterService;

    public FacultyController(FacultyRosterService facultyRosterService) {
        this.facultyRosterService = facultyRosterService;
    }

    @GetMapping("/roster")
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
