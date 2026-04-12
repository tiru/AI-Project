package com.cargo.onerecord.dto.shipment;

import com.cargo.onerecord.dto.common.DimensionsDto;
import com.cargo.onerecord.dto.common.WeightDto;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class PieceRequest {

    private UUID shipmentId;
    private Boolean coload = false;
    private String goodsDescription;
    private String packagedItemIdentifier;

    /** LOOSE, ULD, PALLET */
    private String loadType = "LOOSE";

    private Boolean stackable = true;
    private Boolean turnable = true;

    @Valid
    private WeightDto grossWeight;

    @Valid
    private DimensionsDto dimensions;

    private List<String> specialHandlingCodes = new ArrayList<>();
}