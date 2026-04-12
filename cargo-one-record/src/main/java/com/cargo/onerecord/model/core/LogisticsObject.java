package com.cargo.onerecord.model.core;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Base class for all ONE Record Logistics Objects.
 * Every entity in the ONE Record data model extends this class.
 * Ref: https://onerecord.iata.org/ns/cargo#LogisticsObject
 */
@Getter
@Setter
@MappedSuperclass
public abstract class LogisticsObject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * ONE Record type identifier (e.g. "cargo:Shipment", "cargo:Piece")
     */
    @Column(name = "logistics_object_type", nullable = false)
    private String logisticsObjectType;

    /**
     * The company/organization that owns this logistics object.
     * Expressed as a URI (e.g. https://cargo.example.com)
     */
    @Column(name = "company_identifier")
    private String companyIdentifier;

    /**
     * Revision number incremented on every approved change.
     * Used for audit trail and optimistic locking.
     */
    @Column(name = "revision", nullable = false)
    private Integer revision = 1;

    /**
     * Soft delete flag — ONE Record objects are never hard-deleted.
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    /**
     * Returns the full ONE Record URI for this object.
     * Format: {companyIdentifier}/logistics-objects/{id}
     */
    @Transient
    public String getLogisticsObjectRef() {
        return companyIdentifier + "/logistics-objects/" + id;
    }
}