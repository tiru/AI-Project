package com.cargo.onerecord.controller;

import com.cargo.onerecord.dto.shipment.ShipmentRequest;
import com.cargo.onerecord.dto.shipment.ShipmentResponse;
import com.cargo.onerecord.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/logistics-objects/shipments")
@RequiredArgsConstructor
@Tag(name = "Shipments", description = "ONE Record Shipment logistics objects")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Create a shipment",
               description = "Creates a new ONE Record Shipment logistics object. Requires ADMIN or OPERATOR role.")
    public ResponseEntity<ShipmentResponse> create(@Valid @RequestBody ShipmentRequest request) {
        ShipmentResponse response = shipmentService.create(request);
        URI location = URI.create("/logistics-objects/shipments/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get shipment by ID")
    public ResponseEntity<ShipmentResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(shipmentService.getById(id));
    }

    @GetMapping
    @Operation(summary = "List all shipments",
               description = "Returns paginated list. Use ?search= for keyword search across goods description, shipper, and consignee.")
    public ResponseEntity<Page<ShipmentResponse>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        PageRequest pageable = PageRequest.of(page, size, sort);

        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(shipmentService.search(search, pageable));
        }
        return ResponseEntity.ok(shipmentService.getAll(pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Update a shipment",
               description = "Replaces shipment data. Increments revision number.")
    public ResponseEntity<ShipmentResponse> update(@PathVariable UUID id,
                                                   @Valid @RequestBody ShipmentRequest request) {
        return ResponseEntity.ok(shipmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Soft-delete a shipment",
               description = "Marks shipment as deleted. ONE Record objects are never hard-deleted. Requires ADMIN role.")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        shipmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}