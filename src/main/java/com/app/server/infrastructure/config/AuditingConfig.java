package com.app.server.infrastructure.config;

import com.app.server.security.CustomUserPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () ->{
            // Spring Security 컨텍스트에서 현재 인증 정보를 가져옴. 쓰레드 로컬에 저장돼서 아무데서나 접근 가능.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // 인증 정보가 없거나, 인증되지 않았거나, Principal 객체가 내 만든 CustomUserPrincipal 타입이 아닌 경우
            // 예를 들어, 시스템 부팅 시점이나 인증 필터 돌기 전에는 인증 정보가 없을 수 있음.
            if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof CustomUserPrincipal)) {
                // 이럴 땐 '익명 사용자'나 'SYSTEM' 같은 기본값을 반환하게 처리. null 반환하면 안됨.
                return Optional.of("anonymousUser");
            }
            // 이 이메일이 BaseEntity의 createdBy, updatedBy 필드에 박히는 거임.
            CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
            return Optional.of(principal.getUsername());
        };
    }
}
