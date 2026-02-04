// Package declaration: groups related classes in a namespace.
package com.example.demo.service;

// Import statement: brings a class into scope by name.
import com.example.demo.entity.Course;
// Import statement: brings a class into scope by name.
import com.example.demo.entity.Enrollment;
// Import statement: brings a class into scope by name.
import com.example.demo.entity.User;
// Import statement: brings a class into scope by name.
import com.example.demo.repository.CourseRepository;
// Import statement: brings a class into scope by name.
import com.example.demo.repository.EnrollmentRepository;
// Import statement: brings a class into scope by name.
import com.example.demo.repository.UserRepository;
// Import statement: brings a class into scope by name.
import org.springframework.stereotype.Service;
// Import statement: brings a class into scope by name.
import org.springframework.transaction.annotation.Transactional;

// Import statement: brings a class into scope by name.
import java.time.LocalDateTime;
// Import statement: brings a class into scope by name.
import java.time.LocalDate;
// Import statement: brings a class into scope by name.
import java.util.Optional;

// Annotation: adds metadata used by frameworks/tools.
@Service
// Class declaration: defines a new type.
public class EnrollmentService {

    // Field declaration: defines a member variable.
    private final CourseRepository courseRepository;
    // Field declaration: defines a member variable.
    private final EnrollmentRepository enrollmentRepository;
    // Field declaration: defines a member variable.
    private final UserRepository userRepository;

    // Opens a method/constructor/block.
    public EnrollmentService(CourseRepository courseRepository, EnrollmentRepository enrollmentRepository, UserRepository userRepository) {
        // Uses current object (this) to access a field or method.
        this.courseRepository = courseRepository;
        // Uses current object (this) to access a field or method.
        this.enrollmentRepository = enrollmentRepository;
        // Uses current object (this) to access a field or method.
        this.userRepository = userRepository;
    // Closes the current code block.
    }

    // Annotation: adds metadata used by frameworks/tools.
    @Transactional
    // Field declaration: defines a member variable.
    public Enrollment enrollStudent(String userEmail, Long courseId, String comments, 
                                    // Statement: String fullName, LocalDate dob, Double pastMarks,
                                    String fullName, LocalDate dob, Double pastMarks,
                                    // Statement: String highestQualification, String boardUniversity, Integer passingYear,
                                    String highestQualification, String boardUniversity, Integer passingYear,
                                    // Opens a new code block.
                                    String marksheetPath) {
        // Statement: User student = userRepository.findByEmail(userEmail)
        User student = userRepository.findByEmail(userEmail)
                // Statement: .orElseThrow(() -> new IllegalArgumentException("User not found"));
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        // Statement: Course course = courseRepository.findByIdForUpdate(courseId)
        Course course = courseRepository.findByIdForUpdate(courseId)
                // Statement: .orElseThrow(() -> new IllegalArgumentException("Course not found"));
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        // Opens a method/constructor/block.
        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            // Throw: raises an exception.
            throw new IllegalStateException("Already applied/enrolled in this course");
        // Closes the current code block.
        }

        // Comment: explains code for readers.
        // Prerequisite check
        // Opens a method/constructor/block.
        for (Course prereq : course.getPrerequisites()) {
            // Statement: boolean hasCompleted = enrollmentRepository.existsByStudentAndCourseAndStatus(
            boolean hasCompleted = enrollmentRepository.existsByStudentAndCourseAndStatus(
                    // Statement: student, prereq, Enrollment.EnrollmentStatus.COMPLETED);
                    student, prereq, Enrollment.EnrollmentStatus.COMPLETED);
            // Opens a method/constructor/block.
            if (!hasCompleted) {
                // Throw: raises an exception.
                throw new IllegalStateException("Prerequisite not met: " + prereq.getCode());
            // Closes the current code block.
            }
        // Closes the current code block.
        }

        // Opens a method/constructor/block.
        if (course.getRemainingSeats() <= 0) {
            // Throw: raises an exception.
            throw new IllegalStateException("Course is full");
        // Closes the current code block.
        }

        // Statement: course.setRemainingSeats(course.getRemainingSeats() - 1);
        course.setRemainingSeats(course.getRemainingSeats() - 1);
        // Statement: courseRepository.save(course);
        courseRepository.save(course);

        // Statement: Enrollment enrollment = new Enrollment();
        Enrollment enrollment = new Enrollment();
        // Statement: enrollment.setStudent(student);
        enrollment.setStudent(student);
        // Statement: enrollment.setCourse(course);
        enrollment.setCourse(course);
        // Statement: enrollment.setComments(comments);
        enrollment.setComments(comments);
        // Statement: enrollment.setPastEducationMarks(pastMarks);
        enrollment.setPastEducationMarks(pastMarks);
        // Statement: enrollment.setMarksheetPath(marksheetPath);
        enrollment.setMarksheetPath(marksheetPath);
        
        // Comment: explains code for readers.
        // Save personal info as JSON-like string for simplicity
        // Statement: String personalInfo = String.format(
        String personalInfo = String.format(
                // Statement: "{\"fullName\": \"%s\", \"dob\": \"%s\", \"highestQualification\": \"%s\", \"...
                "{\"fullName\": \"%s\", \"dob\": \"%s\", \"highestQualification\": \"%s\", \"boardUniversity\": \"%s\", \"passingYear\": \"%s\"}",
                // Statement: fullName, dob, highestQualification, boardUniversity, passingYear
                fullName, dob, highestQualification, boardUniversity, passingYear
        // Statement: );
        );
        // Statement: enrollment.setPersonalInfo(personalInfo);
        enrollment.setPersonalInfo(personalInfo);
        
        // Statement: enrollment.setStatus(Enrollment.EnrollmentStatus.PENDING);
        enrollment.setStatus(Enrollment.EnrollmentStatus.PENDING);
        
        // Return: sends a value back to the caller.
        return enrollmentRepository.save(enrollment);
    // Closes the current code block.
    }

    // Opens a method/constructor/block.
    private boolean checkPrerequisites(User student, Course course) {
        // Comment: explains code for readers.
        // Implementation for FR-S3: Check student's academic history against course.getPrerequisites()
        // Comment: explains code for readers.
        // For now, we assume true to allow testing flow.
        // Return: sends a value back to the caller.
        return true; 
    // Closes the current code block.
    }
// Closes the current code block.
}
