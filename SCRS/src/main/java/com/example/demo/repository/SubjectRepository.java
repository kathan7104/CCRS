/*
 * File: src/main/java/com/example/demo/repository/SubjectRepository.java
 * Role: Repository
 * MVC Fit: Data access layer using Spring Data JPA.
 * Connects To: Service uses Repository for CRUD queries
 */

package com.example.demo.repository;

import com.example.demo.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Class Summary: Repository class that is the data access layer using Spring Data JPA.
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByDepartmentIgnoreCaseOrderBySubjectCodeAsc(String department);
    List<Subject> findByDepartmentIgnoreCaseAndTeachingSchemaIsNotNullOrderByProgramNameAscSemesterAscSubjectCodeAsc(String department);
    List<Subject> findByTeachingSchemaId(Long teachingSchemaId);
    boolean existsByTeachingSchemaId(Long teachingSchemaId);
    Optional<Subject> findByDepartmentIgnoreCaseAndSubjectCodeIgnoreCase(String department, String subjectCode);
}
