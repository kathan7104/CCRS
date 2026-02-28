package com.example.demo.repository;

import com.example.demo.entity.FacultyCourseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FacultyCourseAssignmentRepository extends JpaRepository<FacultyCourseAssignment, Long> {
    List<FacultyCourseAssignment> findByFacultyDepartmentIgnoreCase(String department);
    boolean existsByFacultyIdAndCourseId(Long facultyId, Long courseId);

    @Query("select a from FacultyCourseAssignment a join fetch a.course where a.faculty.id = :facultyId")
    List<FacultyCourseAssignment> findByFacultyIdWithCourse(@Param("facultyId") Long facultyId);
}
