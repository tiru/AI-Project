package com.cargo.onerecord.dto.common;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DimensionsDto {

    @Positive(message = "Length must be positive")
    private Double length;

    @Positive(message = "Width must be positive")
    private Double width;

    @Positive(message = "Height must be positive")
    private Double height;

    private Double volume;

    /** CMT (centimetres) or INH (inches) */
    private String unit = "CMT";
}