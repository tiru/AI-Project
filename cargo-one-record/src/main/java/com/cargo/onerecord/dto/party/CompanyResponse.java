package com.cargo.onerecord.dto.party;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CompanyResponse {

    @JsonProperty("@type")
    private String type = "cargo:Company";

    private UUID id;
    private String logisticsObjectRef;
    private String name;
    private String shortName;
    private String companyType;
    private String iataCarrierCode;
    private String icaoCode;
    private String cassCode;
    private String taxId;
    private AddressDto address;
    private List<ContactDetailDto> contactDetails;
    private List<PersonDto> contactPersons;
    private Integer revision;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}