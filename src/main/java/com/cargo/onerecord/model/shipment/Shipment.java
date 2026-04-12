package com.cargo.onerecord.model.shipment;

import com.cargo.onerecord.model.common.Weight;
import com.cargo.onerecord.model.core.LogisticsObject;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ONE Record Shipment — the central logistics object.
 * Represents a consignment of goods being transported.
 * Ref: https://onerecord.iata.org/ns/cargo#Shipment
 */
@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment extends LogisticsObject {

    /** Human-readable description of the goods */
    @Column(name = "goods_description", columnDefinition = "TEXT")
    private String goodsDescription;

    /** Declared value for carriage (insurance purposes) */
    @Column(name = "declared_value_for_carriage")
    private Double declaredValueForCarriage;

    /** Declared value for customs */
    @Column(name = "declared_value_for_customs")
    private Double declaredValueForCustoms;

    /** Currency code for declared values (e.g. USD, EUR) */
    @Column(name = "declared_value_currency", length = 10)
    private String declaredValueCurrency;

    /** Total gross weight of the entire shipment */
    @Embedded
    private Weight totalGrossWeight;

    /**
     * Shipper (sender) — references a Company logistics object by identifier.
     * Full Party entity will be linked in Step 3.
     */
    @Column(name = "shipper_identifier")
    private String shipperIdentifier;

    @Column(name = "shipper_name")
    private String shipperName;

    /**
     * Consignee (receiver) — references a Company logistics object by identifier.
     */
    @Column(name = "consignee_identifier")
    private String consigneeIdentifier;

    @Column(name = "consignee_name")
    private String consigneeName;

    /** IATA special handling codes (e.g. PER, DGR, VAL, HEA) */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "shipment_handling_codes",
            joinColumns = @JoinColumn(name = "shipment_id"))
    @Column(name = "code", length = 10)
    @Builder.Default
    private List<String> specialHandlingCodes = new ArrayList<>();

    /** Pieces belonging to this shipment */
    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Piece> pieces = new ArrayList<>();

    /** Air waybill linked to this shipment */
    @OneToOne(mappedBy = "shipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Waybill waybill;

    @PrePersist
    void prePersist() {
        setLogisticsObjectType("cargo:Shipment");
    }
}