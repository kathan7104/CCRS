package com.example.demo.service;
import com.example.demo.entity.Course;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.User;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Business logic for Enrollment.
 */
@Service
public class EnrollmentService {
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    public EnrollmentService(CourseRepository courseRepository, EnrollmentRepository enrollmentRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
    }
/**
 * Business logic for Enrollment.
 */
    @Transactional
    public Enrollment enrollStudent(String userEmail, Long courseId, String comments, 
                                    String fullName, LocalDate dob, Double pastMarks,
                                    String highestQualification, String boardUniversity, Integer passingYear,
                                    String marksheetPath) {
        User student = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Course course = courseRepository.findByIdForUpdate(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            throw new IllegalStateException("Already applied/enrolled in this course");
        }
        for (Course prereq : course.getPrerequisites()) {
            boolean hasCompleted = enrollmentRepository.existsByStudentAndCourseAndStatus(
                    student, prereq, Enrollment.EnrollmentStatus.COMPLETED);
            if (!hasCompleted) {
                throw new IllegalStateException("Prerequisite not met: " + prereq.getCode());
            }
        }
        if (course.getRemainingSeats() <= 0) {
            throw new IllegalStateException("Course is full");
        }
        course.setRemainingSeats(course.getRemainingSeats() - 1);
        courseRepository.save(course);
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setComments(comments);
        enrollment.setPastEducationMarks(pastMarks);
        enrollment.setMarksheetPath(marksheetPath);
        String personalInfo = String.format(
                "{\"fullName\": \"%s\", \"dob\": \"%s\", \"highestQualification\": \"%s\", \"boardUniversity\": \"%s\", \"passingYear\": \"%s\"}",
                fullName, dob, highestQualification, boardUniversity, passingYear
        );
        enrollment.setPersonalInfo(personalInfo);
        enrollment.setStatus(Enrollment.EnrollmentStatus.PENDING);
        return enrollmentRepository.save(enrollment);
    }
    private boolean checkPrerequisites(User student, Course course) {
        return true; 
    }
}
