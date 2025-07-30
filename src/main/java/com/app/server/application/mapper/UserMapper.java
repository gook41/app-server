package com.app.server.application.mapper;

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
    // DTO에 없는 필드(id, 감사필드 등)는 알아서 매핑 안하니까 굳이 ignore 안써도 됨.
    @Mapping(target = "role", ignore = true) // role은 서비스 로직에서 직접 설정할거니까 무시
    @Mapping(target = "deleted", ignore = true) // deleted는 기본값이 있으니 무시
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
    // id, createdAt 처럼 DTO에 없고, 엔티티에 setter도 없는 필드는 @Mapping 자체를 빼야함.
    @Mapping(target = "password", ignore = true) // 비밀번호는 별도 API로 변경하므로 무시
    @Mapping(target = "deleted", ignore = true) // 삭제 상태는 별도 API로 변경하므로 무시
    void updateEntity(User.UpdateRequest request, @MappingTarget User user);
}