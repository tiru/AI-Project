package com.cargo.onerecord.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class LogisticsEventRequest {

    /**
     * IATA event code. Common codes:
     * RCS — Received from Shipper
     * DEP — Departed
     * ARR — Arrived
     * RCF — Received from Flight
     * DLV — Delivered to Consignee
     * NFD — Notified
     * AWR — Arrived at Warehouse
     * TFD — Transferred
     * FOH — Freight on Hand
     * CCD — Customs Cleared
     * MAN — Manifested
     * PRE — Prepared for Loading
     */
    @NotBlank(message = "Event code is required")
    private String eventCode;

    private String eventDescription;

    @NotNull(message = "Event date is required")
    private OffsetDateTime eventDate;

    /** ACTUAL, EXPECTED, PLANNED */
    private String eventType = "ACTUAL";

    private String eventLocationCode;
    private String eventLocationName;
    private String recordedBy;

    private Integer pieceCount;
    private Double weight;
    private String weightUnit = "KGM";

    /** ID of the logistics object this event belongs to */
    @NotNull(message = "Logistics object ID is required")
    private UUID logisticsObjectId;

    /**
     * Type of the parent logistics object.
     * cargo:Shipment, cargo:Piece, cargo:Waybill
     */
    private String logisticsObjectTypeRef;
}