package com.cargo.onerecord.dto.transport;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransportMeansRequest {

    /** AIRCRAFT, TRUCK, VESSEL, RAIL, OTHER */
    @NotNull(message = "Transport mode is required")
    private String transportMode;

    /** e.g. EK201, QR007 */
    private String flightNumber;

    /** ICAO aircraft type: B77W, A388, A332 */
    private String aircraftType;

    /** Tail/registration number: A6-ENA */
    private String registrationNumber;

    @NotBlank(message = "Operating carrier code is required")
    private String operatingCarrierCode;

    private String operatingCarrierName;

    private String vehicleIdentifier;
    private Double maxPayloadKg;
    private Double maxVolumeCbm;
}