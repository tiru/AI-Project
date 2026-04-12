package com.cargo.onerecord.dto.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class AuditTrailResponse {

    @JsonProperty("@type")
    private String type = "api:AuditTrail";

    private UUID id;
    private UUID logisticsObjectId;
    private String logisticsObjectType;
    private Integer revision;
    private String fieldPath;
    private String oldValue;
    private String newValue;
    private String jsonPatch;
    private String operation;
    private UUID changeRequestId;
    private String changedBy;
    private String reason;
    private OffsetDateTime changedAt;
}