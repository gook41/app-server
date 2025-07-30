package com.app.server.domain.BusinessExceptions;

import com.app.server.infrastructure.exception.AuthenticationException;

/**
 * 잘못된 자격 증명(이메일/비밀번호)으로 인한 인증 실패 예외
 */
public class InvalidCredentialsException extends AuthenticationException {
    
    public InvalidCredentialsException() {
        super("이메일 또는 비밀번호가 올바르지 않습니다.");
    }
    
    public InvalidCredentialsException(String message) {
        super(message);
    }
}