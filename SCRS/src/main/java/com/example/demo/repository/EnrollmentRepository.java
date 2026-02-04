// Package declaration: groups related classes in a namespace.
package com.example.demo.repository;

// Import statement: brings a class into scope by name.
import com.example.demo.entity.Course;
// Import statement: brings a class into scope by name.
import com.example.demo.entity.Enrollment;
// Import statement: brings a class into scope by name.
import com.example.demo.entity.User;
// Import statement: brings a class into scope by name.
import org.springframework.data.jpa.repository.JpaRepository;

// Import statement: brings a class into scope by name.
import java.util.List;
// Import statement: brings a class into scope by name.
import java.util.Optional;

// Interface declaration: defines a contract of methods.
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // Statement: Optional<Enrollment> findByStudentAndCourse(User student, Course course);
    Optional<Enrollment> findByStudentAndCourse(User student, Course course);
    
    // Statement: boolean existsByStudentAndCourse(User student, Course course);
    boolean existsByStudentAndCourse(User student, Course course);
    
    // Statement: boolean existsByStudentAndCourseAndStatus(User student, Course course, Enroll...
    boolean existsByStudentAndCourseAndStatus(User student, Course course, Enrollment.EnrollmentStatus status);

    // Statement: List<Enrollment> findByCourse(Course course);
    List<Enrollment> findByCourse(Course course);

    // Statement: long countByCourseAndStatus(Course course, Enrollment.EnrollmentStatus status);
    long countByCourseAndStatus(Course course, Enrollment.EnrollmentStatus status);

    // Statement: List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByStudentId(Long studentId);
// Closes the current code block.
}
