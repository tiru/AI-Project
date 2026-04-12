package com.cargo.onerecord.service;

import com.cargo.onerecord.dto.common.WeightDto;
import com.cargo.onerecord.dto.kafka.CargoKafkaEvent;
import com.cargo.onerecord.dto.shipment.ShipmentRequest;
import com.cargo.onerecord.dto.shipment.ShipmentResponse;
import com.cargo.onerecord.exception.ResourceNotFoundException;
import com.cargo.onerecord.kafka.CargoEventProducer;
import com.cargo.onerecord.kafka.KafkaTopicConfig;
import com.cargo.onerecord.model.common.Weight;
import com.cargo.onerecord.model.shipment.Shipment;
import com.cargo.onerecord.repository.ShipmentRepository;
import com.cargo.onerecord.repository.WaybillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final WaybillRepository waybillRepository;
    private final CargoEventProducer eventProducer;

    @Value("${one-record.server.company-identifier}")
    private String companyIdentifier;

    @Transactional
    public ShipmentResponse create(ShipmentRequest request) {
        Shipment shipment = Shipment.builder()
                .goodsDescription(request.getGoodsDescription())
                .declaredValueForCarriage(request.getDeclaredValueForCarriage())
                .declaredValueForCustoms(request.getDeclaredValueForCustoms())
                .declaredValueCurrency(request.getDeclaredValueCurrency())
                .totalGrossWeight(toWeight(request.getTotalGrossWeight()))
                .shipperIdentifier(request.getShipperIdentifier())
                .shipperName(request.getShipperName())
                .consigneeIdentifier(request.getConsigneeIdentifier())
                .consigneeName(request.getConsigneeName())
                .specialHandlingCodes(request.getSpecialHandlingCodes())
                .build();

        shipment.setCompanyIdentifier(companyIdentifier);
        Shipment saved = shipmentRepository.save(shipment);
        ShipmentResponse response = toResponse(saved);

        eventProducer.publish(KafkaTopicConfig.TOPIC_SHIPMENTS,
                eventProducer.buildEvent("SHIPMENT_CREATED",
                        saved.getId().toString(), "cargo:Shipment",
                        saved.getLogisticsObjectRef(), "CREATED",
                        currentUser(), response));
        return response;
    }

    @Transactional(readOnly = true)
    public ShipmentResponse getById(UUID id) {
        Shipment shipment = shipmentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", id));
        return toResponse(shipment);
    }

    @Transactional(readOnly = true)
    public Page<ShipmentResponse> getAll(Pageable pageable) {
        return shipmentRepository.findByIsDeletedFalse(pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ShipmentResponse> search(String keyword, Pageable pageable) {
        return shipmentRepository.searchByKeyword(keyword, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public ShipmentResponse update(UUID id, ShipmentRequest request) {
        Shipment shipment = shipmentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", id));

        shipment.setGoodsDescription(request.getGoodsDescription());
        shipment.setDeclaredValueForCarriage(request.getDeclaredValueForCarriage());
        shipment.setDeclaredValueForCustoms(request.getDeclaredValueForCustoms());
        shipment.setDeclaredValueCurrency(request.getDeclaredValueCurrency());
        shipment.setTotalGrossWeight(toWeight(request.getTotalGrossWeight()));
        shipment.setShipperIdentifier(request.getShipperIdentifier());
        shipment.setShipperName(request.getShipperName());
        shipment.setConsigneeIdentifier(request.getConsigneeIdentifier());
        shipment.setConsigneeName(request.getConsigneeName());
        shipment.setSpecialHandlingCodes(request.getSpecialHandlingCodes());
        shipment.setRevision(shipment.getRevision() + 1);

        ShipmentResponse response = toResponse(shipmentRepository.save(shipment));
        eventProducer.publish(KafkaTopicConfig.TOPIC_SHIPMENTS,
                eventProducer.buildEvent("SHIPMENT_UPDATED",
                        id.toString(), "cargo:Shipment",
                        shipment.getLogisticsObjectRef(), "UPDATED",
                        currentUser(), response));
        return response;
    }

    @Transactional
    public void delete(UUID id) {
        Shipment shipment = shipmentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", id));
        shipment.setIsDeleted(true);
        shipmentRepository.save(shipment);

        eventProducer.publish(KafkaTopicConfig.TOPIC_SHIPMENTS,
                eventProducer.buildEvent("SHIPMENT_DELETED",
                        id.toString(), "cargo:Shipment",
                        shipment.getLogisticsObjectRef(), "DELETED",
                        currentUser(), null));
    }

    // --- Helpers ---

    private String currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }

    // --- Mappers ---

    private Weight toWeight(WeightDto dto) {
        if (dto == null) return null;
        return new Weight(dto.getValue(), dto.getUnit());
    }

    private WeightDto toWeightDto(Weight weight) {
        if (weight == null) return null;
        return new WeightDto(weight.getValue(), weight.getUnit());
    }

    public ShipmentResponse toResponse(Shipment s) {
        String waybillNumber = null;
        if (s.getWaybill() != null) {
            waybillNumber = s.getWaybill().getWaybillNumber();
        } else {
            waybillNumber = waybillRepository
                    .findAll()
                    .stream()
                    .filter(w -> w.getShipment() != null && w.getShipment().getId().equals(s.getId()))
                    .map(w -> w.getWaybillNumber())
                    .findFirst()
                    .orElse(null);
        }

        return ShipmentResponse.builder()
                .type("cargo:Shipment")
                .id(s.getId())
                .logisticsObjectRef(s.getLogisticsObjectRef())
                .goodsDescription(s.getGoodsDescription())
                .declaredValueForCarriage(s.getDeclaredValueForCarriage())
                .declaredValueForCustoms(s.getDeclaredValueForCustoms())
                .declaredValueCurrency(s.getDeclaredValueCurrency())
                .totalGrossWeight(toWeightDto(s.getTotalGrossWeight()))
                .shipperIdentifier(s.getShipperIdentifier())
                .shipperName(s.getShipperName())
                .consigneeIdentifier(s.getConsigneeIdentifier())
                .consigneeName(s.getConsigneeName())
                .specialHandlingCodes(s.getSpecialHandlingCodes())
                .pieceCount(s.getPieces() != null ? s.getPieces().size() : 0)
                .waybillNumber(waybillNumber)
                .revision(s.getRevision())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}