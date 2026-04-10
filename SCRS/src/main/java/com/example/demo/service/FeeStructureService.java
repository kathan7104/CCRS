/*
 * File: src/main/java/com/example/demo/service/FeeStructureService.java
 * Role: Service
 * MVC Fit: Contains business logic used by controllers.
 * Connects To: Controller calls Service, Service calls Repository
 */

package com.example.demo.service;

import com.example.demo.entity.FeeStructure;
import com.example.demo.entity.FeeStructureAuditLog;
import com.example.demo.repository.FeeStructureAuditLogRepository;
import com.example.demo.repository.FeeStructureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Class Summary: Service class that contains business logic used by controllers.
// @Service marks the business logic layer for Spring to manage as a bean.
@Service
public class FeeStructureService {
// Field: stores feeStructureRepository for this class.
    private final FeeStructureRepository feeStructureRepository;
// Field: stores feeStructureAuditLogRepository for this class.
    private final FeeStructureAuditLogRepository feeStructureAuditLogRepository;

// Constructor: Spring injects dependencies here.
    public FeeStructureService(FeeStructureRepository feeStructureRepository,
                               FeeStructureAuditLogRepository feeStructureAuditLogRepository) {
        this.feeStructureRepository = feeStructureRepository;
        this.feeStructureAuditLogRepository = feeStructureAuditLogRepository;
    }

// Service method: contains business logic and coordinates repositories.
    public List<FeeStructure> getAll() {
        return feeStructureRepository.findAllByOrderByEffectiveFromDesc();
    }

// Service method: contains business logic and coordinates repositories.
    public List<FeeStructureAuditLog> getRecentAuditLogs() {
        return feeStructureAuditLogRepository.findTop20ByOrderByChangedAtDesc();
    }

// Service method: contains business logic and coordinates repositories.
    public FeeStructure getById(Long id) {
        return feeStructureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fee structure not found"));
    }

// Service method: contains business logic and coordinates repositories.
    public FeeStructure getActive() {
        return feeStructureRepository.findFirstByActiveTrueOrderByEffectiveFromDesc().orElse(null);
    }

    @Transactional
// Service method: contains business logic and coordinates repositories.
    public FeeStructure createOrUpdate(FeeStructure feeStructure, String actor, String action) {
        normalize(feeStructure);
        if (feeStructure.isActive()) {
            deactivateOthers(feeStructure.getId());
        }
        FeeStructure saved = feeStructureRepository.save(feeStructure);
        logChange(saved, actor, action, buildSummary(saved));
        return saved;
    }

    @Transactional
// Service method: contains business logic and coordinates repositories.
    public void delete(Long id, String actor) {
        FeeStructure existing = getById(id);
        feeStructureRepository.delete(existing);
        logChange(existing, actor, "DELETE", "Deleted fee structure: " + existing.getName());
    }

// Service method: contains business logic and coordinates repositories.
    private void deactivateOthers(Long currentId) {
        List<FeeStructure> all = feeStructureRepository.findAll();
        for (FeeStructure candidate : all) {
            if (currentId != null && currentId.equals(candidate.getId())) {
                continue;
            }
            if (candidate.isActive()) {
                candidate.setActive(false);
                feeStructureRepository.save(candidate);
            }
        }
    }

// Service method: contains business logic and coordinates repositories.
    private void normalize(FeeStructure feeStructure) {
        if (feeStructure.getEffectiveFrom() == null) {
            feeStructure.setEffectiveFrom(LocalDate.now());
        }
        feeStructure.setCostPerCredit(positive(feeStructure.getCostPerCredit()));
        feeStructure.setLabFee(positive(feeStructure.getLabFee()));
        feeStructure.setDifferentialFee(positive(feeStructure.getDifferentialFee()));
        feeStructure.setLatePenalty(positive(feeStructure.getLatePenalty()));
    }

// Service method: contains business logic and coordinates repositories.
    private BigDecimal positive(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return value;
    }

// Service method: contains business logic and coordinates repositories.
    private String buildSummary(FeeStructure feeStructure) {
        return "name=" + feeStructure.getName()
                + ", perCredit=" + feeStructure.getCostPerCredit()
                + ", lab=" + feeStructure.getLabFee()
                + ", differential=" + feeStructure.getDifferentialFee()
                + ", latePenalty=" + feeStructure.getLatePenalty()
                + ", active=" + feeStructure.isActive();
    }

// Service method: contains business logic and coordinates repositories.
    private void logChange(FeeStructure feeStructure, String actor, String action, String summary) {
        FeeStructureAuditLog log = new FeeStructureAuditLog();
        log.setFeeStructure(feeStructure);
        log.setChangedBy(actor);
        log.setAction(action);
        log.setChangeSummary(summary);
        feeStructureAuditLogRepository.save(log);
    }
}
