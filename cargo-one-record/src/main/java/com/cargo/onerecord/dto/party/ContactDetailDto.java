package com.cargo.onerecord.dto.party;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactDetailDto {
    /** PHONE, MOBILE, EMAIL, FAX, WEBSITE, OTHER */
    private String contactType;
    private String contactValue;
}