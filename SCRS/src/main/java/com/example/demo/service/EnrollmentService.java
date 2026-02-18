package com.example.demo.service;
import com.example.demo.entity.Course;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.EnrollmentDocument;
import com.example.demo.entity.User;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
    @Transactional
    public Enrollment enrollStudent(String userEmail, Long courseId, String comments, 
                                    String fullName, LocalDate dob, Double pastMarks,
                                    String highestQualification, String boardUniversity, Integer passingYear,
                                    List<DocumentPayload> documents) {
        // 1. Get or save data in the database
        User student = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        // 2. Get or save data in the database
        Course course = courseRepository.findByIdForUpdate(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        // 3. Check a rule -> decide what to do next
        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            // 4. Important: stop here and report an error
            throw new IllegalStateException("Already applied/enrolled in this course");
        }
        for (Course prereq : course.getPrerequisites()) {
            // 5. Get or save data in the database
            boolean hasCompleted = enrollmentRepository.existsByStudentAndCourseAndStatus(
                    student, prereq, Enrollment.EnrollmentStatus.COMPLETED);
            // 6. Check a rule -> decide what to do next
            if (!hasCompleted) {
                // 7. Important: stop here and report an error
                throw new IllegalStateException("Prerequisite not met: " + prereq.getCode());
            }
        }
        // 8. Check a rule -> decide what to do next
        if (course.getRemainingSeats() <= 0) {
            // 9. Important: stop here and report an error
            throw new IllegalStateException("Course is full");
        }
        course.setRemainingSeats(course.getRemainingSeats() - 1);
        // 10. Get or save data in the database
        courseRepository.save(course);
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setComments(comments);
        enrollment.setPastEducationMarks(pastMarks);
        enrollment.setMarksheetPath(null);
        for (DocumentPayload documentPayload : documents) {
            EnrollmentDocument document = new EnrollmentDocument();
            document.setDocumentType(documentPayload.documentType());
            document.setFileName(documentPayload.fileName());
            document.setFilePath(documentPayload.filePath());
            document.setContentType(documentPayload.contentType());
            enrollment.addDocument(document);
            if (enrollment.getMarksheetPath() == null
                    && documentPayload.documentType() == EnrollmentDocument.DocumentType.MARKSHEET) {
                enrollment.setMarksheetPath(documentPayload.filePath());
            }
        }
        String personalInfo = String.format(
                "{\"fullName\": \"%s\", \"dob\": \"%s\", \"highestQualification\": \"%s\", \"boardUniversity\": \"%s\", \"passingYear\": \"%s\"}",
                fullName, dob, highestQualification, boardUniversity, passingYear
        );
        enrollment.setPersonalInfo(personalInfo);
        enrollment.setStatus(Enrollment.EnrollmentStatus.PENDING);
        // 11. Send the result back to the screen
        return enrollmentRepository.save(enrollment);
    }
    private boolean checkPrerequisites(User student, Course course) {
        // 1. Send the result back to the screen
        return true; 
    }

    public record DocumentPayload(EnrollmentDocument.DocumentType documentType,
                                  String fileName,
                                  String filePath,
                                  String contentType) {
    }
}
