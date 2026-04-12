package com.cargo.onerecord.dto.transport;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class TransportSegmentRequest {

    @NotNull(message = "Sequence number is required")
    @Min(value = 1, message = "Sequence number must be >= 1")
    private Integer sequenceNumber;

    @NotBlank(message = "Departure location code is required")
    private String departureLocationCode;

    private String departureLocationName;
    private OffsetDateTime scheduledDeparture;
    private OffsetDateTime actualDeparture;

    @NotBlank(message = "Arrival location code is required")
    private String arrivalLocationCode;

    private String arrivalLocationName;
    private OffsetDateTime scheduledArrival;
    private OffsetDateTime actualArrival;

    private UUID transportMeansId;
    private String transportIdentifier;

    /** PLANNED, CONFIRMED, BOARDED, DEPARTED, ARRIVED, CANCELLED */
    private String segmentStatus = "PLANNED";

    private UUID shipmentId;
    private String loadReference;
    private Double bookedWeightKg;
    private Double bookedVolumeCbm;
}