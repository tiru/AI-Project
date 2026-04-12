package com.cargo.onerecord.dto.shipment;

import com.cargo.onerecord.dto.common.WeightDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ShipmentRequest {

    @NotBlank(message = "Goods description is required")
    private String goodsDescription;

    private Double declaredValueForCarriage;
    private Double declaredValueForCustoms;
    private String declaredValueCurrency = "USD";

    @Valid
    private WeightDto totalGrossWeight;

    private String shipperIdentifier;

    @NotBlank(message = "Shipper name is required")
    private String shipperName;

    private String consigneeIdentifier;

    @NotBlank(message = "Consignee name is required")
    private String consigneeName;

    private List<String> specialHandlingCodes = new ArrayList<>();
}