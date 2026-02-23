package com.example.demo.repository;

import com.example.demo.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByDepartmentIgnoreCaseOrderBySubjectCodeAsc(String department);
    Optional<Subject> findByDepartmentIgnoreCaseAndSubjectCodeIgnoreCase(String department, String subjectCode);
}
