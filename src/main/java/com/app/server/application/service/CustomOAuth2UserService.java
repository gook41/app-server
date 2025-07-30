package com.app.server.application.service;


import com.app.server.application.mapper.OAuth2AttributeMapper;
import com.app.server.domain.UserRepository;
import com.app.server.domain.UserRole;
import com.app.server.dto.UnifiedProviderInfo;
import com.app.server.security.CustomUserPrincipal;
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

        return new CustomUserPrincipal(user, attributes);
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
        // provider+providerId 조합 대신, 이메일로 유저를 식별해서 계정 중복생성 방지.
        return userRepository.findByEmail(providerInfo.email())
                .map(user -> {
                    // 이메일로 유저를 찾았는데, provider 정보가 비어있거나(일반 회원가입 유저) 다른 provider라면
                    // 기존 계정에 새로운 소셜 계정을 연결(update)해준다.
                    if (user.getProvider() == null || !user.getProvider().equals(providerInfo.provider())) {
                        user.setProvider(providerInfo.provider());
                        user.setProviderId(providerInfo.providerId());
                        // @Transactional 덕분에 메소드 끝나면 알아서 더티 체킹으로 DB에 update 쿼리 날아감.
                    }
                    return user;
                })
                .orElseGet(() -> {
                    // 이메일로 유저를 못찾았으면 신규 유저 새로 만들어서 저장.
                    User newUser = User.builder()
                            .email(providerInfo.email()) // 이메일은 필수
                            .nickname(generateUniqueNickname(providerInfo.nickname())) // 닉네임 중복 처리
                            .provider(providerInfo.provider()) // "google", "kakao" 등 저장
                            .providerId(providerInfo.providerId()) // 소셜 서비스의 고유 ID 저장
                            .role(UserRole.USER) // 기본 역할 부여
                            .build();
                    return userRepository.save(newUser);
                });
    }

    // 닉네임 중복을 피하기 위한 헬퍼 메소드.
    private String generateUniqueNickname(String nickname) {
        String baseNickname = (nickname == null || nickname.isBlank()) ? "user" : nickname;
        String finalNickname = baseNickname;
        int count = 1;
        // DB에 중복된 닉네임이 없을 때까지 _1, _2, _3... 붙여봄
        while (userRepository.existsByNickname(finalNickname)) {
            finalNickname = baseNickname + "_" + count++;
        }
        return finalNickname;
    }
}




