package com.cargo.onerecord.dto.party;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CompanyRequest {

    @NotBlank(message = "Company name is required")
    private String name;

    private String shortName;

    /**
     * AIRLINE, FREIGHT_FORWARDER, GROUND_HANDLER,
     * SHIPPER, CONSIGNEE, CUSTOMS_BROKER, AIRPORT_AUTHORITY, OTHER
     */
    @NotNull(message = "Company type is required")
    private String companyType;

    /** IATA 2-letter carrier code — required for AIRLINE type (e.g. EK, QR, SQ) */
    private String iataCarrierCode;

    /** ICAO 3-letter code — for airlines (e.g. UAE, QTR) */
    private String icaoCode;

    /** CASS code — for freight forwarders */
    private String cassCode;

    private String taxId;

    @Valid
    private AddressDto address;

    @Valid
    private List<ContactDetailDto> contactDetails = new ArrayList<>();

    @Valid
    private List<PersonDto> contactPersons = new ArrayList<>();
}