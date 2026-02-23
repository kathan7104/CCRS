package com.example.demo.dto;

import com.example.demo.entity.Enrollment;
import com.example.demo.entity.Subject;

import java.util.List;

public record FacultySubjectRoster(Subject subject, List<Enrollment> enrollments) {
}
