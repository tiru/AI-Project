package com.cargo.onerecord.service;

import com.cargo.onerecord.dto.party.*;
import com.cargo.onerecord.exception.ResourceNotFoundException;
import com.cargo.onerecord.model.party.*;
import com.cargo.onerecord.repository.CompanyRepository;
import com.cargo.onerecord.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final PersonRepository personRepository;

    @Value("${one-record.server.company-identifier}")
    private String serverCompanyIdentifier;

    // -------------------------------------------------------------------------
    // Company CRUD
    // -------------------------------------------------------------------------

    @Transactional
    public CompanyResponse create(CompanyRequest request) {
        validateCarrierCode(request);

        Company company = Company.builder()
                .name(request.getName())
                .shortName(request.getShortName())
                .companyType(Company.CompanyType.valueOf(request.getCompanyType().toUpperCase()))
                .iataCarrierCode(request.getIataCarrierCode())
                .icaoCode(request.getIcaoCode())
                .cassCode(request.getCassCode())
                .taxId(request.getTaxId())
                .address(toAddress(request.getAddress()))
                .contactDetails(toContactDetails(request.getContactDetails()))
                .build();

        company.setCompanyIdentifier(serverCompanyIdentifier);
        Company saved = companyRepository.save(company);

        // Save contact persons linked to this company
        if (request.getContactPersons() != null) {
            for (PersonDto pd : request.getContactPersons()) {
                Person person = buildPerson(pd, saved);
                personRepository.save(person);
            }
        }

        return toResponse(companyRepository.findByIdAndIsDeletedFalse(saved.getId()).orElse(saved));
    }

    @Transactional(readOnly = true)
    public CompanyResponse getById(UUID id) {
        Company company = companyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", id));
        return toResponse(company);
    }

    @Transactional(readOnly = true)
    public CompanyResponse getByIataCode(String iataCode) {
        Company company = companyRepository.findByIataCarrierCodeAndIsDeletedFalse(iataCode)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with IATA code: " + iataCode));
        return toResponse(company);
    }

    @Transactional(readOnly = true)
    public Page<CompanyResponse> getAll(Pageable pageable) {
        return companyRepository.findByIsDeletedFalse(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<CompanyResponse> getByType(String companyType, Pageable pageable) {
        Company.CompanyType type = Company.CompanyType.valueOf(companyType.toUpperCase());
        return companyRepository.findByCompanyTypeAndIsDeletedFalse(type, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<CompanyResponse> search(String keyword, Pageable pageable) {
        return companyRepository.searchByKeyword(keyword, pageable).map(this::toResponse);
    }

    @Transactional
    public CompanyResponse update(UUID id, CompanyRequest request) {
        Company company = companyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", id));

        if (request.getIataCarrierCode() != null
                && !request.getIataCarrierCode().equals(company.getIataCarrierCode())
                && companyRepository.existsByIataCarrierCode(request.getIataCarrierCode())) {
            throw new IllegalArgumentException("IATA carrier code already exists: " + request.getIataCarrierCode());
        }

        company.setName(request.getName());
        company.setShortName(request.getShortName());
        company.setCompanyType(Company.CompanyType.valueOf(request.getCompanyType().toUpperCase()));
        company.setIataCarrierCode(request.getIataCarrierCode());
        company.setIcaoCode(request.getIcaoCode());
        company.setCassCode(request.getCassCode());
        company.setTaxId(request.getTaxId());
        company.setAddress(toAddress(request.getAddress()));
        company.setContactDetails(toContactDetails(request.getContactDetails()));
        company.setRevision(company.getRevision() + 1);

        return toResponse(companyRepository.save(company));
    }

    @Transactional
    public void delete(UUID id) {
        Company company = companyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", id));
        company.setIsDeleted(true);
        companyRepository.save(company);
    }

    // -------------------------------------------------------------------------
    // Person management within a Company
    // -------------------------------------------------------------------------

    @Transactional
    public PersonDto addPerson(UUID companyId, PersonDto dto) {
        Company company = companyRepository.findByIdAndIsDeletedFalse(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", companyId));
        Person person = personRepository.save(buildPerson(dto, company));
        return toPersonDto(person);
    }

    @Transactional(readOnly = true)
    public List<PersonDto> getPersonsByCompany(UUID companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company", companyId);
        }
        return personRepository.findByCompanyIdAndIsDeletedFalse(companyId)
                .stream().map(this::toPersonDto).collect(Collectors.toList());
    }

    @Transactional
    public void removePerson(UUID companyId, UUID personId) {
        Person person = personRepository.findByIdAndIsDeletedFalse(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person", personId));
        if (person.getCompany() == null || !person.getCompany().getId().equals(companyId)) {
            throw new IllegalArgumentException("Person does not belong to this company");
        }
        person.setIsDeleted(true);
        personRepository.save(person);
    }

    // -------------------------------------------------------------------------
    // Mappers
    // -------------------------------------------------------------------------

    private void validateCarrierCode(CompanyRequest request) {
        if (request.getIataCarrierCode() != null
                && companyRepository.existsByIataCarrierCode(request.getIataCarrierCode())) {
            throw new IllegalArgumentException("IATA carrier code already registered: " + request.getIataCarrierCode());
        }
    }

    private Address toAddress(AddressDto dto) {
        if (dto == null) return null;
        return new Address(
                dto.getStreetAddressLine1(), dto.getStreetAddressLine2(),
                dto.getPostalCode(), dto.getCityName(),
                dto.getRegionCode(), dto.getCountryCode(),
                dto.getIataLocationCode(), dto.getLatitude(), dto.getLongitude());
    }

    private AddressDto toAddressDto(Address a) {
        if (a == null) return null;
        return new AddressDto(
                a.getStreetAddressLine1(), a.getStreetAddressLine2(),
                a.getPostalCode(), a.getCityName(),
                a.getRegionCode(), a.getCountryCode(),
                a.getIataLocationCode(), a.getLatitude(), a.getLongitude());
    }

    private List<ContactDetail> toContactDetails(List<ContactDetailDto> dtos) {
        if (dtos == null) return List.of();
        return dtos.stream()
                .map(d -> new ContactDetail(d.getContactType(), d.getContactValue()))
                .collect(Collectors.toList());
    }

    private List<ContactDetailDto> toContactDetailDtos(List<ContactDetail> details) {
        if (details == null) return List.of();
        return details.stream()
                .map(d -> new ContactDetailDto(d.getContactType(), d.getContactValue()))
                .collect(Collectors.toList());
    }

    private Person buildPerson(PersonDto dto, Company company) {
        Person person = Person.builder()
                .salutation(dto.getSalutation())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .jobTitle(dto.getJobTitle())
                .department(dto.getDepartment())
                .contactDetails(toContactDetails(dto.getContactDetails()))
                .company(company)
                .build();
        person.setCompanyIdentifier(serverCompanyIdentifier);
        return person;
    }

    public PersonDto toPersonDto(Person p) {
        return PersonDto.builder()
                .id(p.getId())
                .salutation(p.getSalutation())
                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .jobTitle(p.getJobTitle())
                .department(p.getDepartment())
                .contactDetails(toContactDetailDtos(p.getContactDetails()))
                .build();
    }

    public CompanyResponse toResponse(Company c) {
        List<PersonDto> persons = (c.getContactPersons() != null)
                ? c.getContactPersons().stream()
                    .filter(p -> !p.getIsDeleted())
                    .map(this::toPersonDto)
                    .collect(Collectors.toList())
                : List.of();

        return CompanyResponse.builder()
                .type("cargo:Company")
                .id(c.getId())
                .logisticsObjectRef(c.getLogisticsObjectRef())
                .name(c.getName())
                .shortName(c.getShortName())
                .companyType(c.getCompanyType().name())
                .iataCarrierCode(c.getIataCarrierCode())
                .icaoCode(c.getIcaoCode())
                .cassCode(c.getCassCode())
                .taxId(c.getTaxId())
                .address(toAddressDto(c.getAddress()))
                .contactDetails(toContactDetailDtos(c.getContactDetails()))
                .contactPersons(persons)
                .revision(c.getRevision())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}