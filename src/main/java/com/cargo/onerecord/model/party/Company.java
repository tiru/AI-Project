package com.cargo.onerecord.model.party;

import com.cargo.onerecord.model.core.LogisticsObject;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ONE Record Company — a legal entity acting as a party in cargo operations.
 * Examples: Airlines, Freight Forwarders, Ground Handlers, Shippers, Consignees.
 * Ref: https://onerecord.iata.org/ns/cargo#Company
 */
@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company extends LogisticsObject {

    /** Full legal name of the company */
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /** Short/abbreviated name (e.g. "ACME", "EK", "DHL") */
    @Column(name = "short_name", length = 50)
    private String shortName;

    /**
     * Role of this company in the cargo ecosystem.
     * AIRLINE, FREIGHT_FORWARDER, GROUND_HANDLER,
     * SHIPPER, CONSIGNEE, CUSTOMS_BROKER, AIRPORT_AUTHORITY, OTHER
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "company_type", nullable = false, length = 30)
    private CompanyType companyType;

    /** IATA 2-letter carrier code (airlines only, e.g. EK, QR, SQ) */
    @Column(name = "iata_carrier_code", length = 5)
    private String iataCarrierCode;

    /** ICAO 3-letter code (airlines only, e.g. UAE, QTR, SIA) */
    @Column(name = "icao_code", length = 5)
    private String icaoCode;

    /** IATA cargo agent CASS code (freight forwarders) */
    @Column(name = "cass_code", length = 10)
    private String cassCode;

    /** Tax / VAT identification number */
    @Column(name = "tax_id", length = 50)
    private String taxId;

    /** Physical address of this company */
    @Embedded
    private Address address;

    /** Contact channels for this company (phone, email, website) */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "company_contact_details",
            joinColumns = @JoinColumn(name = "company_id"))
    @Builder.Default
    private List<ContactDetail> contactDetails = new ArrayList<>();

    /** Key contact persons within this company */
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Person> contactPersons = new ArrayList<>();

    @PrePersist
    void prePersist() {
        setLogisticsObjectType("cargo:Company");
    }

    public enum CompanyType {
        AIRLINE,
        FREIGHT_FORWARDER,
        GROUND_HANDLER,
        SHIPPER,
        CONSIGNEE,
        CUSTOMS_BROKER,
        AIRPORT_AUTHORITY,
        OTHER
    }
}