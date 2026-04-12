package com.cargo.onerecord.controller;

import com.cargo.onerecord.dto.shipment.PieceRequest;
import com.cargo.onerecord.dto.shipment.PieceResponse;
import com.cargo.onerecord.service.PieceService;
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
@RequestMapping("/logistics-objects/pieces")
@RequiredArgsConstructor
@Tag(name = "Pieces", description = "ONE Record Piece logistics objects")
public class PieceController {

    private final PieceService pieceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Create a piece",
               description = "Creates a new ONE Record Piece. Optionally links to a Shipment via shipmentId.")
    public ResponseEntity<PieceResponse> create(@Valid @RequestBody PieceRequest request) {
        PieceResponse response = pieceService.create(request);
        URI location = URI.create("/logistics-objects/pieces/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get piece by ID")
    public ResponseEntity<PieceResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(pieceService.getById(id));
    }

    @GetMapping
    @Operation(summary = "List all pieces (paginated)")
    public ResponseEntity<Page<PieceResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                pieceService.getAll(PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @GetMapping("/shipment/{shipmentId}")
    @Operation(summary = "Get all pieces for a shipment")
    public ResponseEntity<List<PieceResponse>> getByShipment(@PathVariable UUID shipmentId) {
        return ResponseEntity.ok(pieceService.getByShipment(shipmentId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Update a piece")
    public ResponseEntity<PieceResponse> update(@PathVariable UUID id,
                                                @Valid @RequestBody PieceRequest request) {
        return ResponseEntity.ok(pieceService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft-delete a piece")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        pieceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}