package com.example.demo.service;

import com.example.demo.entity.Enrollment;
import com.example.demo.entity.User;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;

@Service
public class StudentAccessService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public StudentAccessService(EnrollmentRepository enrollmentRepository,
                                CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
    }

    public boolean hasActiveEnrollment(User student) {
        if (student == null || student.getId() == null) {
            return false;
        }
        return enrollmentRepository.findByStudentId(student.getId()).stream()
                .anyMatch(e -> e.getStatus() == Enrollment.EnrollmentStatus.APPROVED
                        || e.getStatus() == Enrollment.EnrollmentStatus.ENROLLED);
    }

    public boolean hasOpenCourseSeats() {
        return courseRepository.countByRemainingSeatsGreaterThan(0) > 0;
    }

    public boolean isStudentAllowed(User student) {
        if (student == null) {
            return false;
        }
        if (hasActiveEnrollment(student)) {
            return true;
        }
        // Temporary student login allowed only when registration is open (open seats exist)
        return hasOpenCourseSeats();
    }
}
