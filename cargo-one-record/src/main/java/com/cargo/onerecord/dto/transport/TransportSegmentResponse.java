package com.cargo.onerecord.dto.transport;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class TransportSegmentResponse {

    @JsonProperty("@type")
    private String type = "cargo:TransportSegment";

    private UUID id;
    private String logisticsObjectRef;
    private Integer sequenceNumber;
    private String departureLocationCode;
    private String departureLocationName;
    private OffsetDateTime scheduledDeparture;
    private OffsetDateTime actualDeparture;
    private String arrivalLocationCode;
    private String arrivalLocationName;
    private OffsetDateTime scheduledArrival;
    private OffsetDateTime actualArrival;
    private UUID transportMeansId;
    private String transportIdentifier;
    private String segmentStatus;
    private UUID shipmentId;
    private String loadReference;
    private Double bookedWeightKg;
    private Double bookedVolumeCbm;
    private Integer revision;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}