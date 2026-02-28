package com.example.demo.service;

import com.example.demo.dto.FacultySubjectRoster;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.FacultySubjectAssignment;
import com.example.demo.entity.User;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.FacultySubjectAssignmentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FacultyRosterService {
    private final FacultySubjectAssignmentRepository assignmentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public FacultyRosterService(FacultySubjectAssignmentRepository assignmentRepository,
                                EnrollmentRepository enrollmentRepository) {
        this.assignmentRepository = assignmentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<FacultySubjectRoster> getFacultyRoster(User faculty) {
        if (faculty == null || faculty.getId() == null) {
            return List.of();
        }

        String department = normalize(faculty.getDepartment());
        if (department.isBlank()) {
            return List.of();
        }

        List<FacultySubjectAssignment> assignments = assignmentRepository.findByFacultyIdWithSubject(faculty.getId()).stream()
                .filter(a -> a.getSubject() != null)
                .filter(a -> department.equalsIgnoreCase(normalize(a.getSubject().getDepartment())))
                .toList();

        if (assignments.isEmpty()) {
            return List.of();
        }

        List<Enrollment> enrollments = enrollmentRepository.findApprovedOrEnrolledByDepartment(department);
        List<FacultySubjectRoster> roster = new ArrayList<>();
        for (FacultySubjectAssignment assignment : assignments) {
            String assignedProgram = normalize(assignment.getSubject().getProgramName());
            List<Enrollment> filtered = enrollments.stream()
                    .filter(e -> e.getCourse() != null)
                    .filter(e -> assignedProgram.equalsIgnoreCase(normalize(e.getCourse().getProgramName())))
                    .toList();
            roster.add(new FacultySubjectRoster(assignment.getSubject(), filtered));
        }
        return roster;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
