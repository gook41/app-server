package com.app.server.mapper;

import com.app.server.domain.Log;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LogMapper {

    // Entity → Response DTO 변환
    Log.Response toResponse(Log log);

    // CreateRequest → Entity 변환
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    Log toEntity(Log.CreateRequest request);
}