package com.cargo.onerecord.repository;

import com.cargo.onerecord.model.booking.BookingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRequestRepository extends JpaRepository<BookingRequest, UUID> {

    Optional<BookingRequest> findByIdAndIsDeletedFalse(UUID id);

    Optional<BookingRequest> findByBookingRefAndIsDeletedFalse(String bookingRef);

    boolean existsByBookingRef(String bookingRef);

    Page<BookingRequest> findByIsDeletedFalse(Pageable pageable);

    Page<BookingRequest> findByStatusAndIsDeletedFalse(BookingRequest.BookingStatus status, Pageable pageable);

    Page<BookingRequest> findByPreferredCarrierCodeAndIsDeletedFalse(String carrierCode, Pageable pageable);

    Page<BookingRequest> findByOriginCodeAndDestinationCodeAndIsDeletedFalse(
            String origin, String destination, Pageable pageable);
}