package com.cargo.onerecord.service;

import com.cargo.onerecord.dto.action.AuditTrailResponse;
import com.cargo.onerecord.model.action.AuditTrailEntry;
import com.cargo.onerecord.repository.AuditTrailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditTrailService {

    private final AuditTrailRepository auditTrailRepository;

    /**
     * Called automatically on CREATE operations to seed the audit trail.
     */
    @Transactional
    public void recordCreate(UUID logisticsObjectId, String logisticsObjectType,
                             String createdBy, String initialValue) {
        AuditTrailEntry entry = AuditTrailEntry.builder()
                .logisticsObjectId(logisticsObjectId)
                .logisticsObjectType(logisticsObjectType)
                .revision(1)
                .operation("CREATE")
                .newValue(initialValue)
                .changedBy(createdBy)
                .build();
        auditTrailRepository.save(entry);
    }

    @Transactional(readOnly = true)
    public List<AuditTrailResponse> getByLogisticsObject(UUID logisticsObjectId) {
        return auditTrailRepository
                .findByLogisticsObjectIdOrderByRevisionDesc(logisticsObjectId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<AuditTrailResponse> getByLogisticsObjectPaged(UUID logisticsObjectId, Pageable pageable) {
        return auditTrailRepository
                .findByLogisticsObjectIdOrderByRevisionDesc(logisticsObjectId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<AuditTrailResponse> getByRevision(UUID logisticsObjectId, Integer revision) {
        return auditTrailRepository
                .findByLogisticsObjectIdAndRevision(logisticsObjectId, revision)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public AuditTrailResponse toResponse(AuditTrailEntry e) {
        return AuditTrailResponse.builder()
                .type("api:AuditTrail")
                .id(e.getId())
                .logisticsObjectId(e.getLogisticsObjectId())
                .logisticsObjectType(e.getLogisticsObjectType())
                .revision(e.getRevision())
                .fieldPath(e.getFieldPath())
                .oldValue(e.getOldValue())
                .newValue(e.getNewValue())
                .jsonPatch(e.getJsonPatch())
                .operation(e.getOperation())
                .changeRequestId(e.getChangeRequestId())
                .changedBy(e.getChangedBy())
                .reason(e.getReason())
                .changedAt(e.getChangedAt())
                .build();
    }
}