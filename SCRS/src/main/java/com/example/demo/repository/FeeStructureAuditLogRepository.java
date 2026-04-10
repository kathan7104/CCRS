/*
 * File: src/main/java/com/example/demo/repository/FeeStructureAuditLogRepository.java
 * Role: Repository
 * MVC Fit: Data access layer using Spring Data JPA.
 * Connects To: Service uses Repository for CRUD queries
 */

package com.example.demo.repository;

import com.example.demo.entity.FeeStructureAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Class Summary: Repository class that is the data access layer using Spring Data JPA.
public interface FeeStructureAuditLogRepository extends JpaRepository<FeeStructureAuditLog, Long> {
    List<FeeStructureAuditLog> findTop20ByOrderByChangedAtDesc();
}
