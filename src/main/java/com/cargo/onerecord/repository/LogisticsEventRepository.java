package com.cargo.onerecord.repository;

import com.cargo.onerecord.model.event.LogisticsEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LogisticsEventRepository extends JpaRepository<LogisticsEvent, UUID> {

    /** All events for a specific logistics object (shipment, piece, waybill, etc.) */
    List<LogisticsEvent> findByLogisticsObjectIdOrderByEventDateDesc(UUID logisticsObjectId);

    Page<LogisticsEvent> findByLogisticsObjectIdOrderByEventDateDesc(UUID logisticsObjectId, Pageable pageable);

    Optional<LogisticsEvent> findByIdAndIsDeletedFalse(UUID id);

    /** Events by code for a specific logistics object */
    List<LogisticsEvent> findByLogisticsObjectIdAndEventCodeOrderByEventDateDesc(
            UUID logisticsObjectId, String eventCode);

    /** Latest event for a logistics object */
    Optional<LogisticsEvent> findTopByLogisticsObjectIdOrderByEventDateDesc(UUID logisticsObjectId);
}