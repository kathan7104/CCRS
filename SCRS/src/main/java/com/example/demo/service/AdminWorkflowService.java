/*
 * File: src/main/java/com/example/demo/service/AdminWorkflowService.java
 * Role: Service
 * MVC Fit: Contains business logic used by controllers.
 * Connects To: Controller calls Service, Service calls Repository
 */

package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Class Summary: Service class that contains business logic used by controllers.
// @Service marks the business logic layer for Spring to manage as a bean.
@Service
public class AdminWorkflowService {
// Field: stores enrollmentRepository for this class.
    private final EnrollmentRepository enrollmentRepository;
// Field: stores courseRepository for this class.
    private final CourseRepository courseRepository;

// Constructor: Spring injects dependencies here.
    public AdminWorkflowService(EnrollmentRepository enrollmentRepository,
                                CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
// Service method: contains business logic and coordinates repositories.
    public void approveEnrollment(Long enrollmentId, String adminNote) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));
        if (enrollment.getStatus() != Enrollment.EnrollmentStatus.PENDING) {
            throw new IllegalStateException("Only pending applications can be approved.");
        }

        enrollment.setStatus(Enrollment.EnrollmentStatus.APPROVED);
        enrollment.setComments(adminNote);
        enrollmentRepository.save(enrollment);
    }

    @Transactional
// Service method: contains business logic and coordinates repositories.
    public void rejectEnrollment(Long enrollmentId, String adminNote) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));
        if (enrollment.getStatus() != Enrollment.EnrollmentStatus.PENDING) {
            throw new IllegalStateException("Only pending applications can be rejected.");
        }

        Course course = courseRepository.findByIdForUpdate(enrollment.getCourse().getId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        course.setRemainingSeats(course.getRemainingSeats() + 1);
        courseRepository.save(course);

        enrollment.setStatus(Enrollment.EnrollmentStatus.CANCELLED);
        enrollment.setComments(adminNote);
        enrollmentRepository.save(enrollment);
    }
}
