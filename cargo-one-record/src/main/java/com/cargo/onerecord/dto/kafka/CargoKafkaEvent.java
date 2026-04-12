package com.cargo.onerecord.dto.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CargoKafkaEvent {

    /** Unique ID for this event message */
    private String eventId;

    /**
     * Type of domain event, e.g.:
     * SHIPMENT_CREATED, SHIPMENT_UPDATED, SHIPMENT_DELETED,
     * LOGISTICS_EVENT_RECORDED,
     * BOOKING_REQUEST_SUBMITTED, BOOKING_REQUEST_CANCELLED,
     * BOOKING_CONFIRMED, BOOKING_CANCELLED,
     * CHANGE_REQUEST_SUBMITTED, CHANGE_REQUEST_APPROVED,
     * CHANGE_REQUEST_REJECTED, CHANGE_REQUEST_REVOKED
     */
    private String eventType;

    /** UUID of the affected logistics object */
    private String objectId;

    /** ONE Record type, e.g. cargo:Shipment, cargo:Booking */
    private String objectType;

    /** Full ONE Record reference URL */
    private String objectRef;

    /** Current status or event code (e.g. CONFIRMED, RCS, APPROVED) */
    private String status;

    /** User who triggered the event */
    private String performedBy;

    /** Full response payload for downstream consumers */
    private Object payload;

    /** When this event occurred */
    private OffsetDateTime occurredAt;
}