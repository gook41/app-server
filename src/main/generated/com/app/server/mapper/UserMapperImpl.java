package com.app.server.mapper;

import com.app.server.domain.User;
import com.app.server.domain.UserRole;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-26T20:48:44+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(User.CreateRequest request) {
        if ( request == null ) {
            return null;
        }

        User user = new User();

        user.setNickname( request.nickname() );
        user.setPassword( request.password() );
        user.setEmail( request.email() );

        return user;
    }

    @Override
    public User.Response toResponse(User user) {
        if ( user == null ) {
            return null;
        }

        Long id = null;
        String email = null;
        String nickname = null;
        String name = null;
        UserRole role = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;
        String createdBy = null;
        String updatedBy = null;
        Boolean deleted = null;

        id = user.getId();
        email = user.getEmail();
        nickname = user.getNickname();
        name = user.getName();
        role = user.getRole();
        createdAt = user.getCreatedAt();
        updatedAt = user.getUpdatedAt();
        createdBy = user.getCreatedBy();
        updatedBy = user.getUpdatedBy();
        deleted = user.isDeleted();

        User.Response response = new User.Response( id, email, nickname, name, role, createdAt, updatedAt, createdBy, updatedBy, deleted );

        return response;
    }

    @Override
    public void updateEntity(User.UpdateRequest request, User user) {
        if ( request == null ) {
            return;
        }

        user.setNickname( request.nickname() );
        user.setEmail( request.email() );
        user.setRole( request.role() );
        user.setName( request.name() );
    }
}
