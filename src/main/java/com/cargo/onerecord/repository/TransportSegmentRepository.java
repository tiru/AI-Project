package com.cargo.onerecord.repository;

import com.cargo.onerecord.model.transport.TransportSegment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransportSegmentRepository extends JpaRepository<TransportSegment, UUID> {

    Page<TransportSegment> findByIsDeletedFalse(Pageable pageable);

    Optional<TransportSegment> findByIdAndIsDeletedFalse(UUID id);

    List<TransportSegment> findByShipmentIdAndIsDeletedFalseOrderBySequenceNumber(UUID shipmentId);

    List<TransportSegment> findByDepartureLocationCodeAndIsDeletedFalse(String locationCode);

    List<TransportSegment> findByDepartureLocationCodeAndArrivalLocationCodeAndIsDeletedFalse(
            String departure, String arrival);
}