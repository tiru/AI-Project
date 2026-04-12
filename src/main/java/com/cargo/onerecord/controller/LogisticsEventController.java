package com.cargo.onerecord.controller;

import com.cargo.onerecord.dto.event.LogisticsEventRequest;
import com.cargo.onerecord.dto.event.LogisticsEventResponse;
import com.cargo.onerecord.service.LogisticsEventService;
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
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Logistics Events", description = "ONE Record LogisticsEvents — shipment status milestones")
public class LogisticsEventController {

    private final LogisticsEventService eventService;

    /**
     * ONE Record standard path:
     * POST /logistics-objects/{id}/logistics-events
     */
    @PostMapping("/logistics-objects/{logisticsObjectId}/logistics-events")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Add a logistics event to a logistics object",
               description = """
                       Common IATA event codes:
                       - RCS — Received from Shipper
                       - DEP — Departed
                       - ARR — Arrived
                       - RCF — Received from Flight
                       - DLV — Delivered to Consignee
                       - NFD — Notified
                       - AWR — Arrived at Warehouse
                       - TFD — Transferred
                       - FOH — Freight on Hand
                       - CCD — Customs Cleared
                       - MAN — Manifested
                       - PRE — Prepared for Loading
                       """)
    public ResponseEntity<LogisticsEventResponse> create(
            @PathVariable UUID logisticsObjectId,
            @Valid @RequestBody LogisticsEventRequest request) {

        request.setLogisticsObjectId(logisticsObjectId);
        LogisticsEventResponse response = eventService.create(request);
        URI location = URI.create("/logistics-objects/" + logisticsObjectId
                + "/logistics-events/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    /**
     * ONE Record standard path:
     * GET /logistics-objects/{id}/logistics-events
     */
    @GetMapping("/logistics-objects/{logisticsObjectId}/logistics-events")
    @Operation(summary = "Get all logistics events for a logistics object",
               description = "Returns all events ordered by event date descending. Filter by ?eventCode=DEP")
    public ResponseEntity<?> getEvents(
            @PathVariable UUID logisticsObjectId,
            @RequestParam(required = false) String eventCode,
            @RequestParam(defaultValue = "false") boolean paged,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (eventCode != null && !eventCode.isBlank()) {
            List<LogisticsEventResponse> events = eventService.getByEventCode(logisticsObjectId, eventCode);
            return ResponseEntity.ok(events);
        }
        if (paged) {
            Page<LogisticsEventResponse> events = eventService.getByLogisticsObjectPaged(
                    logisticsObjectId, PageRequest.of(page, size, Sort.by("eventDate").descending()));
            return ResponseEntity.ok(events);
        }
        return ResponseEntity.ok(eventService.getByLogisticsObject(logisticsObjectId));
    }

    /**
     * ONE Record standard path:
     * GET /logistics-objects/{id}/logistics-events/{eventId}
     */
    @GetMapping("/logistics-objects/{logisticsObjectId}/logistics-events/{eventId}")
    @Operation(summary = "Get a specific logistics event by ID")
    public ResponseEntity<LogisticsEventResponse> getById(
            @PathVariable UUID logisticsObjectId,
            @PathVariable UUID eventId) {
        return ResponseEntity.ok(eventService.getById(eventId));
    }

    /** Convenience: get the latest/current status of a logistics object */
    @GetMapping("/logistics-objects/{logisticsObjectId}/latest-event")
    @Operation(summary = "Get the latest event (current status) of a logistics object")
    public ResponseEntity<LogisticsEventResponse> getLatest(@PathVariable UUID logisticsObjectId) {
        return ResponseEntity.ok(eventService.getLatestEvent(logisticsObjectId));
    }
}