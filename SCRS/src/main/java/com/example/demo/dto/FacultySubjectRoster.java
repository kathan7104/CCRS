/*
 * File: src/main/java/com/example/demo/dto/FacultySubjectRoster.java
 * Role: DTO
 * MVC Fit: Data Transfer Object used between layers or views.
 * Connects To: Controller/Service use it to carry data
 */

package com.example.demo.dto;

import com.example.demo.entity.Enrollment;
import com.example.demo.entity.Subject;

import java.util.List;

// Method: performs a focused unit of work in this class.
public record FacultySubjectRoster(Subject subject, List<Enrollment> enrollments) {
}
