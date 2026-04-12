package com.cargo.onerecord.model.party;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ONE Record ContactDetail — a single contact channel.
 * Ref: https://onerecord.iata.org/ns/cargo#ContactDetail
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactDetail {

    /**
     * Contact type:
     * PHONE, MOBILE, EMAIL, FAX, WEBSITE, OTHER
     */
    @Column(name = "contact_type", length = 20)
    private String contactType;

    /** The actual contact value (phone number, email address, URL, etc.) */
    @Column(name = "contact_value", length = 255)
    private String contactValue;
}