package com.cargo.onerecord.model.action;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * ONE Record AuditTrail entry — immutable record of every change applied to a logistics object.
 * Every approved ChangeRequest creates one AuditTrailEntry.
 * Provides full version history and traceability.
 *
 * Ref: https://onerecord.iata.org/ns/api#AuditTrail
 */
@Entity
@Table(name = "audit_trail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditTrailEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** The logistics object this audit entry belongs to */
    @Column(name = "logistics_object_id", nullable = false)
    private UUID logisticsObjectId;

    /** Type of the logistics object */
    @Column(name = "logistics_object_type", length = 50)
    private String logisticsObjectType;

    /** Revision number this entry corresponds to */
    @Column(name = "revision", nullable = false)
    private Integer revision;

    /** Field that was changed */
    @Column(name = "field_path")
    private String fieldPath;

    /** Value before the change */
    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    /** Value after the change */
    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    /** Full JSON Patch that was applied */
    @Column(name = "json_patch", columnDefinition = "TEXT")
    private String jsonPatch;

    /** Operation type: CREATE, UPDATE, DELETE */
    @Column(name = "operation", length = 20)
    private String operation;

    /** ID of the ChangeRequest that triggered this entry (null for direct creates) */
    @Column(name = "change_request_id")
    private UUID changeRequestId;

    /** Who made the change */
    @Column(name = "changed_by")
    private String changedBy;

    /** Reason provided */
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @CreationTimestamp
    @Column(name = "changed_at", updatable = false)
    private OffsetDateTime changedAt;
}