package com.cargo.onerecord.model.party;

import com.cargo.onerecord.model.core.LogisticsObject;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ONE Record Person — an individual contact within a Company.
 * Ref: https://onerecord.iata.org/ns/cargo#Person
 */
@Entity
@Table(name = "persons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person extends LogisticsObject {

    /** Salutation: Mr, Ms, Mrs, Dr, Prof */
    @Column(name = "salutation", length = 20)
    private String salutation;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "job_title", length = 150)
    private String jobTitle;

    @Column(name = "department", length = 100)
    private String department;

    /** Contact details (phone, email, etc.) for this person */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "person_contact_details",
            joinColumns = @JoinColumn(name = "person_id"))
    @Builder.Default
    private List<ContactDetail> contactDetails = new ArrayList<>();

    /** The company this person belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @PrePersist
    void prePersist() {
        setLogisticsObjectType("cargo:Person");
    }

    /** Convenience method */
    public String getFullName() {
        return (salutation != null ? salutation + " " : "") + firstName + " " + lastName;
    }
}