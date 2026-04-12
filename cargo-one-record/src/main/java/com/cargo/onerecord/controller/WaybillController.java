package com.cargo.onerecord.controller;

import com.cargo.onerecord.dto.shipment.WaybillRequest;
import com.cargo.onerecord.dto.shipment.WaybillResponse;
import com.cargo.onerecord.service.WaybillService;
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
@RequestMapping("/logistics-objects/waybills")
@RequiredArgsConstructor
@Tag(name = "Waybills", description = "ONE Record Air Waybill logistics objects")
public class WaybillController {

    private final WaybillService waybillService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Create an Air Waybill (AWB)",
               description = "Waybill number must be in IATA format NNN-NNNNNNNN (e.g. 180-12345678).")
    public ResponseEntity<WaybillResponse> create(@Valid @RequestBody WaybillRequest request) {
        WaybillResponse response = waybillService.create(request);
        URI location = URI.create("/logistics-objects/waybills/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get waybill by ID")
    public ResponseEntity<WaybillResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(waybillService.getById(id));
    }

    @GetMapping("/number/{waybillNumber}")
    @Operation(summary = "Get waybill by AWB number",
               description = "Lookup by the 11-digit IATA AWB number (e.g. 180-12345678).")
    public ResponseEntity<WaybillResponse> getByNumber(@PathVariable String waybillNumber) {
        return ResponseEntity.ok(waybillService.getByWaybillNumber(waybillNumber));
    }

    @GetMapping
    @Operation(summary = "List all waybills",
               description = "Filter by ?carrierCode= or ?origin= and ?destination= query params.")
    public ResponseEntity<Page<WaybillResponse>> getAll(
            @RequestParam(required = false) String carrierCode,
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (carrierCode != null && !carrierCode.isBlank()) {
            return ResponseEntity.ok(waybillService.getByCarrier(carrierCode, pageable));
        }
        if (origin != null && destination != null) {
            return ResponseEntity.ok(waybillService.getByRoute(origin, destination, pageable));
        }
        return ResponseEntity.ok(waybillService.getAll(pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Update an Air Waybill")
    public ResponseEntity<WaybillResponse> update(@PathVariable UUID id,
                                                  @Valid @RequestBody WaybillRequest request) {
        return ResponseEntity.ok(waybillService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft-delete a waybill")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        waybillService.delete(id);
        return ResponseEntity.noContent().build();
    }
}