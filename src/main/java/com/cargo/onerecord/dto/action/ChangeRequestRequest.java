package com.cargo.onerecord.dto.action;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ChangeRequestRequest {

    @NotNull(message = "Target logistics object ID is required")
    private UUID targetObjectId;

    private String targetObjectType;

    /**
     * Simple single-field change: provide fieldPath + newValue.
     * Example: fieldPath="goodsDescription", newValue="Updated description"
     */
    private String fieldPath;
    private String newValue;

    /**
     * Complex multi-field change: provide jsonPatch (RFC 6902 JSON Patch format).
     * Example: [{"op":"replace","path":"/goodsDescription","value":"New value"}]
     */
    private String jsonPatch;

    /** Reason / justification for the change */
    private String reason;
}