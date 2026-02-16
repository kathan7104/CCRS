package com.example.demo.repository;

import com.example.demo.entity.FacultyCourseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacultyCourseAssignmentRepository extends JpaRepository<FacultyCourseAssignment, Long> {
    List<FacultyCourseAssignment> findByFacultyDepartmentIgnoreCase(String department);
    boolean existsByFacultyIdAndCourseId(Long facultyId, Long courseId);
}
