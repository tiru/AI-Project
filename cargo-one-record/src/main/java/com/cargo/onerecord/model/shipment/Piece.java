package com.cargo.onerecord.model.shipment;

import com.cargo.onerecord.model.common.Dimensions;
import com.cargo.onerecord.model.common.Weight;
import com.cargo.onerecord.model.core.LogisticsObject;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ONE Record Piece — an individual package/unit within a shipment.
 * Ref: https://onerecord.iata.org/ns/cargo#Piece
 */
@Entity
@Table(name = "pieces")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Piece extends LogisticsObject {

    /** Indicates if this piece is a co-load piece */
    @Column(name = "coload")
    private Boolean coload = false;

    /** Description of the goods inside this piece */
    @Column(name = "goods_description", columnDefinition = "TEXT")
    private String goodsDescription;

    /** Shipper's reference number for this piece */
    @Column(name = "packaged_item_identifier", length = 50)
    private String packagedItemIdentifier;

    /** Type of load: LOOSE, ULD, PALLET, etc. */
    @Column(name = "load_type", length = 20)
    private String loadType;

    /** Whether this piece can be stacked */
    @Column(name = "stackable")
    private Boolean stackable = true;

    /** Whether this piece can be turned/tilted */
    @Column(name = "turnable")
    private Boolean turnable = true;

    /** Actual gross weight of this piece */
    @Embedded
    private Weight grossWeight;

    /** Physical dimensions of this piece */
    @Embedded
    private Dimensions dimensions;

    /** IATA special handling codes specific to this piece */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "piece_handling_codes",
            joinColumns = @JoinColumn(name = "piece_id"))
    @Column(name = "code", length = 10)
    @Builder.Default
    private List<String> specialHandlingCodes = new ArrayList<>();

    /** The shipment this piece belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    @PrePersist
    void prePersist() {
        setLogisticsObjectType("cargo:Piece");
    }
}