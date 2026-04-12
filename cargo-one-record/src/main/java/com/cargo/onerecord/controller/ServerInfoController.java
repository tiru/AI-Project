package com.cargo.onerecord.controller;

import com.cargo.onerecord.config.OneRecordProperties;
import com.cargo.onerecord.dto.ServerInformationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Tag(name = "Server", description = "ONE Record server information")
public class ServerInfoController {

    private final OneRecordProperties props;

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
                .title(props.getTitle())
                .description(props.getDescription())
                .version(props.getVersion())
                .companyIdentifier(props.getCompanyIdentifier())
                .supportedApiVersions(props.getSupportedApiVersions())
                .supportedOntologies(props.getSupportedOntologies())
                .supportedEncodings(List.of("application/ld+json", "application/json"))
                .build();

        return ResponseEntity.ok(info);
    }
}