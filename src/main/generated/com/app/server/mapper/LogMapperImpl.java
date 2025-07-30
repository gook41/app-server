package com.app.server.mapper;

import com.app.server.domain.Log;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-30T19:26:13+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class LogMapperImpl implements LogMapper {

    @Override
    public Log.Response toResponse(Log log) {
        if ( log == null ) {
            return null;
        }

        Long id = null;
        String action = null;
        String entityType = null;
        Long entityId = null;
        Long userId = null;
        LocalDateTime timestamp = null;
        String details = null;
        String createdBy = null;

        id = log.getId();
        action = log.getAction();
        entityType = log.getEntityType();
        entityId = log.getEntityId();
        userId = log.getUserId();
        timestamp = log.getTimestamp();
        details = log.getDetails();
        createdBy = log.getCreatedBy();

        String userNickname = null;

        Log.Response response = new Log.Response( id, action, entityType, entityId, userId, userNickname, timestamp, details, createdBy );

        return response;
    }

    @Override
    public Log toEntity(Log.CreateRequest request) {
        if ( request == null ) {
            return null;
        }

        Log log = new Log();

        log.setAction( request.action() );
        log.setEntityType( request.entityType() );
        log.setEntityId( request.entityId() );
        log.setUserId( request.userId() );
        log.setDetails( request.details() );

        return log;
    }
}
