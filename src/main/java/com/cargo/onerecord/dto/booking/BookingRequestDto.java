package com.cargo.onerecord.dto.booking;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class BookingRequestDto {

    private String bookingRef;

    @NotBlank(message = "Origin code is required")
    private String originCode;

    @NotBlank(message = "Destination code is required")
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
}