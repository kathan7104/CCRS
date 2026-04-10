/*
 * File: src/main/java/com/example/demo/repository/EnrollmentDocumentRepository.java
 * Role: Repository
 * MVC Fit: Data access layer using Spring Data JPA.
 * Connects To: Service uses Repository for CRUD queries
 */

package com.example.demo.repository;

import com.example.demo.entity.EnrollmentDocument;
import org.springframework.data.jpa.repository.JpaRepository;

// Class Summary: Repository class that is the data access layer using Spring Data JPA.
public interface EnrollmentDocumentRepository extends JpaRepository<EnrollmentDocument, Long> {
}
