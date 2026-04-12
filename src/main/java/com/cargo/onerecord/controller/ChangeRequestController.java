package com.cargo.onerecord.controller;

import com.cargo.onerecord.dto.action.ChangeRequestRequest;
import com.cargo.onerecord.dto.action.ChangeRequestResponse;
import com.cargo.onerecord.dto.action.ChangeRequestReview;
import com.cargo.onerecord.service.ChangeRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Change Requests", description = "ONE Record ChangeRequests — controlled update workflow for logistics objects")
public class ChangeRequestController {

    private final ChangeRequestService changeRequestService;

    /**
     * ONE Record standard: PATCH /logistics-objects/{id}
     * Submits a ChangeRequest (does not apply changes directly).
     */
    @PostMapping("/action-requests/change-requests")
    @Operation(summary = "Submit a change request",
               description = """
                       Submits a request to modify a logistics object.
                       The owner (ADMIN) must APPROVE before the change is applied.

                       Simple change: provide fieldPath + newValue.
                       Complex change: provide jsonPatch (RFC 6902 JSON Patch format).
                       Example jsonPatch: [{"op":"replace","path":"/goodsDescription","value":"Updated goods"}]
                       """)
    public ResponseEntity<ChangeRequestResponse> submit(@Valid @RequestBody ChangeRequestRequest request) {
        ChangeRequestResponse response = changeRequestService.submit(request);
        URI location = URI.create("/action-requests/change-requests/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/action-requests/change-requests/{id}")
    @Operation(summary = "Get change request by ID")
    public ResponseEntity<ChangeRequestResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(changeRequestService.getById(id));
    }

    @GetMapping("/action-requests/change-requests")
    @Operation(summary = "List change requests",
               description = "Filter by ?status=PENDING | APPROVED | REJECTED | REVOKED. Filter by ?targetObjectId=<uuid>")
    public ResponseEntity<Page<ChangeRequestResponse>> getAll(
            @RequestParam(required = false) UUID targetObjectId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("requestedAt").descending());

        if (targetObjectId != null) {
            return ResponseEntity.ok(changeRequestService.getByTargetObject(targetObjectId, pageable));
        }
        if (status != null && !status.isBlank()) {
            return ResponseEntity.ok(changeRequestService.getByStatus(status, pageable));
        }
        return ResponseEntity.ok(changeRequestService.getByStatus("PENDING", pageable));
    }

    /**
     * ONE Record standard: PATCH /action-requests/{id}
     * Approve or Reject a change request.
     */
    @PatchMapping("/action-requests/change-requests/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve or reject a change request",
               description = "Decision must be APPROVED or REJECTED. Only ADMIN can review. On APPROVED, audit trail entry is created.")
    public ResponseEntity<ChangeRequestResponse> review(@PathVariable UUID id,
                                                        @Valid @RequestBody ChangeRequestReview review) {
        return ResponseEntity.ok(changeRequestService.review(id, review));
    }

    /** Requester can revoke their own pending request */
    @PatchMapping("/action-requests/change-requests/{id}/revoke")
    @Operation(summary = "Revoke a pending change request",
               description = "Only the original requester can revoke. Only PENDING requests can be revoked.")
    public ResponseEntity<ChangeRequestResponse> revoke(@PathVariable UUID id) {
        return ResponseEntity.ok(changeRequestService.revoke(id));
    }
}