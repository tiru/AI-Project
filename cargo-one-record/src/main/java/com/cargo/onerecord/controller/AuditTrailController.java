package com.cargo.onerecord.controller;

import com.cargo.onerecord.dto.action.AuditTrailResponse;
import com.cargo.onerecord.service.AuditTrailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Audit Trail", description = "ONE Record AuditTrail — immutable version history of every logistics object")
public class AuditTrailController {

    private final AuditTrailService auditTrailService;

    /**
     * ONE Record standard path:
     * GET /logistics-objects/{id}/audit-trail
     */
    @GetMapping("/logistics-objects/{logisticsObjectId}/audit-trail")
    @Operation(summary = "Get audit trail for a logistics object",
               description = "Returns full version history ordered by revision descending. Use ?revision=N to get a specific revision.")
    public ResponseEntity<?> getAuditTrail(
            @PathVariable UUID logisticsObjectId,
            @RequestParam(required = false) Integer revision,
            @RequestParam(defaultValue = "false") boolean paged,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (revision != null) {
            List<AuditTrailResponse> entries = auditTrailService.getByRevision(logisticsObjectId, revision);
            return ResponseEntity.ok(entries);
        }
        if (paged) {
            Page<AuditTrailResponse> entries = auditTrailService.getByLogisticsObjectPaged(
                    logisticsObjectId, PageRequest.of(page, size, Sort.by("revision").descending()));
            return ResponseEntity.ok(entries);
        }
        return ResponseEntity.ok(auditTrailService.getByLogisticsObject(logisticsObjectId));
    }
}