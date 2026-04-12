package com.cargo.onerecord.service;

import com.cargo.onerecord.dto.shipment.WaybillRequest;
import com.cargo.onerecord.dto.shipment.WaybillResponse;
import com.cargo.onerecord.exception.ResourceNotFoundException;
import com.cargo.onerecord.model.shipment.Shipment;
import com.cargo.onerecord.model.shipment.Waybill;
import com.cargo.onerecord.repository.ShipmentRepository;
import com.cargo.onerecord.repository.WaybillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WaybillService {

    private final WaybillRepository waybillRepository;
    private final ShipmentRepository shipmentRepository;

    @Value("${one-record.server.company-identifier}")
    private String companyIdentifier;

    @Transactional
    public WaybillResponse create(WaybillRequest request) {
        if (waybillRepository.existsByWaybillNumber(request.getWaybillNumber())) {
            throw new IllegalArgumentException("Waybill number already exists: " + request.getWaybillNumber());
        }

        Shipment shipment = null;
        if (request.getShipmentId() != null) {
            shipment = shipmentRepository.findByIdAndIsDeletedFalse(request.getShipmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shipment", request.getShipmentId()));
        }

        Waybill waybill = Waybill.builder()
                .waybillNumber(request.getWaybillNumber())
                .waybillType(request.getWaybillType())
                .carrierCode(request.getCarrierCode())
                .originCode(request.getOriginCode())
                .originName(request.getOriginName())
                .destinationCode(request.getDestinationCode())
                .destinationName(request.getDestinationName())
                .issueDate(request.getIssueDate())
                .issuePlace(request.getIssuePlace())
                .numberOfPieces(request.getNumberOfPieces())
                .totalWeight(request.getTotalWeight())
                .totalWeightUnit(request.getTotalWeightUnit())
                .natureAndQuantity(request.getNatureAndQuantity())
                .shipment(shipment)
                .build();

        waybill.setCompanyIdentifier(companyIdentifier);
        return toResponse(waybillRepository.save(waybill));
    }

    @Transactional(readOnly = true)
    public WaybillResponse getById(UUID id) {
        return toResponse(waybillRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Waybill", id)));
    }

    @Transactional(readOnly = true)
    public WaybillResponse getByWaybillNumber(String waybillNumber) {
        return toResponse(waybillRepository.findByWaybillNumberAndIsDeletedFalse(waybillNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Waybill not found: " + waybillNumber)));
    }

    @Transactional(readOnly = true)
    public Page<WaybillResponse> getAll(Pageable pageable) {
        return waybillRepository.findByIsDeletedFalse(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<WaybillResponse> getByCarrier(String carrierCode, Pageable pageable) {
        return waybillRepository.findByCarrierCodeAndIsDeletedFalse(carrierCode, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<WaybillResponse> getByRoute(String origin, String destination, Pageable pageable) {
        return waybillRepository
                .findByOriginCodeAndDestinationCodeAndIsDeletedFalse(origin, destination, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public WaybillResponse update(UUID id, WaybillRequest request) {
        Waybill waybill = waybillRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Waybill", id));

        if (!waybill.getWaybillNumber().equals(request.getWaybillNumber())
                && waybillRepository.existsByWaybillNumber(request.getWaybillNumber())) {
            throw new IllegalArgumentException("Waybill number already exists: " + request.getWaybillNumber());
        }

        if (request.getShipmentId() != null) {
            Shipment shipment = shipmentRepository.findByIdAndIsDeletedFalse(request.getShipmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shipment", request.getShipmentId()));
            waybill.setShipment(shipment);
        }

        waybill.setWaybillNumber(request.getWaybillNumber());
        waybill.setWaybillType(request.getWaybillType());
        waybill.setCarrierCode(request.getCarrierCode());
        waybill.setOriginCode(request.getOriginCode());
        waybill.setOriginName(request.getOriginName());
        waybill.setDestinationCode(request.getDestinationCode());
        waybill.setDestinationName(request.getDestinationName());
        waybill.setIssueDate(request.getIssueDate());
        waybill.setIssuePlace(request.getIssuePlace());
        waybill.setNumberOfPieces(request.getNumberOfPieces());
        waybill.setTotalWeight(request.getTotalWeight());
        waybill.setTotalWeightUnit(request.getTotalWeightUnit());
        waybill.setNatureAndQuantity(request.getNatureAndQuantity());
        waybill.setRevision(waybill.getRevision() + 1);

        return toResponse(waybillRepository.save(waybill));
    }

    @Transactional
    public void delete(UUID id) {
        Waybill waybill = waybillRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Waybill", id));
        waybill.setIsDeleted(true);
        waybillRepository.save(waybill);
    }

    // --- Mapper ---

    public WaybillResponse toResponse(Waybill w) {
        return WaybillResponse.builder()
                .type("cargo:Waybill")
                .id(w.getId())
                .logisticsObjectRef(w.getLogisticsObjectRef())
                .waybillNumber(w.getWaybillNumber())
                .waybillType(w.getWaybillType())
                .carrierCode(w.getCarrierCode())
                .originCode(w.getOriginCode())
                .originName(w.getOriginName())
                .destinationCode(w.getDestinationCode())
                .destinationName(w.getDestinationName())
                .issueDate(w.getIssueDate())
                .issuePlace(w.getIssuePlace())
                .numberOfPieces(w.getNumberOfPieces())
                .totalWeight(w.getTotalWeight())
                .totalWeightUnit(w.getTotalWeightUnit())
                .natureAndQuantity(w.getNatureAndQuantity())
                .shipmentId(w.getShipment() != null ? w.getShipment().getId() : null)
                .revision(w.getRevision())
                .createdAt(w.getCreatedAt())
                .updatedAt(w.getUpdatedAt())
                .build();
    }
}