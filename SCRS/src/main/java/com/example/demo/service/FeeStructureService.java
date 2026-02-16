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

@Service
public class FeeStructureService {
    private final FeeStructureRepository feeStructureRepository;
    private final FeeStructureAuditLogRepository feeStructureAuditLogRepository;

    public FeeStructureService(FeeStructureRepository feeStructureRepository,
                               FeeStructureAuditLogRepository feeStructureAuditLogRepository) {
        this.feeStructureRepository = feeStructureRepository;
        this.feeStructureAuditLogRepository = feeStructureAuditLogRepository;
    }

    public List<FeeStructure> getAll() {
        return feeStructureRepository.findAllByOrderByEffectiveFromDesc();
    }

    public List<FeeStructureAuditLog> getRecentAuditLogs() {
        return feeStructureAuditLogRepository.findTop20ByOrderByChangedAtDesc();
    }

    public FeeStructure getById(Long id) {
        return feeStructureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fee structure not found"));
    }

    public FeeStructure getActive() {
        return feeStructureRepository.findFirstByActiveTrueOrderByEffectiveFromDesc().orElse(null);
    }

    @Transactional
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
    public void delete(Long id, String actor) {
        FeeStructure existing = getById(id);
        feeStructureRepository.delete(existing);
        logChange(existing, actor, "DELETE", "Deleted fee structure: " + existing.getName());
    }

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

    private void normalize(FeeStructure feeStructure) {
        if (feeStructure.getEffectiveFrom() == null) {
            feeStructure.setEffectiveFrom(LocalDate.now());
        }
        feeStructure.setCostPerCredit(positive(feeStructure.getCostPerCredit()));
        feeStructure.setLabFee(positive(feeStructure.getLabFee()));
        feeStructure.setDifferentialFee(positive(feeStructure.getDifferentialFee()));
        feeStructure.setLatePenalty(positive(feeStructure.getLatePenalty()));
    }

    private BigDecimal positive(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return value;
    }

    private String buildSummary(FeeStructure feeStructure) {
        return "name=" + feeStructure.getName()
                + ", perCredit=" + feeStructure.getCostPerCredit()
                + ", lab=" + feeStructure.getLabFee()
                + ", differential=" + feeStructure.getDifferentialFee()
                + ", latePenalty=" + feeStructure.getLatePenalty()
                + ", active=" + feeStructure.isActive();
    }

    private void logChange(FeeStructure feeStructure, String actor, String action, String summary) {
        FeeStructureAuditLog log = new FeeStructureAuditLog();
        log.setFeeStructure(feeStructure);
        log.setChangedBy(actor);
        log.setAction(action);
        log.setChangeSummary(summary);
        feeStructureAuditLogRepository.save(log);
    }
}
