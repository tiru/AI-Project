package com.cargo.onerecord.dto.booking;

import com.cargo.onerecord.dto.transport.TransportSegmentResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class BookingResponse {

    @JsonProperty("@type")
    private String type = "cargo:Booking";

    private UUID id;
    private String logisticsObjectRef;
    private String bookingNumber;
    private UUID bookingRequestId;
    private UUID shipmentId;
    private String waybillNumber;
    private String originCode;
    private String destinationCode;
    private OffsetDateTime confirmedDeparture;
    private OffsetDateTime confirmedArrival;
    private Integer totalPieces;
    private Double totalWeightKg;
    private Double totalVolumeCbm;
    private String carrierCode;
    private String carrierName;
    private String shipperName;
    private String consigneeName;
    private String agentName;
    private String status;
    private String cancellationReason;
    private List<TransportSegmentResponse> transportSegments;
    private Integer revision;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}