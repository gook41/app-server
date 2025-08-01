package com.app.server.exception;

/**
 * 인증 관련 예외를 처리하는 기본 예외 클래스
 */
public class AuthenticationException extends BusinessException {
    
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}