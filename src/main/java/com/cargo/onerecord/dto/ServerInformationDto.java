package com.cargo.onerecord.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * ONE Record Server Information response.
 * Returned by GET / — the entry point of any ONE Record server.
 * Ref: https://onerecord.iata.org/ns/api#ServerInformation
 */
@Data
@Builder
public class ServerInformationDto {

    @JsonProperty("@context")
    private Object context;

    @JsonProperty("@type")
    private String type;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("version")
    private String version;

    @JsonProperty("companyIdentifier")
    private String companyIdentifier;

    @JsonProperty("supportedApiVersions")
    private List<String> supportedApiVersions;

    @JsonProperty("supportedOntologies")
    private List<String> supportedOntologies;

    @JsonProperty("supportedEncodings")
    private List<String> supportedEncodings;
}