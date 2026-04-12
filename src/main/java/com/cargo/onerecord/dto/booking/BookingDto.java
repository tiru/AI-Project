package com.cargo.onerecord.dto.booking;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class BookingDto {

    @NotBlank(message = "Booking number is required")
    private String bookingNumber;

    private UUID bookingRequestId;
    private UUID shipmentId;
    private String waybillNumber;

    @NotBlank(message = "Origin code is required")
    private String originCode;

    @NotBlank(message = "Destination code is required")
    private String destinationCode;

    private OffsetDateTime confirmedDeparture;
    private OffsetDateTime confirmedArrival;
    private Integer totalPieces;
    private Double totalWeightKg;
    private Double totalVolumeCbm;

    @NotBlank(message = "Carrier code is required")
    private String carrierCode;

    private String carrierName;
    private String shipperName;
    private String consigneeName;
    private String agentName;
}