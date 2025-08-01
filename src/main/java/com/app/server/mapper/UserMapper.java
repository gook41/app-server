package com.app.server.mapper;

import com.app.server.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

/**
 * User 엔티티와 DTO 간의 자동 매핑을 처리하는 MapStruct
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    /**
     * CreateRequest DTO를 User 엔티티로 변환
     * @param request 사용자 생성 요청 DTO
     * @return User 엔티티 (id, 감사 필드 등은 null)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true) // 기본값은 서비스에서 설정
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    User toEntity(User.CreateRequest request);

    /**
     * User 엔티티를 Response DTO로 변환
     * @param user User 엔티티
     * @return 사용자 응답 DTO
     */
    User.Response toResponse(User user);

    /**
     * UpdateRequest DTO를 기존 User 엔티티에 매핑
     * @param request 사용자 수정 요청 DTO
     * @param user 업데이트할 기존 User 엔티티
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // 비밀번호는 별도 API로 변경
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntity(User.UpdateRequest request, @MappingTarget User user);
}