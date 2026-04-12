package com.cargo.onerecord.repository;

import com.cargo.onerecord.model.shipment.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {

    Page<Shipment> findByIsDeletedFalse(Pageable pageable);

    Optional<Shipment> findByIdAndIsDeletedFalse(UUID id);

    @Query("SELECT s FROM Shipment s WHERE s.isDeleted = false AND " +
           "(LOWER(s.goodsDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.shipperName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.consigneeName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Shipment> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<Shipment> findByShipperIdentifierAndIsDeletedFalse(String shipperIdentifier, Pageable pageable);

    Page<Shipment> findByConsigneeIdentifierAndIsDeletedFalse(String consigneeIdentifier, Pageable pageable);
}