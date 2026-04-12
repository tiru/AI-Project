package com.cargo.onerecord.service;

import com.cargo.onerecord.dto.event.LogisticsEventRequest;
import com.cargo.onerecord.dto.event.LogisticsEventResponse;
import com.cargo.onerecord.exception.ResourceNotFoundException;
import com.cargo.onerecord.model.event.LogisticsEvent;
import com.cargo.onerecord.repository.LogisticsEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogisticsEventService {

    private final LogisticsEventRepository eventRepository;

    @Value("${one-record.server.company-identifier}")
    private String companyIdentifier;

    @Transactional
    public LogisticsEventResponse create(LogisticsEventRequest request) {
        LogisticsEvent event = LogisticsEvent.builder()
                .eventCode(request.getEventCode().toUpperCase())
                .eventDescription(request.getEventDescription())
                .eventDate(request.getEventDate())
                .eventType(request.getEventType())
                .eventLocationCode(request.getEventLocationCode() != null
                        ? request.getEventLocationCode().toUpperCase() : null)
                .eventLocationName(request.getEventLocationName())
                .recordedBy(request.getRecordedBy())
                .pieceCount(request.getPieceCount())
                .weight(request.getWeight())
                .weightUnit(request.getWeightUnit())
                .logisticsObjectId(request.getLogisticsObjectId())
                .logisticsObjectTypeRef(request.getLogisticsObjectTypeRef())
                .build();

        event.setCompanyIdentifier(companyIdentifier);
        return toResponse(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public LogisticsEventResponse getById(UUID id) {
        return toResponse(eventRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("LogisticsEvent", id)));
    }

    @Transactional(readOnly = true)
    public List<LogisticsEventResponse> getByLogisticsObject(UUID logisticsObjectId) {
        return eventRepository
                .findByLogisticsObjectIdOrderByEventDateDesc(logisticsObjectId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<LogisticsEventResponse> getByLogisticsObjectPaged(UUID logisticsObjectId, Pageable pageable) {
        return eventRepository
                .findByLogisticsObjectIdOrderByEventDateDesc(logisticsObjectId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public LogisticsEventResponse getLatestEvent(UUID logisticsObjectId) {
        return eventRepository.findTopByLogisticsObjectIdOrderByEventDateDesc(logisticsObjectId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No events found for logistics object: " + logisticsObjectId));
    }

    @Transactional(readOnly = true)
    public List<LogisticsEventResponse> getByEventCode(UUID logisticsObjectId, String eventCode) {
        return eventRepository
                .findByLogisticsObjectIdAndEventCodeOrderByEventDateDesc(
                        logisticsObjectId, eventCode.toUpperCase())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // --- Mapper ---

    public LogisticsEventResponse toResponse(LogisticsEvent e) {
        return LogisticsEventResponse.builder()
                .type("cargo:LogisticsEvent")
                .id(e.getId())
                .logisticsObjectRef(e.getLogisticsObjectRef())
                .eventCode(e.getEventCode())
                .eventDescription(e.getEventDescription())
                .eventDate(e.getEventDate())
                .eventType(e.getEventType())
                .eventLocationCode(e.getEventLocationCode())
                .eventLocationName(e.getEventLocationName())
                .recordedBy(e.getRecordedBy())
                .recordedAt(e.getRecordedAt())
                .pieceCount(e.getPieceCount())
                .weight(e.getWeight())
                .weightUnit(e.getWeightUnit())
                .logisticsObjectId(e.getLogisticsObjectId())
                .logisticsObjectTypeRef(e.getLogisticsObjectTypeRef())
                .createdAt(e.getCreatedAt())
                .build();
    }
}