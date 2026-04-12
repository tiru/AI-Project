package com.cargo.onerecord.dto.common;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeightDto {

    @Positive(message = "Weight value must be positive")
    private Double value;

    /** KGM or LBR */
    private String unit = "KGM";
}