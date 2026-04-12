package com.cargo.onerecord.service;

import com.cargo.onerecord.dto.action.ChangeRequestRequest;
import com.cargo.onerecord.dto.action.ChangeRequestResponse;
import com.cargo.onerecord.dto.action.ChangeRequestReview;
import com.cargo.onerecord.exception.ResourceNotFoundException;
import com.cargo.onerecord.model.action.AuditTrailEntry;
import com.cargo.onerecord.model.action.ChangeRequest;
import com.cargo.onerecord.repository.AuditTrailRepository;
import com.cargo.onerecord.repository.ChangeRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChangeRequestService {

    private final ChangeRequestRepository changeRequestRepository;
    private final AuditTrailRepository auditTrailRepository;

    @Value("${one-record.server.company-identifier}")
    private String companyIdentifier;

    @Transactional
    public ChangeRequestResponse submit(ChangeRequestRequest request) {
        String currentUser = getCurrentUsername();

        ChangeRequest cr = ChangeRequest.builder()
                .targetObjectId(request.getTargetObjectId())
                .targetObjectType(request.getTargetObjectType())
                .fieldPath(request.getFieldPath())
                .newValue(request.getNewValue())
                .jsonPatch(request.getJsonPatch())
                .reason(request.getReason())
                .status(ChangeRequest.RequestStatus.PENDING)
                .requestedBy(currentUser)
                .build();

        cr.setCompanyIdentifier(companyIdentifier);
        return toResponse(changeRequestRepository.save(cr));
    }

    @Transactional(readOnly = true)
    public ChangeRequestResponse getById(UUID id) {
        return toResponse(changeRequestRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChangeRequest", id)));
    }

    @Transactional(readOnly = true)
    public Page<ChangeRequestResponse> getByTargetObject(UUID targetObjectId, Pageable pageable) {
        return changeRequestRepository
                .findByTargetObjectIdOrderByRequestedAtDesc(targetObjectId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ChangeRequestResponse> getByStatus(String status, Pageable pageable) {
        ChangeRequest.RequestStatus requestStatus = ChangeRequest.RequestStatus.valueOf(status.toUpperCase());
        return changeRequestRepository
                .findByStatusOrderByRequestedAtDesc(requestStatus, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public ChangeRequestResponse review(UUID id, ChangeRequestReview review) {
        ChangeRequest cr = changeRequestRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChangeRequest", id));

        if (cr.getStatus() != ChangeRequest.RequestStatus.PENDING) {
            throw new IllegalArgumentException(
                    "ChangeRequest is already " + cr.getStatus() + " — cannot review again");
        }

        String decision = review.getDecision().toUpperCase();
        if (!decision.equals("APPROVED") && !decision.equals("REJECTED")) {
            throw new IllegalArgumentException("Decision must be APPROVED or REJECTED");
        }

        String reviewer = getCurrentUsername();
        cr.setStatus(ChangeRequest.RequestStatus.valueOf(decision));
        cr.setReviewedBy(reviewer);
        cr.setReviewedAt(OffsetDateTime.now());
        cr.setReviewNotes(review.getReviewNotes());

        ChangeRequest saved = changeRequestRepository.save(cr);

        // If APPROVED, record in audit trail
        if (saved.getStatus() == ChangeRequest.RequestStatus.APPROVED) {
            recordAuditEntry(saved, reviewer);
        }

        return toResponse(saved);
    }

    @Transactional
    public ChangeRequestResponse revoke(UUID id) {
        ChangeRequest cr = changeRequestRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChangeRequest", id));

        if (cr.getStatus() != ChangeRequest.RequestStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING requests can be revoked");
        }

        String currentUser = getCurrentUsername();
        if (!cr.getRequestedBy().equals(currentUser)) {
            throw new IllegalArgumentException("Only the original requester can revoke this request");
        }

        cr.setStatus(ChangeRequest.RequestStatus.REVOKED);
        cr.setReviewedAt(OffsetDateTime.now());
        return toResponse(changeRequestRepository.save(cr));
    }

    // --- Private helpers ---

    private void recordAuditEntry(ChangeRequest cr, String reviewer) {
        AuditTrailEntry entry = AuditTrailEntry.builder()
                .logisticsObjectId(cr.getTargetObjectId())
                .logisticsObjectType(cr.getTargetObjectType())
                .revision(1) // Will be updated by caller if needed
                .fieldPath(cr.getFieldPath())
                .oldValue(cr.getOldValue())
                .newValue(cr.getNewValue())
                .jsonPatch(cr.getJsonPatch())
                .operation("UPDATE")
                .changeRequestId(cr.getId())
                .changedBy(reviewer)
                .reason(cr.getReason())
                .build();
        auditTrailRepository.save(entry);
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }

    public ChangeRequestResponse toResponse(ChangeRequest cr) {
        return ChangeRequestResponse.builder()
                .type("api:ChangeRequest")
                .id(cr.getId())
                .targetObjectId(cr.getTargetObjectId())
                .targetObjectType(cr.getTargetObjectType())
                .fieldPath(cr.getFieldPath())
                .oldValue(cr.getOldValue())
                .newValue(cr.getNewValue())
                .jsonPatch(cr.getJsonPatch())
                .reason(cr.getReason())
                .status(cr.getStatus().name())
                .requestedBy(cr.getRequestedBy())
                .requestedAt(cr.getRequestedAt())
                .reviewedBy(cr.getReviewedBy())
                .reviewedAt(cr.getReviewedAt())
                .reviewNotes(cr.getReviewNotes())
                .build();
    }
}