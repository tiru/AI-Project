package com.cargo.onerecord.repository;

import com.cargo.onerecord.model.shipment.Waybill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WaybillRepository extends JpaRepository<Waybill, UUID> {

    Page<Waybill> findByIsDeletedFalse(Pageable pageable);

    Optional<Waybill> findByIdAndIsDeletedFalse(UUID id);

    Optional<Waybill> findByWaybillNumberAndIsDeletedFalse(String waybillNumber);

    boolean existsByWaybillNumber(String waybillNumber);

    Page<Waybill> findByCarrierCodeAndIsDeletedFalse(String carrierCode, Pageable pageable);

    Page<Waybill> findByOriginCodeAndDestinationCodeAndIsDeletedFalse(
            String originCode, String destinationCode, Pageable pageable);
}