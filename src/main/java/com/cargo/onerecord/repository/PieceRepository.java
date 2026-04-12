package com.cargo.onerecord.repository;

import com.cargo.onerecord.model.shipment.Piece;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PieceRepository extends JpaRepository<Piece, UUID> {

    Page<Piece> findByIsDeletedFalse(Pageable pageable);

    Optional<Piece> findByIdAndIsDeletedFalse(UUID id);

    List<Piece> findByShipmentIdAndIsDeletedFalse(UUID shipmentId);

    long countByShipmentIdAndIsDeletedFalse(UUID shipmentId);
}