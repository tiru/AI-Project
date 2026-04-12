package com.cargo.onerecord.model.common;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ONE Record Dimensions (length × width × height).
 * Ref: https://onerecord.iata.org/ns/cargo#Dimensions
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dimensions {

    @Column(name = "dim_length")
    private Double length;

    @Column(name = "dim_width")
    private Double width;

    @Column(name = "dim_height")
    private Double height;

    @Column(name = "dim_volume")
    private Double volume;

    /**
     * Unit of measurement: CMT (Centimeters) or INH (Inches)
     */
    @Column(name = "dim_unit", length = 10)
    private String unit = "CMT";
}