package com.example.demo.dto;

import com.example.demo.entity.Course;
import com.example.demo.entity.Enrollment;

import java.util.List;

public record FacultyCourseRoster(Course course, List<Enrollment> enrollments) {
}
