package com.cargo.onerecord.repository;

import com.cargo.onerecord.model.booking.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    Optional<Booking> findByIdAndIsDeletedFalse(UUID id);

    Optional<Booking> findByBookingNumberAndIsDeletedFalse(String bookingNumber);

    boolean existsByBookingNumber(String bookingNumber);

    Page<Booking> findByIsDeletedFalse(Pageable pageable);

    Page<Booking> findByStatusAndIsDeletedFalse(Booking.BookingStatus status, Pageable pageable);

    Page<Booking> findByCarrierCodeAndIsDeletedFalse(String carrierCode, Pageable pageable);

    Optional<Booking> findByShipmentIdAndIsDeletedFalse(UUID shipmentId);

    Page<Booking> findByOriginCodeAndDestinationCodeAndIsDeletedFalse(
            String origin, String destination, Pageable pageable);
}