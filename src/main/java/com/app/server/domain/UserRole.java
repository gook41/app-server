package com.app.server.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor // final 필드만 받는 생성자 만들어줌. 개꿀.
public enum UserRole {
    // Spring Security에서는 권한(Authority) 앞에 'ROLE_' 붙이는게 국룰임.
    // hasRole('USER') 같은 메소드가 내부적으로 'ROLE_USER'를 찾기 때문.
    USER("ROLE_USER","일반 사용자"),
    ADMIN("ROLE_ADMIN","관리자");

    private final String key;
    private final String title;

}