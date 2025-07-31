package com.app.server.service;

import com.app.server.application.mapper.UserMapper;
import com.app.server.application.service.AuthServiceImpl;
import com.app.server.domain.User;
import com.app.server.domain.UserRepository;
import com.app.server.domain.UserRole;
import com.app.server.domain.service.TokenService;
import com.app.server.infrastructure.controller.AuthController.SignInRequest;
import com.app.server.infrastructure.controller.AuthController.SignInResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 단위 테스트")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("testuser")
                .password("encodedPassword")
                .role(UserRole.USER)
                .build();
    }

    @Test
    @DisplayName("로그인 성공")
    void signIn_Success() {
        // Given
        SignInRequest request = new SignInRequest("test@example.com", "password");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(tokenService.generateAccessToken(any())).thenReturn("access-token");
        when(tokenService.createAndSaveRefreshToken(any())).thenReturn("refresh-token");

        // When
        SignInResponse response = authService.signIn(request.email(),request.password());

        // Then
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
    }

    // ... other tests need to be updated for the new architecture
}