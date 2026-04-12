package com.cargo.onerecord.model.party;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ONE Record Address — physical location of a party.
 * Ref: https://onerecord.iata.org/ns/cargo#Address
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Column(name = "addr_street_line1")
    private String streetAddressLine1;

    @Column(name = "addr_street_line2")
    private String streetAddressLine2;

    @Column(name = "addr_postal_code", length = 20)
    private String postalCode;

    @Column(name = "addr_city", length = 100)
    private String cityName;

    /** ISO 3166-2 region/state code (e.g. DXB, CA) */
    @Column(name = "addr_region_code", length = 10)
    private String regionCode;

    /** ISO 3166-1 alpha-2 country code (e.g. AE, US, GB) */
    @Column(name = "addr_country_code", length = 5)
    private String countryCode;

    /** IATA 3-letter airport/city code (e.g. DXB, JFK) */
    @Column(name = "addr_iata_code", length = 5)
    private String iataLocationCode;

    @Column(name = "addr_latitude")
    private Double latitude;

    @Column(name = "addr_longitude")
    private Double longitude;
}