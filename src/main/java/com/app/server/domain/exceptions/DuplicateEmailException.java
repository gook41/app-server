package com.app.server.domain.exceptions;


/**
 * 중복된 이메일로 회원가입 시도 시 발생하는 예외
 */
public class DuplicateEmailException extends BusinessException {
    
    public DuplicateEmailException(String email) {
        super("이미 사용 중인 이메일입니다: " + email);
    }
    
    public DuplicateEmailException() {
        super("이미 사용 중인 이메일입니다.");
    }
}