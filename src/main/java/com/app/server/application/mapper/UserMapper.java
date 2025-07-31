package com.app.server.application.mapper;

import com.app.server.domain.User;
import com.app.server.infrastructure.controller.AuthController;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

/**
 * User 엔티티와 DTO 간의 자동 매핑을 처리하는 MapStruct
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "role", ignore = true) // role은 서비스 로직에서 직접 설정할거니까 무시하는걸로.
    @Mapping(target = "deleted", ignore = true)
    User toEntity(AuthController.SignUpRequest request);

    AuthController.UserResponse toResponse(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntity(AuthController.UserUpdateRequest request, @MappingTarget User user);
}