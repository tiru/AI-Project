package com.cargo.onerecord.dto.shipment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class WaybillResponse {

    @JsonProperty("@type")
    private String type = "cargo:Waybill";

    private UUID id;
    private String logisticsObjectRef;
    private String waybillNumber;
    private String waybillType;
    private String carrierCode;
    private String originCode;
    private String originName;
    private String destinationCode;
    private String destinationName;
    private LocalDate issueDate;
    private String issuePlace;
    private Integer numberOfPieces;
    private Double totalWeight;
    private String totalWeightUnit;
    private String natureAndQuantity;
    private UUID shipmentId;
    private Integer revision;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}