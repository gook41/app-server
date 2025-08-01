package com.app.server.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Map;

/**
 * OAuth2에서 받아온 Attributes를 우리 DB에 맞게 매핑하는 매퍼
 * 구글 : openid,profile,email
 * 네이버 : 닉,프사,이메일
 * 카카오 : 닉,프사
 */
@Mapper(
        componentModel = "spring", // 스프링 빈으로 등록
        unmappedTargetPolicy = ReportingPolicy.IGNORE // 매핑되지 않은 필드는 무시
)
public interface OAuth2AttributeMapper {

    // 구글 : email,profile,openid
    default UnifiedProviderInfo toGoogleInfo(Map<String, Object> attributes){
        String providerId = String.valueOf(attributes.get("sub"));
        String provider = "google";
        String nickname = String.valueOf(attributes.get("openid"));
        String email = String.valueOf(attributes.get("email"));
        String profile = String.valueOf(attributes.get("profile"));
        return new UnifiedProviderInfo(providerId, provider, nickname,email,null,profile);
    }

    // Naver는 (Map)attributes에 'response' 라는 키로 날라옴.
    // 네이버 : 회원이름(선택),이메일, 별명,프로필 사진
    default UnifiedProviderInfo toNaverInfo(Map<String, Object> attributes) {
        @SuppressWarnings("unchecked")
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        String providerId = String.valueOf(response.get("id"));
        String provider = "naver";
        String name = String.valueOf(response.get("name"));
        String email = String.valueOf(response.get("email"));
        String nickname =  String.valueOf(response.get("nickname"));
        String profileImage = String.valueOf(response.get("profile_image"));
        return new UnifiedProviderInfo(providerId, provider, nickname, profileImage,name,email);
    }

    // Kakao는 'kakao_account' 객체 안에 'profile' 객체 안에 데이터가 있음. 2번 감쌌음.
    // 카카오 : 닉네임,프사
    default UnifiedProviderInfo toKakaoInfo(Map<String, Object> attributes){
        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        @SuppressWarnings("unchecked")
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String providerId = String.valueOf(attributes.get("id"));
        String provider = "kakao";
        String nickname =  String.valueOf(profile.get("nickname"));
//        String email = String.valueOf(kakaoAccount.get("email")); // 선택
        String profileImage = String.valueOf(profile.get("profile_image_url"));
        return new UnifiedProviderInfo(providerId, provider, nickname, profileImage,null,null);
    }
}