package com.cargo.onerecord.repository;

import com.cargo.onerecord.model.party.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    Page<Company> findByIsDeletedFalse(Pageable pageable);

    Optional<Company> findByIdAndIsDeletedFalse(UUID id);

    Page<Company> findByCompanyTypeAndIsDeletedFalse(Company.CompanyType type, Pageable pageable);

    Optional<Company> findByIataCarrierCodeAndIsDeletedFalse(String iataCarrierCode);

    boolean existsByIataCarrierCode(String iataCarrierCode);

    @Query("SELECT c FROM Company c WHERE c.isDeleted = false AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.shortName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Company> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}