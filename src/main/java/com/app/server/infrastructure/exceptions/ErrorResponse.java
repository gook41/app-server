package com.app.server.infrastructure.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ErrorResponse {
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;
    
    private final List<ValidationError> validationErrors;
    
    @Getter
    @Builder
    public static class ValidationError {
        private final String field;
        private final Object rejectedValue;
        private final String message;
    }
}