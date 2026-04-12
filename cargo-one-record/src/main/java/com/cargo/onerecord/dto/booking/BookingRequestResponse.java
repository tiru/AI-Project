package com.cargo.onerecord.dto.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class BookingRequestResponse {

    @JsonProperty("@type")
    private String type = "cargo:BookingRequest";

    private UUID id;
    private String logisticsObjectRef;
    private String bookingRef;
    private String originCode;
    private String destinationCode;
    private OffsetDateTime requestedDeparture;
    private OffsetDateTime requestedArrival;
    private String goodsDescription;
    private Integer totalPieces;
    private Double totalWeightKg;
    private Double totalVolumeCbm;
    private String specialHandlingCodes;
    private String shipperName;
    private String consigneeName;
    private String agentName;
    private String preferredCarrierCode;
    private String status;
    private UUID confirmedBookingId;
    private String cancellationReason;
    private Integer revision;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}