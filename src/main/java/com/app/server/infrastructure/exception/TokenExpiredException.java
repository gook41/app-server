package com.app.server.infrastructure.exception;

/**
 * 만료된 토큰으로 인한 인증 실패 예외
 */
public class TokenExpiredException extends AuthenticationException {
    
    public TokenExpiredException() {
        super("토큰이 만료되었습니다. 다시 로그인해 주세요.");
    }
    
    public TokenExpiredException(String message) {
        super(message);
    }
    
    public static TokenExpiredException forTokenType(String tokenType) {
        return new TokenExpiredException(tokenType + " 토큰이 만료되었습니다.");
    }
}