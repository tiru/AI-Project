package com.cargo.onerecord.service;

import com.cargo.onerecord.dto.common.DimensionsDto;
import com.cargo.onerecord.dto.common.WeightDto;
import com.cargo.onerecord.dto.shipment.PieceRequest;
import com.cargo.onerecord.dto.shipment.PieceResponse;
import com.cargo.onerecord.exception.ResourceNotFoundException;
import com.cargo.onerecord.model.common.Dimensions;
import com.cargo.onerecord.model.common.Weight;
import com.cargo.onerecord.model.shipment.Piece;
import com.cargo.onerecord.model.shipment.Shipment;
import com.cargo.onerecord.repository.PieceRepository;
import com.cargo.onerecord.repository.ShipmentRepository;
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
public class PieceService {

    private final PieceRepository pieceRepository;
    private final ShipmentRepository shipmentRepository;

    @Value("${one-record.server.company-identifier}")
    private String companyIdentifier;

    @Transactional
    public PieceResponse create(PieceRequest request) {
        Shipment shipment = null;
        if (request.getShipmentId() != null) {
            shipment = shipmentRepository.findByIdAndIsDeletedFalse(request.getShipmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shipment", request.getShipmentId()));
        }

        Piece piece = Piece.builder()
                .coload(request.getCoload())
                .goodsDescription(request.getGoodsDescription())
                .packagedItemIdentifier(request.getPackagedItemIdentifier())
                .loadType(request.getLoadType())
                .stackable(request.getStackable())
                .turnable(request.getTurnable())
                .grossWeight(toWeight(request.getGrossWeight()))
                .dimensions(toDimensions(request.getDimensions()))
                .specialHandlingCodes(request.getSpecialHandlingCodes())
                .shipment(shipment)
                .build();

        piece.setCompanyIdentifier(companyIdentifier);
        return toResponse(pieceRepository.save(piece));
    }

    @Transactional(readOnly = true)
    public PieceResponse getById(UUID id) {
        return toResponse(pieceRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Piece", id)));
    }

    @Transactional(readOnly = true)
    public Page<PieceResponse> getAll(Pageable pageable) {
        return pieceRepository.findByIsDeletedFalse(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<PieceResponse> getByShipment(UUID shipmentId) {
        if (!shipmentRepository.existsById(shipmentId)) {
            throw new ResourceNotFoundException("Shipment", shipmentId);
        }
        return pieceRepository.findByShipmentIdAndIsDeletedFalse(shipmentId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public PieceResponse update(UUID id, PieceRequest request) {
        Piece piece = pieceRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Piece", id));

        if (request.getShipmentId() != null) {
            Shipment shipment = shipmentRepository.findByIdAndIsDeletedFalse(request.getShipmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shipment", request.getShipmentId()));
            piece.setShipment(shipment);
        }

        piece.setCoload(request.getCoload());
        piece.setGoodsDescription(request.getGoodsDescription());
        piece.setPackagedItemIdentifier(request.getPackagedItemIdentifier());
        piece.setLoadType(request.getLoadType());
        piece.setStackable(request.getStackable());
        piece.setTurnable(request.getTurnable());
        piece.setGrossWeight(toWeight(request.getGrossWeight()));
        piece.setDimensions(toDimensions(request.getDimensions()));
        piece.setSpecialHandlingCodes(request.getSpecialHandlingCodes());
        piece.setRevision(piece.getRevision() + 1);

        return toResponse(pieceRepository.save(piece));
    }

    @Transactional
    public void delete(UUID id) {
        Piece piece = pieceRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Piece", id));
        piece.setIsDeleted(true);
        pieceRepository.save(piece);
    }

    // --- Mappers ---

    private Weight toWeight(WeightDto dto) {
        if (dto == null) return null;
        return new Weight(dto.getValue(), dto.getUnit());
    }

    private Dimensions toDimensions(DimensionsDto dto) {
        if (dto == null) return null;
        return new Dimensions(dto.getLength(), dto.getWidth(), dto.getHeight(), dto.getVolume(), dto.getUnit());
    }

    public PieceResponse toResponse(Piece p) {
        WeightDto weightDto = null;
        if (p.getGrossWeight() != null) {
            weightDto = new WeightDto(p.getGrossWeight().getValue(), p.getGrossWeight().getUnit());
        }
        DimensionsDto dimensionsDto = null;
        if (p.getDimensions() != null) {
            dimensionsDto = new DimensionsDto(
                    p.getDimensions().getLength(), p.getDimensions().getWidth(),
                    p.getDimensions().getHeight(), p.getDimensions().getVolume(),
                    p.getDimensions().getUnit());
        }
        return PieceResponse.builder()
                .type("cargo:Piece")
                .id(p.getId())
                .logisticsObjectRef(p.getLogisticsObjectRef())
                .shipmentId(p.getShipment() != null ? p.getShipment().getId() : null)
                .coload(p.getCoload())
                .goodsDescription(p.getGoodsDescription())
                .packagedItemIdentifier(p.getPackagedItemIdentifier())
                .loadType(p.getLoadType())
                .stackable(p.getStackable())
                .turnable(p.getTurnable())
                .grossWeight(weightDto)
                .dimensions(dimensionsDto)
                .specialHandlingCodes(p.getSpecialHandlingCodes())
                .revision(p.getRevision())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}