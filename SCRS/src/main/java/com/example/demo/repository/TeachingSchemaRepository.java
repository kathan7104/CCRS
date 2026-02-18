package com.example.demo.repository;

import com.example.demo.entity.TeachingSchema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeachingSchemaRepository extends JpaRepository<TeachingSchema, Long> {
    List<TeachingSchema> findByDepartmentIgnoreCaseOrderByProgramNameAscSchemaVersionDesc(String department);
    List<TeachingSchema> findByDepartmentIgnoreCaseAndProgramNameIgnoreCaseOrderBySchemaVersionDesc(String department, String programName);
    Optional<TeachingSchema> findTopByDepartmentIgnoreCaseAndProgramNameIgnoreCaseOrderBySchemaVersionDesc(String department, String programName);
}
