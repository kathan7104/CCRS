/*
 * File: src/main/java/com/example/demo/repository/FeeStructureRepository.java
 * Role: Repository
 * MVC Fit: Data access layer using Spring Data JPA.
 * Connects To: Service uses Repository for CRUD queries
 */

package com.example.demo.repository;

import com.example.demo.entity.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Class Summary: Repository class that is the data access layer using Spring Data JPA.
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {
    Optional<FeeStructure> findFirstByActiveTrueOrderByEffectiveFromDesc();
    List<FeeStructure> findAllByOrderByEffectiveFromDesc();
}
