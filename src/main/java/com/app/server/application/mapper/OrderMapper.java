package com.app.server.application.mapper;

import com.app.server.domain.InboundOrder;
import com.app.server.domain.OutboundOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // InboundOrder Entity → Response DTO 변환
    InboundOrder.Response toResponse(InboundOrder inboundOrder);

    // InboundOrder CreateRequest → Entity 변환
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "processedAt", ignore = true)
    InboundOrder toEntity(InboundOrder.CreateRequest request);

    // InboundOrder UpdateRequest → Entity 변환 (기존 엔티티 업데이트)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "processedAt", ignore = true)
    void updateInboundOrder(InboundOrder.UpdateRequest request, @MappingTarget InboundOrder inboundOrder);

    // OutboundOrder Entity → Response DTO 변환
    OutboundOrder.Response toResponse(OutboundOrder outboundOrder);

    // OutboundOrder CreateRequest → Entity 변환
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "processedAt", ignore = true)
    OutboundOrder toEntity(OutboundOrder.CreateRequest request);

    // OutboundOrder UpdateRequest → Entity 변환 (기존 엔티티 업데이트)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "processedAt", ignore = true)
    void updateOutboundOrder(OutboundOrder.UpdateRequest request, @MappingTarget OutboundOrder outboundOrder);
}