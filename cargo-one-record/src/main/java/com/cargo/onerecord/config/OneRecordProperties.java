package com.cargo.onerecord.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "one-record.server")
public class OneRecordProperties {
    private String title;
    private String description;
    private String version;
    private String companyIdentifier;
    private List<String> supportedApiVersions;
    private List<String> supportedOntologies;
}