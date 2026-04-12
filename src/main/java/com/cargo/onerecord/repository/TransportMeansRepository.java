package com.cargo.onerecord.repository;

import com.cargo.onerecord.model.transport.TransportMeans;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransportMeansRepository extends JpaRepository<TransportMeans, UUID> {

    Page<TransportMeans> findByIsDeletedFalse(Pageable pageable);

    Optional<TransportMeans> findByIdAndIsDeletedFalse(UUID id);

    List<TransportMeans> findByOperatingCarrierCodeAndIsDeletedFalse(String carrierCode);

    Optional<TransportMeans> findByFlightNumberAndIsDeletedFalse(String flightNumber);

    Page<TransportMeans> findByTransportModeAndIsDeletedFalse(
            TransportMeans.TransportMode mode, Pageable pageable);
}