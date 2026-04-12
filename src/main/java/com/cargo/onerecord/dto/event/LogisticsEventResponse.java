package com.cargo.onerecord.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class LogisticsEventResponse {

    @JsonProperty("@type")
    private String type = "cargo:LogisticsEvent";

    private UUID id;
    private String logisticsObjectRef;
    private String eventCode;
    private String eventDescription;
    private OffsetDateTime eventDate;
    private String eventType;
    private String eventLocationCode;
    private String eventLocationName;
    private String recordedBy;
    private OffsetDateTime recordedAt;
    private Integer pieceCount;
    private Double weight;
    private String weightUnit;
    private UUID logisticsObjectId;
    private String logisticsObjectTypeRef;
    private OffsetDateTime createdAt;
}