package com.cargo.onerecord.dto.party;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonDto {
    private UUID id;
    /** Mr, Ms, Mrs, Dr, Prof */
    private String salutation;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String jobTitle;
    private String department;

    @Builder.Default
    private List<ContactDetailDto> contactDetails = new ArrayList<>();
}