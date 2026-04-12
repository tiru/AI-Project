package com.cargo.onerecord.dto.shipment;

import com.cargo.onerecord.dto.common.DimensionsDto;
import com.cargo.onerecord.dto.common.WeightDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PieceResponse {

    @JsonProperty("@type")
    private String type = "cargo:Piece";

    private UUID id;
    private String logisticsObjectRef;
    private UUID shipmentId;
    private Boolean coload;
    private String goodsDescription;
    private String packagedItemIdentifier;
    private String loadType;
    private Boolean stackable;
    private Boolean turnable;
    private WeightDto grossWeight;
    private DimensionsDto dimensions;
    private List<String> specialHandlingCodes;
    private Integer revision;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}