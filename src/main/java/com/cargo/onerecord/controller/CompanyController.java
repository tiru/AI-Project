package com.cargo.onerecord.controller;

import com.cargo.onerecord.dto.party.CompanyRequest;
import com.cargo.onerecord.dto.party.CompanyResponse;
import com.cargo.onerecord.dto.party.PersonDto;
import com.cargo.onerecord.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/logistics-objects/companies")
@RequiredArgsConstructor
@Tag(name = "Companies", description = "ONE Record Party logistics objects — airlines, forwarders, shippers, consignees")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Create a company",
               description = "Company types: AIRLINE, FREIGHT_FORWARDER, GROUND_HANDLER, SHIPPER, CONSIGNEE, CUSTOMS_BROKER, AIRPORT_AUTHORITY, OTHER")
    public ResponseEntity<CompanyResponse> create(@Valid @RequestBody CompanyRequest request) {
        CompanyResponse response = companyService.create(request);
        URI location = URI.create("/logistics-objects/companies/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get company by ID")
    public ResponseEntity<CompanyResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(companyService.getById(id));
    }

    @GetMapping("/iata/{iataCode}")
    @Operation(summary = "Get airline by IATA carrier code",
               description = "Lookup carrier by 2-letter IATA code (e.g. EK, QR, SQ, AA)")
    public ResponseEntity<CompanyResponse> getByIataCode(@PathVariable String iataCode) {
        return ResponseEntity.ok(companyService.getByIataCode(iataCode.toUpperCase()));
    }

    @GetMapping
    @Operation(summary = "List companies",
               description = "Filter by ?type=AIRLINE or ?search=keyword. Types: AIRLINE, FREIGHT_FORWARDER, GROUND_HANDLER, SHIPPER, CONSIGNEE, CUSTOMS_BROKER, AIRPORT_AUTHORITY")
    public ResponseEntity<Page<CompanyResponse>> getAll(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(companyService.search(search, pageable));
        }
        if (type != null && !type.isBlank()) {
            return ResponseEntity.ok(companyService.getByType(type, pageable));
        }
        return ResponseEntity.ok(companyService.getAll(pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Update a company")
    public ResponseEntity<CompanyResponse> update(@PathVariable UUID id,
                                                  @Valid @RequestBody CompanyRequest request) {
        return ResponseEntity.ok(companyService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft-delete a company")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        companyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Contact Persons sub-resource ----

    @PostMapping("/{companyId}/persons")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Add a contact person to a company")
    public ResponseEntity<PersonDto> addPerson(@PathVariable UUID companyId,
                                               @Valid @RequestBody PersonDto dto) {
        PersonDto saved = companyService.addPerson(companyId, dto);
        URI location = URI.create("/logistics-objects/companies/" + companyId + "/persons/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    @GetMapping("/{companyId}/persons")
    @Operation(summary = "Get all contact persons for a company")
    public ResponseEntity<List<PersonDto>> getPersons(@PathVariable UUID companyId) {
        return ResponseEntity.ok(companyService.getPersonsByCompany(companyId));
    }

    @DeleteMapping("/{companyId}/persons/{personId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Remove a contact person from a company")
    public ResponseEntity<Void> removePerson(@PathVariable UUID companyId,
                                             @PathVariable UUID personId) {
        companyService.removePerson(companyId, personId);
        return ResponseEntity.noContent().build();
    }
}