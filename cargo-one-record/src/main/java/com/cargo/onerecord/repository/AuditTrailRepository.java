package com.cargo.onerecord.repository;

import com.cargo.onerecord.model.action.AuditTrailEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrailEntry, UUID> {

    List<AuditTrailEntry> findByLogisticsObjectIdOrderByRevisionDesc(UUID logisticsObjectId);

    Page<AuditTrailEntry> findByLogisticsObjectIdOrderByRevisionDesc(UUID logisticsObjectId, Pageable pageable);

    List<AuditTrailEntry> findByLogisticsObjectIdAndRevision(UUID logisticsObjectId, Integer revision);
}