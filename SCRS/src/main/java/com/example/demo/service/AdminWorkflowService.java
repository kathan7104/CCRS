package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminWorkflowService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public AdminWorkflowService(EnrollmentRepository enrollmentRepository,
                                CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
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
