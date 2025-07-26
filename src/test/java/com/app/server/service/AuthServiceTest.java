package com.app.server.service;

import com.app.server.controller.AuthController.*;
import com.app.server.domain.RefreshToken;
import com.app.server.domain.User;
import com.app.server.domain.UserRole;
import com.app.server.exception.DuplicateEmailException;
import com.app.server.exception.InvalidCredentialsException;
import com.app.server.exception.TokenExpiredException;
import com.app.server.mapper.UserMapper;
import com.app.server.repository.RefreshTokenRepository;
import com.app.server.repository.UserRepository;
import com.app.server.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 단위 테스트")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private RefreshToken testRefreshToken;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setNickname("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRole(UserRole.USER);
        testUser.setDeleted(false);

        testRefreshToken = new RefreshToken("test-refresh-token", 1L, LocalDateTime.now().plusDays(7));
        testRefreshToken.setId(1L);
    }

    @Test
    @DisplayName("로그인 성공")
    void signIn_Success() {
        // Given
        SignInRequest request = new SignInRequest("test@example.com", "password");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(any())).thenReturn("access-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testRefreshToken);
        when(userMapper.toResponse(testUser)).thenReturn(new User.Response(
            1L, "test@example.com", "testuser", null, UserRole.USER, 
            LocalDateTime.now(), LocalDateTime.now(), "system", "system", false
        ));

        // When
        SignInResponse response = authService.signIn(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo("로그인이 완료되었습니다.");
        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isNotNull();
        assertThat(response.user()).isNotNull();
        
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password", "encodedPassword");
        verify(jwtUtil).generateToken(any());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 실패")
    void signIn_UserNotFound_ThrowsException() {
        // Given
        SignInRequest request = new SignInRequest("nonexistent@example.com", "password");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.signIn(request))
                .isInstanceOf(InvalidCredentialsException.class);
        
        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 실패")
    void signIn_WrongPassword_ThrowsException() {
        // Given
        SignInRequest request = new SignInRequest("test@example.com", "wrongpassword");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.signIn(request))
                .isInstanceOf(InvalidCredentialsException.class);
        
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("wrongpassword", "encodedPassword");
    }

    @Test
    @DisplayName("삭제된 사용자 로그인 실패")
    void signIn_DeletedUser_ThrowsException() {
        // Given
        testUser.setDeleted(true);
        SignInRequest request = new SignInRequest("test@example.com", "password");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> authService.signIn(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("삭제된 계정입니다");
        
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp_Success() {
        // Given
        SignUpRequest request = new SignUpRequest("new@example.com", "password", "newuser");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.existsByNickname("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = authService.signUp(request);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).existsByEmail("new@example.com");
        verify(userRepository).existsByNickname("newuser");
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("중복 이메일로 회원가입 실패")
    void signUp_DuplicateEmail_ThrowsException() {
        // Given
        SignUpRequest request = new SignUpRequest("duplicate@example.com", "password", "newuser");
        when(userRepository.existsByEmail("duplicate@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.signUp(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("duplicate@example.com");
        
        verify(userRepository).existsByEmail("duplicate@example.com");
        verify(userRepository, never()).existsByNickname(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("중복 닉네임으로 회원가입 실패")
    void signUp_DuplicateNickname_ThrowsException() {
        // Given
        SignUpRequest request = new SignUpRequest("new@example.com", "password", "duplicatenick");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.existsByNickname("duplicatenick")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.signUp(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("duplicatenick");
        
        verify(userRepository).existsByEmail("new@example.com");
        verify(userRepository).existsByNickname("duplicatenick");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("로그아웃 성공")
    void signOut_Success() {
        // Given
        String accessToken = "valid-access-token";
        when(jwtUtil.getUsernameFromToken(accessToken)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        authService.signOut(accessToken);

        // Then
        verify(jwtUtil).getUsernameFromToken(accessToken);
        verify(userRepository).findByEmail("test@example.com");
        verify(refreshTokenRepository).revokeAllTokensByUserId(1L);
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 로그아웃 - 예외 발생하지 않음")
    void signOut_InvalidToken_NoException() {
        // Given
        String invalidToken = "invalid-token";
        when(jwtUtil.getUsernameFromToken(invalidToken)).thenThrow(new RuntimeException("Invalid token"));

        // When & Then
        assertThatCode(() -> authService.signOut(invalidToken))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("리프레시 토큰으로 액세스 토큰 갱신 성공")
    void refreshToken_Success() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
        when(refreshTokenRepository.findByToken("valid-refresh-token")).thenReturn(Optional.of(testRefreshToken));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(any())).thenReturn("new-access-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testRefreshToken);

        // When
        RefreshTokenResponse response = authService.refreshToken(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo("토큰이 갱신되었습니다.");
        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isNotNull();
        
        verify(refreshTokenRepository).findByToken("valid-refresh-token");
        verify(userRepository).findById(1L);
        verify(jwtUtil).generateToken(any());
        verify(refreshTokenRepository, times(2)).save(any(RefreshToken.class)); // 기존 토큰 무효화 + 새 토큰 저장
    }

    @Test
    @DisplayName("존재하지 않는 리프레시 토큰으로 갱신 실패")
    void refreshToken_TokenNotFound_ThrowsException() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("nonexistent-token");
        when(refreshTokenRepository.findByToken("nonexistent-token")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("유효하지 않은 리프레시 토큰입니다");
        
        verify(refreshTokenRepository).findByToken("nonexistent-token");
    }

    @Test
    @DisplayName("만료된 리프레시 토큰으로 갱신 실패")
    void refreshToken_ExpiredToken_ThrowsException() {
        // Given
        RefreshToken expiredToken = new RefreshToken("expired-token", 1L, LocalDateTime.now().minusDays(1));
        RefreshTokenRequest request = new RefreshTokenRequest("expired-token");
        when(refreshTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(expiredToken));

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(TokenExpiredException.class);
        
        verify(refreshTokenRepository).findByToken("expired-token");
    }

    @Test
    @DisplayName("JWT 토큰 생성")
    void generateAccessToken_Success() {
        // Given
        when(jwtUtil.generateToken(any())).thenReturn("generated-token");

        // When
        String token = authService.generateAccessToken(testUser);

        // Then
        assertThat(token).isEqualTo("generated-token");
        verify(jwtUtil).generateToken(any());
    }

    @Test
    @DisplayName("리프레시 토큰 생성")
    void generateRefreshToken_Success() {
        // Given
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testRefreshToken);

        // When
        String token = authService.generateRefreshToken(testUser);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("토큰 유효성 검증 - 유효한 토큰")
    void validateToken_ValidToken_ReturnsTrue() {
        // Given
        when(jwtUtil.isTokenExpired("valid-token")).thenReturn(false);

        // When
        boolean isValid = authService.validateToken("valid-token");

        // Then
        assertThat(isValid).isTrue();
        verify(jwtUtil).isTokenExpired("valid-token");
    }

    @Test
    @DisplayName("토큰 유효성 검증 - 만료된 토큰")
    void validateToken_ExpiredToken_ReturnsFalse() {
        // Given
        when(jwtUtil.isTokenExpired("expired-token")).thenReturn(true);

        // When
        boolean isValid = authService.validateToken("expired-token");

        // Then
        assertThat(isValid).isFalse();
        verify(jwtUtil).isTokenExpired("expired-token");
    }

    @Test
    @DisplayName("토큰 유효성 검증 - 유효하지 않은 토큰")
    void validateToken_InvalidToken_ReturnsFalse() {
        // Given
        when(jwtUtil.isTokenExpired("invalid-token")).thenThrow(new RuntimeException("Invalid token"));

        // When
        boolean isValid = authService.validateToken("invalid-token");

        // Then
        assertThat(isValid).isFalse();
        verify(jwtUtil).isTokenExpired("invalid-token");
    }

    @Test
    @DisplayName("토큰에서 이메일 추출")
    void getEmailFromToken_Success() {
        // Given
        when(jwtUtil.getUsernameFromToken("valid-token")).thenReturn("test@example.com");

        // When
        String email = authService.getEmailFromToken("valid-token");

        // Then
        assertThat(email).isEqualTo("test@example.com");
        verify(jwtUtil).getUsernameFromToken("valid-token");
    }
}