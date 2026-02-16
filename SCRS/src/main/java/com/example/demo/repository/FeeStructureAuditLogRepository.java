package com.example.demo.repository;

import com.example.demo.entity.FeeStructureAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeeStructureAuditLogRepository extends JpaRepository<FeeStructureAuditLog, Long> {
    List<FeeStructureAuditLog> findTop20ByOrderByChangedAtDesc();
}
