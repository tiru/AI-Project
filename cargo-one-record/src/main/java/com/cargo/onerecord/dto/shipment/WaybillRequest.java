package com.cargo.onerecord.dto.shipment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class WaybillRequest {

    /**
     * AWB number in IATA format: NNN-NNNNNNNN (e.g. 180-12345678)
     */
    @NotBlank(message = "Waybill number is required")
    @Pattern(regexp = "\\d{3}-\\d{8}", message = "Waybill number must be in format NNN-NNNNNNNN (e.g. 180-12345678)")
    private String waybillNumber;

    /** MASTER, HOUSE, DIRECT */
    @NotBlank(message = "Waybill type is required")
    private String waybillType;

    @NotBlank(message = "Carrier code is required")
    private String carrierCode;

    @NotBlank(message = "Origin code is required")
    private String originCode;

    private String originName;

    @NotBlank(message = "Destination code is required")
    private String destinationCode;

    private String destinationName;
    private LocalDate issueDate;
    private String issuePlace;
    private Integer numberOfPieces;
    private Double totalWeight;
    private String totalWeightUnit = "KGM";
    private String natureAndQuantity;

    /** Link to existing shipment */
    private UUID shipmentId;
}