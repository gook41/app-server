package com.app.server.application.mapper;

import com.app.server.domain.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    // Entity → Response DTO 변환
    Inventory.Response toResponse(Inventory inventory);

    // CreateRequest → Entity 변환
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Inventory toEntity(Inventory.CreateRequest request);

    // UpdateRequest → Entity 변환 (기존 엔티티 업데이트)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(Inventory.UpdateRequest request, @MappingTarget Inventory inventory);
}