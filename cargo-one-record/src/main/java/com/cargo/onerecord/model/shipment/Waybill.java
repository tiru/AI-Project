package com.cargo.onerecord.model.shipment;

import com.cargo.onerecord.model.core.LogisticsObject;
import jakarta.persistence.*;
import lombok.*;

/**
 * ONE Record Waybill — the Air Waybill (AWB) document.
 * Can be a Master AWB (MAWB) or House AWB (HAWB).
 * Ref: https://onerecord.iata.org/ns/cargo#Waybill
 */
@Entity
@Table(name = "waybills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Waybill extends LogisticsObject {

    /**
     * Air Waybill number — 11-digit IATA format (e.g. 180-12345678).
     * First 3 digits = carrier code, remaining 8 = serial number.
     */
    @Column(name = "waybill_number", unique = true, length = 20)
    private String waybillNumber;

    /**
     * Type of waybill:
     * MASTER — issued by carrier (MAWB)
     * HOUSE  — issued by freight forwarder (HAWB)
     * DIRECT — direct shipment waybill
     */
    @Column(name = "waybill_type", length = 20)
    private String waybillType;

    /** IATA 2-letter or 3-letter carrier code (e.g. EK, QR, SQ) */
    @Column(name = "carrier_code", length = 10)
    private String carrierCode;

    /** IATA 3-letter code of origin airport (e.g. DXB, DOH, SIN) */
    @Column(name = "origin_code", length = 10)
    private String originCode;

    /** Full name of origin airport/city */
    @Column(name = "origin_name")
    private String originName;

    /** IATA 3-letter code of destination airport (e.g. LHR, JFK, NRT) */
    @Column(name = "destination_code", length = 10)
    private String destinationCode;

    /** Full name of destination airport/city */
    @Column(name = "destination_name")
    private String destinationName;

    /** Date and time the waybill was issued */
    @Column(name = "issue_date")
    private java.time.LocalDate issueDate;

    /** Place where the waybill was issued */
    @Column(name = "issue_place", length = 100)
    private String issuePlace;

    /** Number of pieces on the waybill */
    @Column(name = "number_of_pieces")
    private Integer numberOfPieces;

    /** Total weight on the waybill */
    @Column(name = "total_weight")
    private Double totalWeight;

    @Column(name = "total_weight_unit", length = 10)
    private String totalWeightUnit = "KGM";

    /** Nature and quantity of goods (free text) */
    @Column(name = "nature_and_quantity", columnDefinition = "TEXT")
    private String natureAndQuantity;

    /** Shipment linked to this waybill */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    @PrePersist
    void prePersist() {
        setLogisticsObjectType("cargo:Waybill");
    }
}