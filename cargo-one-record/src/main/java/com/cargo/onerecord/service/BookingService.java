package com.cargo.onerecord.service;

import com.cargo.onerecord.dto.booking.*;
import com.cargo.onerecord.dto.kafka.CargoKafkaEvent;
import com.cargo.onerecord.dto.transport.TransportSegmentResponse;
import com.cargo.onerecord.exception.ResourceNotFoundException;
import com.cargo.onerecord.kafka.CargoEventProducer;
import com.cargo.onerecord.kafka.KafkaTopicConfig;
import com.cargo.onerecord.model.booking.Booking;
import com.cargo.onerecord.model.booking.BookingRequest;
import com.cargo.onerecord.repository.BookingRepository;
import com.cargo.onerecord.repository.BookingRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRequestRepository bookingRequestRepository;
    private final BookingRepository bookingRepository;
    private final TransportService transportService;
    private final CargoEventProducer eventProducer;

    @Value("${one-record.server.company-identifier}")
    private String companyIdentifier;

    // --- BookingRequest ---

    @Transactional
    public BookingRequestResponse submitRequest(BookingRequestDto dto) {
        if (dto.getBookingRef() != null && bookingRequestRepository.existsByBookingRef(dto.getBookingRef())) {
            throw new IllegalArgumentException("Booking ref already exists: " + dto.getBookingRef());
        }
        BookingRequest request = BookingRequest.builder()
                .bookingRef(dto.getBookingRef())
                .originCode(dto.getOriginCode().toUpperCase())
                .destinationCode(dto.getDestinationCode().toUpperCase())
                .requestedDeparture(dto.getRequestedDeparture())
                .requestedArrival(dto.getRequestedArrival())
                .goodsDescription(dto.getGoodsDescription())
                .totalPieces(dto.getTotalPieces())
                .totalWeightKg(dto.getTotalWeightKg())
                .totalVolumeCbm(dto.getTotalVolumeCbm())
                .specialHandlingCodes(dto.getSpecialHandlingCodes())
                .shipperName(dto.getShipperName())
                .consigneeName(dto.getConsigneeName())
                .agentName(dto.getAgentName())
                .preferredCarrierCode(dto.getPreferredCarrierCode())
                .status(BookingRequest.BookingStatus.PENDING)
                .build();
        request.setCompanyIdentifier(companyIdentifier);
        BookingRequest saved = bookingRequestRepository.save(request);
        BookingRequestResponse response = toRequestResponse(saved);

        eventProducer.publish(KafkaTopicConfig.TOPIC_BOOKINGS,
                eventProducer.buildEvent("BOOKING_REQUEST_SUBMITTED",
                        saved.getId().toString(), "cargo:BookingRequest",
                        saved.getLogisticsObjectRef(), "PENDING",
                        currentUser(), response));
        return response;
    }

    @Transactional(readOnly = true)
    public BookingRequestResponse getRequestById(UUID id) {
        return toRequestResponse(bookingRequestRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("BookingRequest", id)));
    }

    @Transactional(readOnly = true)
    public Page<BookingRequestResponse> getAllRequests(String status, Pageable pageable) {
        if (status != null && !status.isBlank()) {
            return bookingRequestRepository
                    .findByStatusAndIsDeletedFalse(BookingRequest.BookingStatus.valueOf(status.toUpperCase()), pageable)
                    .map(this::toRequestResponse);
        }
        return bookingRequestRepository.findByIsDeletedFalse(pageable).map(this::toRequestResponse);
    }

    @Transactional
    public BookingRequestResponse updateRequestStatus(UUID id, String status, String reason) {
        BookingRequest request = bookingRequestRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("BookingRequest", id));
        request.setStatus(BookingRequest.BookingStatus.valueOf(status.toUpperCase()));
        if (reason != null) request.setCancellationReason(reason);
        return toRequestResponse(bookingRequestRepository.save(request));
    }

    @Transactional
    public void cancelRequest(UUID id, String reason) {
        BookingRequest request = bookingRequestRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("BookingRequest", id));
        request.setStatus(BookingRequest.BookingStatus.CANCELLED);
        request.setCancellationReason(reason);
        bookingRequestRepository.save(request);

        eventProducer.publish(KafkaTopicConfig.TOPIC_BOOKINGS,
                eventProducer.buildEvent("BOOKING_REQUEST_CANCELLED",
                        id.toString(), "cargo:BookingRequest",
                        request.getLogisticsObjectRef(), "CANCELLED",
                        currentUser(), null));
    }

    // --- Booking ---

    @Transactional
    public BookingResponse confirmBooking(BookingDto dto) {
        if (bookingRepository.existsByBookingNumber(dto.getBookingNumber())) {
            throw new IllegalArgumentException("Booking number already exists: " + dto.getBookingNumber());
        }
        Booking booking = Booking.builder()
                .bookingNumber(dto.getBookingNumber())
                .bookingRequestId(dto.getBookingRequestId())
                .shipmentId(dto.getShipmentId())
                .waybillNumber(dto.getWaybillNumber())
                .originCode(dto.getOriginCode().toUpperCase())
                .destinationCode(dto.getDestinationCode().toUpperCase())
                .confirmedDeparture(dto.getConfirmedDeparture())
                .confirmedArrival(dto.getConfirmedArrival())
                .totalPieces(dto.getTotalPieces())
                .totalWeightKg(dto.getTotalWeightKg())
                .totalVolumeCbm(dto.getTotalVolumeCbm())
                .carrierCode(dto.getCarrierCode().toUpperCase())
                .carrierName(dto.getCarrierName())
                .shipperName(dto.getShipperName())
                .consigneeName(dto.getConsigneeName())
                .agentName(dto.getAgentName())
                .status(Booking.BookingStatus.CONFIRMED)
                .build();
        booking.setCompanyIdentifier(companyIdentifier);
        Booking saved = bookingRepository.save(booking);

        // Link the BookingRequest to this confirmed booking
        if (dto.getBookingRequestId() != null) {
            bookingRequestRepository.findByIdAndIsDeletedFalse(dto.getBookingRequestId()).ifPresent(req -> {
                req.setStatus(BookingRequest.BookingStatus.CONFIRMED);
                req.setConfirmedBookingId(saved.getId());
                bookingRequestRepository.save(req);
            });
        }
        BookingResponse response = toBookingResponse(saved);

        eventProducer.publish(KafkaTopicConfig.TOPIC_BOOKINGS,
                eventProducer.buildEvent("BOOKING_CONFIRMED",
                        saved.getId().toString(), "cargo:Booking",
                        saved.getLogisticsObjectRef(), "CONFIRMED",
                        currentUser(), response));
        return response;
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(UUID id) {
        return toBookingResponse(bookingRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id)));
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingByNumber(String bookingNumber) {
        return toBookingResponse(bookingRepository.findByBookingNumberAndIsDeletedFalse(bookingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingNumber)));
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> getAllBookings(String status, String carrierCode, Pageable pageable) {
        if (carrierCode != null && !carrierCode.isBlank()) {
            return bookingRepository.findByCarrierCodeAndIsDeletedFalse(carrierCode.toUpperCase(), pageable)
                    .map(this::toBookingResponse);
        }
        if (status != null && !status.isBlank()) {
            return bookingRepository.findByStatusAndIsDeletedFalse(
                    Booking.BookingStatus.valueOf(status.toUpperCase()), pageable).map(this::toBookingResponse);
        }
        return bookingRepository.findByIsDeletedFalse(pageable).map(this::toBookingResponse);
    }

    @Transactional
    public BookingResponse cancelBooking(UUID id, String reason) {
        Booking booking = bookingRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setCancellationReason(reason);
        booking.setRevision(booking.getRevision() + 1);
        BookingResponse response = toBookingResponse(bookingRepository.save(booking));

        eventProducer.publish(KafkaTopicConfig.TOPIC_BOOKINGS,
                eventProducer.buildEvent("BOOKING_CANCELLED",
                        id.toString(), "cargo:Booking",
                        booking.getLogisticsObjectRef(), "CANCELLED",
                        currentUser(), response));
        return response;
    }

    // --- Helpers ---

    private String currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }

    // --- Mappers ---

    public BookingRequestResponse toRequestResponse(BookingRequest r) {
        return BookingRequestResponse.builder()
                .type("cargo:BookingRequest")
                .id(r.getId())
                .logisticsObjectRef(r.getLogisticsObjectRef())
                .bookingRef(r.getBookingRef())
                .originCode(r.getOriginCode())
                .destinationCode(r.getDestinationCode())
                .requestedDeparture(r.getRequestedDeparture())
                .requestedArrival(r.getRequestedArrival())
                .goodsDescription(r.getGoodsDescription())
                .totalPieces(r.getTotalPieces())
                .totalWeightKg(r.getTotalWeightKg())
                .totalVolumeCbm(r.getTotalVolumeCbm())
                .specialHandlingCodes(r.getSpecialHandlingCodes())
                .shipperName(r.getShipperName())
                .consigneeName(r.getConsigneeName())
                .agentName(r.getAgentName())
                .preferredCarrierCode(r.getPreferredCarrierCode())
                .status(r.getStatus().name())
                .confirmedBookingId(r.getConfirmedBookingId())
                .cancellationReason(r.getCancellationReason())
                .revision(r.getRevision())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }

    public BookingResponse toBookingResponse(Booking b) {
        List<TransportSegmentResponse> segments = b.getTransportSegments() != null
                ? b.getTransportSegments().stream()
                    .filter(s -> !s.getIsDeleted())
                    .map(transportService::toSegmentResponse)
                    .collect(Collectors.toList())
                : List.of();

        return BookingResponse.builder()
                .type("cargo:Booking")
                .id(b.getId())
                .logisticsObjectRef(b.getLogisticsObjectRef())
                .bookingNumber(b.getBookingNumber())
                .bookingRequestId(b.getBookingRequestId())
                .shipmentId(b.getShipmentId())
                .waybillNumber(b.getWaybillNumber())
                .originCode(b.getOriginCode())
                .destinationCode(b.getDestinationCode())
                .confirmedDeparture(b.getConfirmedDeparture())
                .confirmedArrival(b.getConfirmedArrival())
                .totalPieces(b.getTotalPieces())
                .totalWeightKg(b.getTotalWeightKg())
                .totalVolumeCbm(b.getTotalVolumeCbm())
                .carrierCode(b.getCarrierCode())
                .carrierName(b.getCarrierName())
                .shipperName(b.getShipperName())
                .consigneeName(b.getConsigneeName())
                .agentName(b.getAgentName())
                .status(b.getStatus().name())
                .cancellationReason(b.getCancellationReason())
                .transportSegments(segments)
                .revision(b.getRevision())
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .build();
    }
}