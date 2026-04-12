package com.cargo.onerecord.model.action;

import com.cargo.onerecord.model.core.LogisticsObject;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * ONE Record ChangeRequest — a request to modify a logistics object.
 *
 * In ONE Record, only the holder (owner) of a logistics object can directly modify it.
 * Other parties must submit a ChangeRequest which the owner can APPROVE or REJECT.
 *
 * Ref: https://onerecord.iata.org/ns/api#ChangeRequest
 */
@Entity
@Table(name = "change_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeRequest extends LogisticsObject {

    /** ID of the logistics object that needs to be changed */
    @Column(name = "target_object_id", nullable = false)
    private UUID targetObjectId;

    /** Type of the target logistics object */
    @Column(name = "target_object_type", length = 50)
    private String targetObjectType;

    /**
     * The field path to change (e.g. "goodsDescription", "totalGrossWeight.value").
     * For complex changes, use JSON Patch format in requestedChanges.
     */
    @Column(name = "field_path")
    private String fieldPath;

    /** Previous value (serialized as JSON string) */
    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    /** New requested value (serialized as JSON string) */
    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    /** Full JSON Patch document (RFC 6902) for complex multi-field changes */
    @Column(name = "json_patch", columnDefinition = "TEXT")
    private String jsonPatch;

    /** Reason / justification provided by the requester */
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    /**
     * Current status:
     * PENDING  — waiting for owner approval
     * APPROVED — approved and applied to the logistics object
     * REJECTED — rejected by the owner
     * REVOKED  — withdrawn by the requester
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RequestStatus status = RequestStatus.PENDING;

    /** Username or company identifier of the party requesting the change */
    @Column(name = "requested_by")
    private String requestedBy;

    /** When the request was submitted */
    @Column(name = "requested_at")
    private OffsetDateTime requestedAt;

    /** Username of the party who approved/rejected */
    @Column(name = "reviewed_by")
    private String reviewedBy;

    /** When the request was approved or rejected */
    @Column(name = "reviewed_at")
    private OffsetDateTime reviewedAt;

    /** Notes from the reviewer when approving or rejecting */
    @Column(name = "review_notes", columnDefinition = "TEXT")
    private String reviewNotes;

    @PrePersist
    void prePersist() {
        setLogisticsObjectType("api:ChangeRequest");
        if (requestedAt == null) requestedAt = OffsetDateTime.now();
    }

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED,
        REVOKED
    }
}