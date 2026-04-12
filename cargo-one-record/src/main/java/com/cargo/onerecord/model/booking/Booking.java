package com.cargo.onerecord.model.booking;

import com.cargo.onerecord.model.core.LogisticsObject;
import com.cargo.onerecord.model.transport.TransportSegment;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ONE Record Booking — a confirmed booking of cargo capacity.
 * Created when a BookingRequest is accepted and confirmed by the carrier.
 * Ref: https://onerecord.iata.org/ns/cargo#Booking
 */
@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking extends LogisticsObject {

    /** Carrier-issued booking confirmation number */
    @Column(name = "booking_number", unique = true, length = 50)
    private String bookingNumber;

    /** Reference back to the original BookingRequest */
    @Column(name = "booking_request_id")
    private UUID bookingRequestId;

    /** Linked shipment ID */
    @Column(name = "shipment_id")
    private UUID shipmentId;

    /** Air Waybill number associated with this booking */
    @Column(name = "waybill_number", length = 20)
    private String waybillNumber;

    // --- Route ---

    @Column(name = "origin_code", nullable = false, length = 10)
    private String originCode;

    @Column(name = "destination_code", nullable = false, length = 10)
    private String destinationCode;

    @Column(name = "confirmed_departure")
    private OffsetDateTime confirmedDeparture;

    @Column(name = "confirmed_arrival")
    private OffsetDateTime confirmedArrival;

    // --- Booked capacity ---

    @Column(name = "total_pieces")
    private Integer totalPieces;

    @Column(name = "total_weight_kg")
    private Double totalWeightKg;

    @Column(name = "total_volume_cbm")
    private Double totalVolumeCbm;

    // --- Carrier ---

    @Column(name = "carrier_code", length = 5)
    private String carrierCode;

    @Column(name = "carrier_name")
    private String carrierName;

    // --- Parties ---

    @Column(name = "shipper_name")
    private String shipperName;

    @Column(name = "consignee_name")
    private String consigneeName;

    @Column(name = "agent_name")
    private String agentName;

    // --- Status ---

    /**
     * CONFIRMED — booking is active
     * CANCELLED — cancelled by either party
     * COMPLETED — shipment has been delivered
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BookingStatus status = BookingStatus.CONFIRMED;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    /** Transport legs (flight segments) for this booking */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    @Builder.Default
    private List<TransportSegment> transportSegments = new ArrayList<>();

    @PrePersist
    void prePersist() {
        setLogisticsObjectType("cargo:Booking");
    }

    public enum BookingStatus {
        CONFIRMED, CANCELLED, COMPLETED
    }
}