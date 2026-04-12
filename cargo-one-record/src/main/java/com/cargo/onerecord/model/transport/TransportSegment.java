package com.cargo.onerecord.model.transport;

import com.cargo.onerecord.model.core.LogisticsObject;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * ONE Record TransportSegment — one leg of the journey from origin to destination.
 * A multi-leg shipment has multiple TransportSegments.
 * Ref: https://onerecord.iata.org/ns/cargo#TransportSegment
 */
@Entity
@Table(name = "transport_segments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransportSegment extends LogisticsObject {

    /** Sequence number of this leg (1 = first leg, 2 = second leg, etc.) */
    @Column(name = "sequence_number", nullable = false)
    private Integer sequenceNumber;

    // --- Departure ---

    /** IATA 3-letter departure airport code (e.g. DXB) */
    @Column(name = "departure_location_code", nullable = false, length = 10)
    private String departureLocationCode;

    /** Full name of departure airport/city */
    @Column(name = "departure_location_name")
    private String departureLocationName;

    /** Scheduled departure date and time */
    @Column(name = "scheduled_departure")
    private OffsetDateTime scheduledDeparture;

    /** Actual departure date and time */
    @Column(name = "actual_departure")
    private OffsetDateTime actualDeparture;

    // --- Arrival ---

    /** IATA 3-letter arrival airport code (e.g. LHR) */
    @Column(name = "arrival_location_code", nullable = false, length = 10)
    private String arrivalLocationCode;

    /** Full name of arrival airport/city */
    @Column(name = "arrival_location_name")
    private String arrivalLocationName;

    /** Scheduled arrival date and time */
    @Column(name = "scheduled_arrival")
    private OffsetDateTime scheduledArrival;

    /** Actual arrival date and time */
    @Column(name = "actual_arrival")
    private OffsetDateTime actualArrival;

    // --- Transport means ---

    /**
     * The aircraft/vehicle operating this segment.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transport_means_id")
    private TransportMeans transportMeans;

    /** IATA flight/service number for this segment (e.g. EK201) */
    @Column(name = "transport_identifier", length = 20)
    private String transportIdentifier;

    // --- Status ---

    /**
     * Status of this segment:
     * PLANNED, CONFIRMED, BOARDED, DEPARTED, ARRIVED, CANCELLED
     */
    @Column(name = "segment_status", length = 20)
    private String segmentStatus = "PLANNED";

    /** Reference to the Shipment this segment belongs to */
    @Column(name = "shipment_id")
    private java.util.UUID shipmentId;

    /** ULD (Unit Load Device) positions / load reference */
    @Column(name = "load_reference", length = 50)
    private String loadReference;

    /** Booked weight on this segment in KG */
    @Column(name = "booked_weight_kg")
    private Double bookedWeightKg;

    /** Booked volume on this segment in CBM */
    @Column(name = "booked_volume_cbm")
    private Double bookedVolumeCbm;

    @PrePersist
    void prePersist() {
        setLogisticsObjectType("cargo:TransportSegment");
    }
}