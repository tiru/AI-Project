package com.cargo.onerecord.model.transport;

import com.cargo.onerecord.model.core.LogisticsObject;
import jakarta.persistence.*;
import lombok.*;

/**
 * ONE Record TransportMeans — the vehicle/aircraft performing a transport segment.
 * For air cargo this is the aircraft.
 * Ref: https://onerecord.iata.org/ns/cargo#TransportMeans
 */
@Entity
@Table(name = "transport_means")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransportMeans extends LogisticsObject {

    /**
     * Type of transport:
     * AIRCRAFT, TRUCK, VESSEL, RAIL, OTHER
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "transport_mode", nullable = false, length = 20)
    private TransportMode transportMode;

    /** IATA flight number (e.g. EK201, QR007) */
    @Column(name = "flight_number", length = 20)
    private String flightNumber;

    /** ICAO aircraft type code (e.g. B77W, A388, A332) */
    @Column(name = "aircraft_type", length = 10)
    private String aircraftType;

    /** Aircraft registration / tail number (e.g. A6-ENA) */
    @Column(name = "registration_number", length = 20)
    private String registrationNumber;

    /** IATA 2-letter operating carrier code (e.g. EK, QR, SQ) */
    @Column(name = "operating_carrier_code", length = 5)
    private String operatingCarrierCode;

    /** Full name of the operating carrier */
    @Column(name = "operating_carrier_name")
    private String operatingCarrierName;

    /** Truck plate / vessel IMO number / train number */
    @Column(name = "vehicle_identifier", length = 50)
    private String vehicleIdentifier;

    /** Maximum payload capacity in KG */
    @Column(name = "max_payload_kg")
    private Double maxPayloadKg;

    /** Maximum volume capacity in cubic meters */
    @Column(name = "max_volume_cbm")
    private Double maxVolumeCbm;

    @PrePersist
    void prePersist() {
        setLogisticsObjectType("cargo:TransportMeans");
    }

    public enum TransportMode {
        AIRCRAFT, TRUCK, VESSEL, RAIL, OTHER
    }
}