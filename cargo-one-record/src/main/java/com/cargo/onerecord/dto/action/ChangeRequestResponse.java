package com.cargo.onerecord.dto.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class ChangeRequestResponse {

    @JsonProperty("@type")
    private String type = "api:ChangeRequest";

    private UUID id;
    private UUID targetObjectId;
    private String targetObjectType;
    private String fieldPath;
    private String oldValue;
    private String newValue;
    private String jsonPatch;
    private String reason;
    private String status;
    private String requestedBy;
    private OffsetDateTime requestedAt;
    private String reviewedBy;
    private OffsetDateTime reviewedAt;
    private String reviewNotes;
}