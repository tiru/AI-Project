package com.cargo.onerecord.service;

import com.cargo.onerecord.dto.transport.*;
import com.cargo.onerecord.exception.ResourceNotFoundException;
import com.cargo.onerecord.model.transport.TransportMeans;
import com.cargo.onerecord.model.transport.TransportSegment;
import com.cargo.onerecord.repository.TransportMeansRepository;
import com.cargo.onerecord.repository.TransportSegmentRepository;
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
public class TransportService {

    private final TransportMeansRepository transportMeansRepository;
    private final TransportSegmentRepository transportSegmentRepository;

    @Value("${one-record.server.company-identifier}")
    private String companyIdentifier;

    // --- TransportMeans ---

    @Transactional
    public TransportMeansResponse createMeans(TransportMeansRequest request) {
        TransportMeans means = TransportMeans.builder()
                .transportMode(TransportMeans.TransportMode.valueOf(request.getTransportMode().toUpperCase()))
                .flightNumber(request.getFlightNumber())
                .aircraftType(request.getAircraftType())
                .registrationNumber(request.getRegistrationNumber())
                .operatingCarrierCode(request.getOperatingCarrierCode() != null
                        ? request.getOperatingCarrierCode().toUpperCase() : null)
                .operatingCarrierName(request.getOperatingCarrierName())
                .vehicleIdentifier(request.getVehicleIdentifier())
                .maxPayloadKg(request.getMaxPayloadKg())
                .maxVolumeCbm(request.getMaxVolumeCbm())
                .build();
        means.setCompanyIdentifier(companyIdentifier);
        return toMeansResponse(transportMeansRepository.save(means));
    }

    @Transactional(readOnly = true)
    public TransportMeansResponse getMeansById(UUID id) {
        return toMeansResponse(transportMeansRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("TransportMeans", id)));
    }

    @Transactional(readOnly = true)
    public Page<TransportMeansResponse> getAllMeans(Pageable pageable) {
        return transportMeansRepository.findByIsDeletedFalse(pageable).map(this::toMeansResponse);
    }

    @Transactional(readOnly = true)
    public List<TransportMeansResponse> getByCarrier(String carrierCode) {
        return transportMeansRepository.findByOperatingCarrierCodeAndIsDeletedFalse(carrierCode.toUpperCase())
                .stream().map(this::toMeansResponse).collect(Collectors.toList());
    }

    @Transactional
    public TransportMeansResponse updateMeans(UUID id, TransportMeansRequest request) {
        TransportMeans means = transportMeansRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("TransportMeans", id));
        means.setTransportMode(TransportMeans.TransportMode.valueOf(request.getTransportMode().toUpperCase()));
        means.setFlightNumber(request.getFlightNumber());
        means.setAircraftType(request.getAircraftType());
        means.setRegistrationNumber(request.getRegistrationNumber());
        means.setOperatingCarrierCode(request.getOperatingCarrierCode());
        means.setOperatingCarrierName(request.getOperatingCarrierName());
        means.setVehicleIdentifier(request.getVehicleIdentifier());
        means.setMaxPayloadKg(request.getMaxPayloadKg());
        means.setMaxVolumeCbm(request.getMaxVolumeCbm());
        means.setRevision(means.getRevision() + 1);
        return toMeansResponse(transportMeansRepository.save(means));
    }

    @Transactional
    public void deleteMeans(UUID id) {
        TransportMeans means = transportMeansRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("TransportMeans", id));
        means.setIsDeleted(true);
        transportMeansRepository.save(means);
    }

    // --- TransportSegment ---

    @Transactional
    public TransportSegmentResponse createSegment(TransportSegmentRequest request) {
        TransportMeans means = null;
        if (request.getTransportMeansId() != null) {
            means = transportMeansRepository.findByIdAndIsDeletedFalse(request.getTransportMeansId())
                    .orElseThrow(() -> new ResourceNotFoundException("TransportMeans", request.getTransportMeansId()));
        }
        TransportSegment segment = TransportSegment.builder()
                .sequenceNumber(request.getSequenceNumber())
                .departureLocationCode(request.getDepartureLocationCode().toUpperCase())
                .departureLocationName(request.getDepartureLocationName())
                .scheduledDeparture(request.getScheduledDeparture())
                .actualDeparture(request.getActualDeparture())
                .arrivalLocationCode(request.getArrivalLocationCode().toUpperCase())
                .arrivalLocationName(request.getArrivalLocationName())
                .scheduledArrival(request.getScheduledArrival())
                .actualArrival(request.getActualArrival())
                .transportMeans(means)
                .transportIdentifier(request.getTransportIdentifier())
                .segmentStatus(request.getSegmentStatus())
                .shipmentId(request.getShipmentId())
                .loadReference(request.getLoadReference())
                .bookedWeightKg(request.getBookedWeightKg())
                .bookedVolumeCbm(request.getBookedVolumeCbm())
                .build();
        segment.setCompanyIdentifier(companyIdentifier);
        return toSegmentResponse(transportSegmentRepository.save(segment));
    }

    @Transactional(readOnly = true)
    public TransportSegmentResponse getSegmentById(UUID id) {
        return toSegmentResponse(transportSegmentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("TransportSegment", id)));
    }

    @Transactional(readOnly = true)
    public List<TransportSegmentResponse> getSegmentsByShipment(UUID shipmentId) {
        return transportSegmentRepository
                .findByShipmentIdAndIsDeletedFalseOrderBySequenceNumber(shipmentId)
                .stream().map(this::toSegmentResponse).collect(Collectors.toList());
    }

    @Transactional
    public TransportSegmentResponse updateSegmentStatus(UUID id, String status) {
        TransportSegment segment = transportSegmentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("TransportSegment", id));
        segment.setSegmentStatus(status.toUpperCase());
        segment.setRevision(segment.getRevision() + 1);
        return toSegmentResponse(transportSegmentRepository.save(segment));
    }

    @Transactional
    public void deleteSegment(UUID id) {
        TransportSegment segment = transportSegmentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("TransportSegment", id));
        segment.setIsDeleted(true);
        transportSegmentRepository.save(segment);
    }

    // --- Mappers ---

    public TransportMeansResponse toMeansResponse(TransportMeans m) {
        return TransportMeansResponse.builder()
                .type("cargo:TransportMeans")
                .id(m.getId())
                .logisticsObjectRef(m.getLogisticsObjectRef())
                .transportMode(m.getTransportMode().name())
                .flightNumber(m.getFlightNumber())
                .aircraftType(m.getAircraftType())
                .registrationNumber(m.getRegistrationNumber())
                .operatingCarrierCode(m.getOperatingCarrierCode())
                .operatingCarrierName(m.getOperatingCarrierName())
                .vehicleIdentifier(m.getVehicleIdentifier())
                .maxPayloadKg(m.getMaxPayloadKg())
                .maxVolumeCbm(m.getMaxVolumeCbm())
                .revision(m.getRevision())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }

    public TransportSegmentResponse toSegmentResponse(TransportSegment s) {
        return TransportSegmentResponse.builder()
                .type("cargo:TransportSegment")
                .id(s.getId())
                .logisticsObjectRef(s.getLogisticsObjectRef())
                .sequenceNumber(s.getSequenceNumber())
                .departureLocationCode(s.getDepartureLocationCode())
                .departureLocationName(s.getDepartureLocationName())
                .scheduledDeparture(s.getScheduledDeparture())
                .actualDeparture(s.getActualDeparture())
                .arrivalLocationCode(s.getArrivalLocationCode())
                .arrivalLocationName(s.getArrivalLocationName())
                .scheduledArrival(s.getScheduledArrival())
                .actualArrival(s.getActualArrival())
                .transportMeansId(s.getTransportMeans() != null ? s.getTransportMeans().getId() : null)
                .transportIdentifier(s.getTransportIdentifier())
                .segmentStatus(s.getSegmentStatus())
                .shipmentId(s.getShipmentId())
                .loadReference(s.getLoadReference())
                .bookedWeightKg(s.getBookedWeightKg())
                .bookedVolumeCbm(s.getBookedVolumeCbm())
                .revision(s.getRevision())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}