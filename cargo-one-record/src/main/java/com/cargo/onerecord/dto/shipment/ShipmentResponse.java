package com.cargo.onerecord.dto.shipment;

import com.cargo.onerecord.dto.common.WeightDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ShipmentResponse {

    @JsonProperty("@type")
    private String type = "cargo:Shipment";

    private UUID id;
    private String logisticsObjectRef;
    private String goodsDescription;
    private Double declaredValueForCarriage;
    private Double declaredValueForCustoms;
    private String declaredValueCurrency;
    private WeightDto totalGrossWeight;
    private String shipperIdentifier;
    private String shipperName;
    private String consigneeIdentifier;
    private String consigneeName;
    private List<String> specialHandlingCodes;
    private Integer pieceCount;
    private String waybillNumber;
    private Integer revision;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}