package com.cargo.onerecord.dto.transport;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class TransportMeansResponse {

    @JsonProperty("@type")
    private String type = "cargo:TransportMeans";

    private UUID id;
    private String logisticsObjectRef;
    private String transportMode;
    private String flightNumber;
    private String aircraftType;
    private String registrationNumber;
    private String operatingCarrierCode;
    private String operatingCarrierName;
    private String vehicleIdentifier;
    private Double maxPayloadKg;
    private Double maxVolumeCbm;
    private Integer revision;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}