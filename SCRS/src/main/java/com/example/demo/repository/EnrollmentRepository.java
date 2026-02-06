package com.example.demo.repository;
import com.example.demo.entity.Course;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for Enrollment.
 */
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudentAndCourse(User student, Course course);
    boolean existsByStudentAndCourse(User student, Course course);
    boolean existsByStudentAndCourseAndStatus(User student, Course course, Enrollment.EnrollmentStatus status);
    List<Enrollment> findByCourse(Course course);
    long countByCourseAndStatus(Course course, Enrollment.EnrollmentStatus status);
    List<Enrollment> findByStudentId(Long studentId);
}
