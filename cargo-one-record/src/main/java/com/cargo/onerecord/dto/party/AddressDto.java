package com.cargo.onerecord.dto.party;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    private String streetAddressLine1;
    private String streetAddressLine2;
    private String postalCode;
    private String cityName;
    private String regionCode;
    /** ISO 3166-1 alpha-2 (e.g. AE, US, GB) */
    private String countryCode;
    /** IATA 3-letter airport/city code (e.g. DXB, JFK) */
    private String iataLocationCode;
    private Double latitude;
    private Double longitude;
}