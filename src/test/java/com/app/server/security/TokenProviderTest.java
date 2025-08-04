package com.app.server.security;

import com.app.server.domain.RefreshToken;
import com.app.server.domain.RefreshTokenRepository;
import com.app.server.domain.User;
import com.app.server.domain.UserRole;
import com.app.server.infrastructure.security.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenProvider 단위 테스트")
class TokenProviderTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private TokenProvider tokenProvider;

    private User testUser;
    private Authentication authentication;
    private RefreshToken testRefreshToken;

    @BeforeEach
    void setUp() {
        // TokenProvider의 설정값들을 테스트용으로 설정 (HS512는 최소 64바이트 필요)
        ReflectionTestUtils.setField(tokenProvider, "secret", "myTestSecretKeyForJWTTokenGenerationAndValidationThatIsLongEnoughForHS512Algorithm");
        ReflectionTestUtils.setField(tokenProvider, "expiration", 3600L); // 1시간
        ReflectionTestUtils.setField(tokenProvider, "refreshExpiration", 604800L); // 7일

        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("testuser")
                .password("encodedPassword")
                .role(UserRole.USER)
                .deleted(false)
                .build();

        authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        testRefreshToken = new RefreshToken();
        testRefreshToken.setId(1L);
        testRefreshToken.setUser(testUser);
        testRefreshToken.setUserId(1L);
        testRefreshToken.setToken("existing-refresh-token");
        testRefreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));
        testRefreshToken.setRevoked(false);
    }

    @Test
    @DisplayName("액세스 토큰 생성 테스트")
    void generateAccessToken_Success() {
        // when
        String token = tokenProvider.generateAccessToken(authentication);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT는 3개 부분으로 구성 (header.payload.signature)
    }

    @Test
    @DisplayName("리프레시 토큰 생성 및 저장 테스트 - 새로운 토큰")
    void createAndSaveRefreshToken_NewToken_Success() {
        // given
        given(refreshTokenRepository.findByUser(testUser)).willReturn(Optional.empty());
        given(refreshTokenRepository.save(any(RefreshToken.class))).willReturn(testRefreshToken);

        // when
        String refreshToken = tokenProvider.createAndSaveRefreshToken(testUser);

        // then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();
        verify(refreshTokenRepository).findByUser(testUser);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("리프레시 토큰 생성 및 저장 테스트 - 기존 토큰 업데이트")
    void createAndSaveRefreshToken_UpdateExistingToken_Success() {
        // given
        given(refreshTokenRepository.findByUser(testUser)).willReturn(Optional.of(testRefreshToken));
        given(refreshTokenRepository.save(any(RefreshToken.class))).willReturn(testRefreshToken);

        // when
        String refreshToken = tokenProvider.createAndSaveRefreshToken(testUser);

        // then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();
        verify(refreshTokenRepository).findByUser(testUser);
        verify(refreshTokenRepository).save(testRefreshToken);
    }

    @Test
    @DisplayName("토큰에서 사용자명 추출 테스트")
    void getUsernameFromToken_Success() {
        // given
        String token = tokenProvider.generateAccessToken(authentication);

        // when
        String username = tokenProvider.getUsernameFromToken(token);

        // then
        assertThat(username).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("토큰에서 만료일 추출 테스트")
    void getExpirationDateFromToken_Success() {
        // given
        String token = tokenProvider.generateAccessToken(authentication);

        // when
        Date expirationDate = tokenProvider.getExpirationDateFromToken(token);

        // then
        assertThat(expirationDate).isNotNull();
        assertThat(expirationDate).isAfter(new Date()); // 현재 시간보다 미래여야 함
    }

    @Test
    @DisplayName("유효한 토큰 검증 테스트")
    void validateToken_ValidToken_ReturnsTrue() {
        // given
        String token = tokenProvider.generateAccessToken(authentication);

        // when
        boolean isValid = tokenProvider.validateToken(token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("잘못된 토큰 검증 테스트")
    void validateToken_InvalidToken_ReturnsFalse() {
        // given
        String invalidToken = "invalid.jwt.token";

        // when
        boolean isValid = tokenProvider.validateToken(invalidToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("빈 토큰 검증 테스트")
    void validateToken_EmptyToken_ReturnsFalse() {
        // when
        boolean isValid = tokenProvider.validateToken("");

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("null 토큰 검증 테스트")
    void validateToken_NullToken_ReturnsFalse() {
        // when
        boolean isValid = tokenProvider.validateToken(null);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰 검증 테스트")
    void validateToken_ExpiredToken_ReturnsFalse() throws InterruptedException {
        // given - 매우 짧은 만료 시간으로 설정
        ReflectionTestUtils.setField(tokenProvider, "expiration", 1L); // 1초
        String token = tokenProvider.generateAccessToken(authentication);
        
        // 토큰이 만료될 때까지 대기
        Thread.sleep(1100); // 1.1초 대기

        // when
        boolean isValid = tokenProvider.validateToken(token);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("토큰 클레임 추출 테스트")
    void getClaimFromToken_Success() {
        // given
        String token = tokenProvider.generateAccessToken(authentication);

        // when
        String subject = tokenProvider.getClaimFromToken(token, claims -> claims.getSubject());
        Date issuedAt = tokenProvider.getClaimFromToken(token, claims -> claims.getIssuedAt());

        // then
        assertThat(subject).isEqualTo("test@example.com");
        assertThat(issuedAt).isNotNull();
        assertThat(issuedAt).isBeforeOrEqualTo(new Date()); // 발급 시간은 현재 시간 이전이어야 함
    }

    @Test
    @DisplayName("다른 사용자의 토큰 생성 테스트")
    void generateTokenForDifferentUsers_Success() {
        // given
        Authentication auth1 = new UsernamePasswordAuthenticationToken("user1@example.com", "password", Collections.emptyList());
        Authentication auth2 = new UsernamePasswordAuthenticationToken("user2@example.com", "password", Collections.emptyList());

        // when
        String token1 = tokenProvider.generateAccessToken(auth1);
        String token2 = tokenProvider.generateAccessToken(auth2);

        // then
        assertThat(token1).isNotEqualTo(token2);
        assertThat(tokenProvider.getUsernameFromToken(token1)).isEqualTo("user1@example.com");
        assertThat(tokenProvider.getUsernameFromToken(token2)).isEqualTo("user2@example.com");
    }

    @Test
    @DisplayName("토큰 재생성 시 다른 값 생성 테스트")
    void generateToken_MultipleTimes_GeneratesDifferentTokens() throws InterruptedException {
        // when
        String token1 = tokenProvider.generateAccessToken(authentication);
        Thread.sleep(1000); // 1초 대기로 확실한 시간 차이 생성
        String token2 = tokenProvider.generateAccessToken(authentication);

        // then
        assertThat(token1).isNotEqualTo(token2); // 발급 시간이 다르므로 토큰도 달라야 함
        assertThat(tokenProvider.getUsernameFromToken(token1)).isEqualTo(tokenProvider.getUsernameFromToken(token2));
    }

    @Test
    @DisplayName("리프레시 토큰과 액세스 토큰 만료 시간 차이 테스트")
    void refreshTokenAndAccessToken_DifferentExpirationTimes() {
        // given
        given(refreshTokenRepository.findByUser(testUser)).willReturn(Optional.empty());
        given(refreshTokenRepository.save(any(RefreshToken.class))).willReturn(testRefreshToken);

        // when
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.createAndSaveRefreshToken(testUser);

        Date accessTokenExpiration = tokenProvider.getExpirationDateFromToken(accessToken);
        Date refreshTokenExpiration = tokenProvider.getExpirationDateFromToken(refreshToken);

        // then
        assertThat(refreshTokenExpiration).isAfter(accessTokenExpiration); // 리프레시 토큰이 더 오래 유효해야 함
    }
}
