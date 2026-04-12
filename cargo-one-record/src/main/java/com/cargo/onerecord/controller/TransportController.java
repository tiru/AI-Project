package com.cargo.onerecord.controller;

import com.cargo.onerecord.dto.transport.*;
import com.cargo.onerecord.service.TransportService;
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
@Tag(name = "Transport", description = "ONE Record TransportMeans and TransportSegments — flight/vehicle data")
public class TransportController {

    private final TransportService transportService;

    // ---- TransportMeans ----

    @PostMapping("/logistics-objects/transport-means")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Register an aircraft or vehicle",
               description = "Transport modes: AIRCRAFT, TRUCK, VESSEL, RAIL, OTHER")
    public ResponseEntity<TransportMeansResponse> createMeans(@Valid @RequestBody TransportMeansRequest request) {
        TransportMeansResponse response = transportService.createMeans(request);
        return ResponseEntity.created(URI.create("/logistics-objects/transport-means/" + response.getId()))
                .body(response);
    }

    @GetMapping("/logistics-objects/transport-means/{id}")
    @Operation(summary = "Get transport means by ID")
    public ResponseEntity<TransportMeansResponse> getMeans(@PathVariable UUID id) {
        return ResponseEntity.ok(transportService.getMeansById(id));
    }

    @GetMapping("/logistics-objects/transport-means")
    @Operation(summary = "List all transport means",
               description = "Filter by ?carrierCode=EK to get all aircraft for a carrier")
    public ResponseEntity<?> getAllMeans(
            @RequestParam(required = false) String carrierCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (carrierCode != null && !carrierCode.isBlank()) {
            return ResponseEntity.ok(transportService.getByCarrier(carrierCode));
        }
        Page<TransportMeansResponse> result = transportService.getAllMeans(
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(result);
    }

    @PutMapping("/logistics-objects/transport-means/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Update transport means")
    public ResponseEntity<TransportMeansResponse> updateMeans(@PathVariable UUID id,
                                                              @Valid @RequestBody TransportMeansRequest request) {
        return ResponseEntity.ok(transportService.updateMeans(id, request));
    }

    @DeleteMapping("/logistics-objects/transport-means/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft-delete transport means")
    public ResponseEntity<Void> deleteMeans(@PathVariable UUID id) {
        transportService.deleteMeans(id);
        return ResponseEntity.noContent().build();
    }

    // ---- TransportSegments ----

    @PostMapping("/logistics-objects/transport-segments")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Create a transport segment (flight leg)",
               description = "One leg of the shipment journey. Link to a Shipment via shipmentId.")
    public ResponseEntity<TransportSegmentResponse> createSegment(@Valid @RequestBody TransportSegmentRequest request) {
        TransportSegmentResponse response = transportService.createSegment(request);
        return ResponseEntity.created(URI.create("/logistics-objects/transport-segments/" + response.getId()))
                .body(response);
    }

    @GetMapping("/logistics-objects/transport-segments/{id}")
    @Operation(summary = "Get transport segment by ID")
    public ResponseEntity<TransportSegmentResponse> getSegment(@PathVariable UUID id) {
        return ResponseEntity.ok(transportService.getSegmentById(id));
    }

    @GetMapping("/logistics-objects/transport-segments/shipment/{shipmentId}")
    @Operation(summary = "Get all transport segments for a shipment (ordered by sequence)")
    public ResponseEntity<List<TransportSegmentResponse>> getByShipment(@PathVariable UUID shipmentId) {
        return ResponseEntity.ok(transportService.getSegmentsByShipment(shipmentId));
    }

    @PatchMapping("/logistics-objects/transport-segments/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Update segment status",
               description = "Statuses: PLANNED, CONFIRMED, BOARDED, DEPARTED, ARRIVED, CANCELLED")
    public ResponseEntity<TransportSegmentResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam String status) {
        return ResponseEntity.ok(transportService.updateSegmentStatus(id, status));
    }

    @DeleteMapping("/logistics-objects/transport-segments/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft-delete a transport segment")
    public ResponseEntity<Void> deleteSegment(@PathVariable UUID id) {
        transportService.deleteSegment(id);
        return ResponseEntity.noContent().build();
    }
}