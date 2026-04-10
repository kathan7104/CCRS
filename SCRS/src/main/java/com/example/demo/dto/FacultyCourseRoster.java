/*
 * File: src/main/java/com/example/demo/dto/FacultyCourseRoster.java
 * Role: DTO
 * MVC Fit: Data Transfer Object used between layers or views.
 * Connects To: Controller/Service use it to carry data
 */

package com.example.demo.dto;

import com.example.demo.entity.Course;
import com.example.demo.entity.Enrollment;

import java.util.List;

// Method: performs a focused unit of work in this class.
public record FacultyCourseRoster(Course course, List<Enrollment> enrollments) {
}
