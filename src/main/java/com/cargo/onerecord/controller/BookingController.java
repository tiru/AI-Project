package com.cargo.onerecord.controller;

import com.cargo.onerecord.dto.booking.*;
import com.cargo.onerecord.service.BookingService;
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
@Tag(name = "Bookings", description = "ONE Record Booking and BookingRequest — cargo capacity booking workflow")
public class BookingController {

    private final BookingService bookingService;

    // ---- BookingRequest ----

    @PostMapping("/logistics-objects/booking-requests")
    @Operation(summary = "Submit a booking request",
               description = "Submit a cargo booking request to the carrier. Status starts as PENDING.")
    public ResponseEntity<BookingRequestResponse> submitRequest(@Valid @RequestBody BookingRequestDto dto) {
        BookingRequestResponse response = bookingService.submitRequest(dto);
        return ResponseEntity.created(URI.create("/logistics-objects/booking-requests/" + response.getId()))
                .body(response);
    }

    @GetMapping("/logistics-objects/booking-requests/{id}")
    @Operation(summary = "Get booking request by ID")
    public ResponseEntity<BookingRequestResponse> getRequest(@PathVariable UUID id) {
        return ResponseEntity.ok(bookingService.getRequestById(id));
    }

    @GetMapping("/logistics-objects/booking-requests")
    @Operation(summary = "List booking requests",
               description = "Filter by ?status=PENDING | OFFERED | CONFIRMED | CANCELLED | REJECTED")
    public ResponseEntity<Page<BookingRequestResponse>> getAllRequests(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookingService.getAllRequests(status,
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @PatchMapping("/logistics-objects/booking-requests/{id}/cancel")
    @Operation(summary = "Cancel a booking request")
    public ResponseEntity<Void> cancelRequest(@PathVariable UUID id,
                                              @RequestParam(required = false) String reason) {
        bookingService.cancelRequest(id, reason);
        return ResponseEntity.noContent().build();
    }

    // ---- Booking ----

    @PostMapping("/logistics-objects/bookings")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Confirm a booking",
               description = "Creates a confirmed booking. Automatically updates the linked BookingRequest status to CONFIRMED.")
    public ResponseEntity<BookingResponse> confirmBooking(@Valid @RequestBody BookingDto dto) {
        BookingResponse response = bookingService.confirmBooking(dto);
        return ResponseEntity.created(URI.create("/logistics-objects/bookings/" + response.getId()))
                .body(response);
    }

    @GetMapping("/logistics-objects/bookings/{id}")
    @Operation(summary = "Get booking by ID")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable UUID id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/logistics-objects/bookings/number/{bookingNumber}")
    @Operation(summary = "Get booking by booking number")
    public ResponseEntity<BookingResponse> getBookingByNumber(@PathVariable String bookingNumber) {
        return ResponseEntity.ok(bookingService.getBookingByNumber(bookingNumber));
    }

    @GetMapping("/logistics-objects/bookings")
    @Operation(summary = "List bookings",
               description = "Filter by ?status=CONFIRMED | CANCELLED | COMPLETED or ?carrierCode=EK")
    public ResponseEntity<Page<BookingResponse>> getAllBookings(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String carrierCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookingService.getAllBookings(status, carrierCode,
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @PatchMapping("/logistics-objects/bookings/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Cancel a confirmed booking")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable UUID id,
                                                         @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(bookingService.cancelBooking(id, reason));
    }
}