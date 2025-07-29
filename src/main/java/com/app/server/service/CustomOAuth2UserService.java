package com.app.server.service;

import com.app.server.domain.User;
import com.app.server.domain.UserRole;
import com.app.server.dto.UnifiedProviderInfo;
import com.app.server.mapper.OAuth2AttributeMapper;
import com.app.server.repository.UserRepository;
import com.app.server.security.CustomUserDetailsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final OAuth2AttributeMapper oAuth2AttributeMapper;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. DefaultOAuth2UserService가 google/kakao/naver에 API 요청 보내서 Attribute(유저 정보) 받아옴
        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 2. provider , request user 가져옴.
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 3. registrationId에 따라 유저 정보를 매핑. UnifiedProviderInfo 형태(registrationId + attributes)로.
        UnifiedProviderInfo providerInfo = mapToUnifiedInfo(registrationId, attributes);

        // 4. Unified된 info로 DB에  유저 조회. 없으면 가입.
        User user = findOrRegisterUser(providerInfo);

        return new CustomUserDetailsService.CustomUserPrincipal(user);
        }
    private UnifiedProviderInfo mapToUnifiedInfo(String registrationId, Map<String,Object> attributes) {
        return switch (registrationId) {
            case "google" -> oAuth2AttributeMapper.toGoogleInfo(attributes);
            case "naver" -> oAuth2AttributeMapper.toNaverInfo(attributes);
            case "kakao" -> oAuth2AttributeMapper.toKakaoInfo(attributes);
            default -> throw new IllegalArgumentException("이런 provider 없음 ::: " + registrationId);
        };
    }

    // 이 메소드는 User를 찾거나, 없으면 새로 만드는 팩토리 역할을 함
    private User findOrRegisterUser(UnifiedProviderInfo providerInfo) {
        // ... (DB 조회 및 저장 로직) ...
        return userRepository.findByProviderAndProviderId(providerInfo.provider(), providerInfo.providerId())
                .orElseGet(() -> {
                // 없으면 새로 만들어서 저장 (회원가입)
                    User newUser = User.builder()
                            .email(providerInfo.email()) // 이메일은 필수
                            // 닉네임 중복 방지를 위해 providerId 일부를 붙여줌. 이건 나중에 바꿀 수 있게 해야 함.
                            .nickname(providerInfo.nickname() + "_" + providerInfo.providerId().substring(0, 6))
                            .provider(providerInfo.provider()) // "google", "kakao" 등 저장
                            .providerId(providerInfo.providerId()) // 소셜 서비스의 고유 ID 저장
                            .role(UserRole.USER) // 기본 역할 부여
                            .build();
                    return userRepository.save(newUser);
                });
    }
}



