package com.cargo.onerecord.model.booking;

import com.cargo.onerecord.model.core.LogisticsObject;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * ONE Record BookingRequest — a request to book capacity for a shipment.
 * Submitted by a shipper/forwarder to an airline.
 * Ref: https://onerecord.iata.org/ns/cargo#BookingRequest
 */
@Entity
@Table(name = "booking_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest extends LogisticsObject {

    /** Reference number assigned by the shipper/forwarder */
    @Column(name = "booking_ref", unique = true, length = 50)
    private String bookingRef;

    // --- Route ---

    /** IATA 3-letter origin airport code */
    @Column(name = "origin_code", nullable = false, length = 10)
    private String originCode;

    /** IATA 3-letter destination airport code */
    @Column(name = "destination_code", nullable = false, length = 10)
    private String destinationCode;

    /** Requested departure date */
    @Column(name = "requested_departure")
    private OffsetDateTime requestedDeparture;

    /** Requested arrival date */
    @Column(name = "requested_arrival")
    private OffsetDateTime requestedArrival;

    // --- Cargo details ---

    @Column(name = "goods_description", columnDefinition = "TEXT")
    private String goodsDescription;

    @Column(name = "total_pieces")
    private Integer totalPieces;

    @Column(name = "total_weight_kg")
    private Double totalWeightKg;

    @Column(name = "total_volume_cbm")
    private Double totalVolumeCbm;

    /** Special handling codes (DGR, PER, VAL, etc.) stored as comma-separated */
    @Column(name = "special_handling_codes")
    private String specialHandlingCodes;

    // --- Parties ---

    @Column(name = "shipper_name")
    private String shipperName;

    @Column(name = "consignee_name")
    private String consigneeName;

    @Column(name = "agent_name")
    private String agentName;

    /** Preferred airline carrier code */
    @Column(name = "preferred_carrier_code", length = 5)
    private String preferredCarrierCode;

    // --- Status ---

    /**
     * PENDING — submitted, awaiting response
     * OFFERED — carrier has sent booking options
     * CONFIRMED — booking confirmed by both parties
     * CANCELLED — cancelled by shipper or carrier
     * REJECTED — carrier could not accommodate
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BookingStatus status = BookingStatus.PENDING;

    /** ID of the Booking created upon confirmation */
    @Column(name = "confirmed_booking_id")
    private java.util.UUID confirmedBookingId;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @PrePersist
    void prePersist() {
        setLogisticsObjectType("cargo:BookingRequest");
    }

    public enum BookingStatus {
        PENDING, OFFERED, CONFIRMED, CANCELLED, REJECTED
    }
}