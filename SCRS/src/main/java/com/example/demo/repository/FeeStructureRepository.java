package com.example.demo.repository;

import com.example.demo.entity.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {
    Optional<FeeStructure> findFirstByActiveTrueOrderByEffectiveFromDesc();
    List<FeeStructure> findAllByOrderByEffectiveFromDesc();
}
