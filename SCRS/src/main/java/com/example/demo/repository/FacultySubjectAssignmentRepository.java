/*
 * File: src/main/java/com/example/demo/repository/FacultySubjectAssignmentRepository.java
 * Role: Repository
 * MVC Fit: Data access layer using Spring Data JPA.
 * Connects To: Service uses Repository for CRUD queries
 */

package com.example.demo.repository;

import com.example.demo.entity.FacultySubjectAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

// Class Summary: Repository class that is the data access layer using Spring Data JPA.
public interface FacultySubjectAssignmentRepository extends JpaRepository<FacultySubjectAssignment, Long> {
    boolean existsByFacultyIdAndSubjectId(Long facultyId, Long subjectId);

    @Query("select a from FacultySubjectAssignment a join fetch a.subject where a.faculty.id = :facultyId")
    List<FacultySubjectAssignment> findByFacultyIdWithSubject(@Param("facultyId") Long facultyId);

    @Query("select a from FacultySubjectAssignment a join fetch a.subject s join fetch a.faculty f where lower(f.department) = lower(:department) order by a.assignedAt desc")
    List<FacultySubjectAssignment> findByFacultyDepartmentIgnoreCaseOrderByAssignedAtDesc(@Param("department") String department);

    @Query("select a from FacultySubjectAssignment a join fetch a.subject s join fetch a.faculty f order by a.assignedAt desc")
    List<FacultySubjectAssignment> findAllWithFacultyAndSubjectOrderByAssignedAtDesc();

    @Query(value = """
            select
              fsa.id as id,
              u.full_name as facultyName,
              u.department as facultyDepartment,
              s.program_name as programName,
              s.subject_code as subjectCode,
              s.subject_name as subjectName,
              s.semester as semester,
              s.department as subjectDepartment,
              fsa.assigned_at as assignedAt
            from faculty_subject_assignments fsa
            join users u on u.id = fsa.faculty_id
            join subjects s on s.id = fsa.subject_id
            order by fsa.assigned_at desc
            """, nativeQuery = true)
    List<AssignmentRowView> findAssignmentRows();

    interface AssignmentRowView {
        Long getId();
        String getFacultyName();
        String getFacultyDepartment();
        String getProgramName();
        String getSubjectCode();
        String getSubjectName();
        Integer getSemester();
        String getSubjectDepartment();
        LocalDateTime getAssignedAt();
    }
}
