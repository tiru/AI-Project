package com.cargo.onerecord.model.common;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ONE Record Weight measurement.
 * Ref: https://onerecord.iata.org/ns/cargo#Weight
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Weight {

    @Column(name = "weight_value")
    private Double value;

    /**
     * Unit of measurement: KGM (Kilograms) or LBR (Pounds)
     */
    @Column(name = "weight_unit", length = 10)
    private String unit = "KGM";
}