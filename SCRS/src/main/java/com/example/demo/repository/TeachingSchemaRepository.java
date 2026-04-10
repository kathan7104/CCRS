/*
 * File: src/main/java/com/example/demo/repository/TeachingSchemaRepository.java
 * Role: Repository
 * MVC Fit: Data access layer using Spring Data JPA.
 * Connects To: Service uses Repository for CRUD queries
 */

package com.example.demo.repository;

import com.example.demo.entity.TeachingSchema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Class Summary: Repository class that is the data access layer using Spring Data JPA.
public interface TeachingSchemaRepository extends JpaRepository<TeachingSchema, Long> {
    List<TeachingSchema> findByDepartmentIgnoreCaseOrderByProgramNameAscSchemaVersionDesc(String department);
    List<TeachingSchema> findByDepartmentIgnoreCaseAndProgramNameIgnoreCaseOrderBySchemaVersionDesc(String department, String programName);
    Optional<TeachingSchema> findTopByDepartmentIgnoreCaseAndProgramNameIgnoreCaseOrderBySchemaVersionDesc(String department, String programName);
    Optional<TeachingSchema> findByFilePath(String filePath);
}
