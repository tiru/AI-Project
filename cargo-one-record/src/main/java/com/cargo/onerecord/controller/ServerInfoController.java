package com.cargo.onerecord.controller;

import com.cargo.onerecord.dto.ServerInformationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
@Tag(name = "Server", description = "ONE Record server information")
public class ServerInfoController {

    @Value("${one-record.server.title}")
    private String title;

    @Value("${one-record.server.description}")
    private String description;

    @Value("${one-record.server.version}")
    private String version;

    @Value("${one-record.server.company-identifier}")
    private String companyIdentifier;

    @Value("${one-record.server.supported-api-versions}")
    private List<String> supportedApiVersions;

    @Value("${one-record.server.supported-ontologies}")
    private List<String> supportedOntologies;

    @GetMapping
    @Operation(
        summary = "Get server information",
        description = "Returns ONE Record server metadata — supported versions, ontologies, and company identifier. Public endpoint."
    )
    public ResponseEntity<ServerInformationDto> getServerInformation() {
        ServerInformationDto info = ServerInformationDto.builder()
                .context(Map.of(
                        "api", "https://onerecord.iata.org/ns/api#",
                        "cargo", "https://onerecord.iata.org/ns/cargo#"
                ))
                .type("api:ServerInformation")
                .title(title)
                .description(description)
                .version(version)
                .companyIdentifier(companyIdentifier)
                .supportedApiVersions(supportedApiVersions)
                .supportedOntologies(supportedOntologies)
                .supportedEncodings(List.of("application/ld+json", "application/json"))
                .build();

        return ResponseEntity.ok(info);
    }
}