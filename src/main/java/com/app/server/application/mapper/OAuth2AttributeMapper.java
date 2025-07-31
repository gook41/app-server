package com.app.server.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Map;

@Mapper(
        componentModel = "spring", // 스프링 빈으로 등록
        unmappedTargetPolicy = ReportingPolicy.IGNORE // 매핑되지 않은 필드는 무시
)
public interface OAuth2AttributeMapper {

    default UnifiedProviderInfo toGoogleInfo(Map<String, Object> attributes){
        String providerId = String.valueOf(attributes.get("sub"));
        String provider = "google"; // provider 필드는 "google" 상수로 채움
        String nickname = String.valueOf(attributes.get("name"));
        String email = String.valueOf(attributes.get("email"));
        return new UnifiedProviderInfo(providerId, provider, nickname, email);
    }

    // Naver는 (Map)attributes에 'response' 라는 키로 날라옴.
    default UnifiedProviderInfo toNaverInfo(Map<String, Object> attributes) {
        @SuppressWarnings("unchecked")
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        String providerId = String.valueOf(response.get("id"));
        String provider = "naver";
        String nickname =  String.valueOf(response.get("nickname"));
        String email = String.valueOf(response.get("email"));
        return new UnifiedProviderInfo(providerId, provider, nickname, email);
    }

    // Kakao는 'kakao_account' 객체 안에 'profile' 객체 안에 데이터가 있음. 2번 감쌌음.
    default UnifiedProviderInfo toKakaoInfo(Map<String, Object> attributes){
        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        @SuppressWarnings("unchecked")
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String providerId = String.valueOf(attributes.get("id"));
        String provider = "kakao";
        String nickname =  String.valueOf(profile.get("nickname"));
        String email = String.valueOf(kakaoAccount.get("email"));
        return new UnifiedProviderInfo(providerId, provider, nickname, email);
    }
}