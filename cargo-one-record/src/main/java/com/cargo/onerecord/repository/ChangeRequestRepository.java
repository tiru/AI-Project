package com.cargo.onerecord.repository;

import com.cargo.onerecord.model.action.ChangeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChangeRequestRepository extends JpaRepository<ChangeRequest, UUID> {

    Optional<ChangeRequest> findByIdAndIsDeletedFalse(UUID id);

    Page<ChangeRequest> findByTargetObjectIdOrderByRequestedAtDesc(UUID targetObjectId, Pageable pageable);

    Page<ChangeRequest> findByStatusOrderByRequestedAtDesc(ChangeRequest.RequestStatus status, Pageable pageable);

    Page<ChangeRequest> findByRequestedByOrderByRequestedAtDesc(String requestedBy, Pageable pageable);

    Page<ChangeRequest> findByTargetObjectIdAndStatusOrderByRequestedAtDesc(
            UUID targetObjectId, ChangeRequest.RequestStatus status, Pageable pageable);
}