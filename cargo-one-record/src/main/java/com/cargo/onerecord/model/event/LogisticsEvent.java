package com.cargo.onerecord.model.event;

import com.cargo.onerecord.model.core.LogisticsObject;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * ONE Record LogisticsEvent — records what happened to a logistics object.
 * Examples: RCS (Received from Shipper), DEP (Departed), ARR (Arrived),
 *           DLV (Delivered), NFD (Notified), AWR (Arrived at Warehouse).
 *
 * Ref: https://onerecord.iata.org/ns/cargo#LogisticsEvent
 */
@Entity
@Table(name = "logistics_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogisticsEvent extends LogisticsObject {

    /**
     * IATA event code (e.g. RCS, DEP, ARR, DLV, NFD, TFD, AWR, CCD, FOH).
     * Ref: IATA CCSF event codes.
     */
    @Column(name = "event_code", nullable = false, length = 10)
    private String eventCode;

    /** Human-readable description of the event */
    @Column(name = "event_description")
    private String eventDescription;

    /** Actual date and time the event occurred */
    @Column(name = "event_date", nullable = false)
    private OffsetDateTime eventDate;

    /**
     * Event type:
     * ACTUAL   — the event has happened
     * EXPECTED — the event is planned/expected
     * PLANNED  — scheduled event
     */
    @Column(name = "event_type", length = 20)
    private String eventType = "ACTUAL";

    /** IATA 3-letter code of the location where the event occurred (e.g. DXB, LHR) */
    @Column(name = "event_location_code", length = 10)
    private String eventLocationCode;

    /** Name of the location */
    @Column(name = "event_location_name")
    private String eventLocationName;

    /** Name of the party recording this event (e.g. carrier, handler) */
    @Column(name = "recorded_by")
    private String recordedBy;

    /** Date and time the event was recorded in the system */
    @Column(name = "recorded_at")
    private OffsetDateTime recordedAt;

    /** Partial pieces count (for piece-level events) */
    @Column(name = "piece_count")
    private Integer pieceCount;

    /** Partial weight for this event */
    @Column(name = "weight")
    private Double weight;

    @Column(name = "weight_unit", length = 10)
    private String weightUnit;

    /**
     * ID of the logistics object this event belongs to.
     * Can reference a Shipment, Piece, Waybill, or any other LogisticsObject.
     */
    @Column(name = "logistics_object_id", nullable = false)
    private UUID logisticsObjectId;

    /**
     * Type of the parent logistics object
     * (cargo:Shipment, cargo:Piece, cargo:Waybill, etc.)
     */
    @Column(name = "logistics_object_type_ref", length = 50)
    private String logisticsObjectTypeRef;

    @PrePersist
    void prePersist() {
        setLogisticsObjectType("cargo:LogisticsEvent");
        if (recordedAt == null) recordedAt = OffsetDateTime.now();
    }
}