package com.app.server.controller;

import com.app.server.domain.User;
import com.app.server.domain.UserRole;
import com.app.server.repository.RefreshTokenRepository;
import com.app.server.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("AuthController 통합 테스트")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 기존 데이터 정리
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        // 테스트 사용자 생성
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setNickname("testuser");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setName("Test User");
        testUser.setRole(UserRole.USER);
        testUser.setDeleted(false);
        testUser.setCreatedBy("system");
        testUser.setUpdatedBy("system");
        
        userRepository.save(testUser);
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp_Success() throws Exception {
        // Given
        AuthController.SignUpRequest request = new AuthController.SignUpRequest(
                "new@example.com",
                "password123",
                "newuser"
        );

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."))
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.email").value("new@example.com"))
                .andExpect(jsonPath("$.user.nickname").value("newuser"))
                .andExpect(jsonPath("$.user.role").value("USER"));
    }

    @Test
    @DisplayName("중복 이메일로 회원가입 실패")
    void signUp_DuplicateEmail_Failure() throws Exception {
        // Given
        AuthController.SignUpRequest request = new AuthController.SignUpRequest(
                "test@example.com", // 이미 존재하는 이메일
                "password123",
                "newuser"
        );

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(containsString("이미 사용 중인 이메일입니다")));
    }

    @Test
    @DisplayName("유효하지 않은 이메일 형식으로 회원가입 실패")
    void signUp_InvalidEmail_Failure() throws Exception {
        // Given
        AuthController.SignUpRequest request = new AuthController.SignUpRequest(
                "invalid-email", // 잘못된 이메일 형식
                "password123",
                "newuser"
        );

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("입력값 검증에 실패했습니다"))
                .andExpect(jsonPath("$.validationErrors").exists())
                .andExpect(jsonPath("$.validationErrors[0].field").value("email"))
                .andExpect(jsonPath("$.validationErrors[0].message").value("올바른 이메일 형식이 아닙니다"));
    }

    @Test
    @DisplayName("짧은 비밀번호로 회원가입 실패")
    void signUp_ShortPassword_Failure() throws Exception {
        // Given
        AuthController.SignUpRequest request = new AuthController.SignUpRequest(
                "new@example.com",
                "123", // 8자 미만
                "newuser"
        );

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors[0].field").value("password"))
                .andExpect(jsonPath("$.validationErrors[0].message").value("비밀번호는 최소 8자 이상이어야 합니다"));
    }

    @Test
    @DisplayName("로그인 성공")
    void signIn_Success() throws Exception {
        // Given
        AuthController.SignInRequest request = new AuthController.SignInRequest(
                "test@example.com",
                "password123"
        );

        // When & Then
        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그인이 완료되었습니다."))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.user.nickname").value("testuser"));
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 실패")
    void signIn_NonexistentEmail_Failure() throws Exception {
        // Given
        AuthController.SignInRequest request = new AuthController.SignInRequest(
                "nonexistent@example.com",
                "password123"
        );

        // When & Then
        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("이메일 또는 비밀번호가 올바르지 않습니다."));
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 실패")
    void signIn_WrongPassword_Failure() throws Exception {
        // Given
        AuthController.SignInRequest request = new AuthController.SignInRequest(
                "test@example.com",
                "wrongpassword"
        );

        // When & Then
        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("이메일 또는 비밀번호가 올바르지 않습니다."));
    }

    @Test
    @DisplayName("삭제된 사용자 로그인 실패")
    void signIn_DeletedUser_Failure() throws Exception {
        // Given
        testUser.setDeleted(true);
        userRepository.save(testUser);

        AuthController.SignInRequest request = new AuthController.SignInRequest(
                "test@example.com",
                "password123"
        );

        // When & Then
        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("삭제된 계정입니다."));
    }

    @Test
    @DisplayName("로그인 후 로그아웃 성공")
    void signOut_Success() throws Exception {
        // Given - 먼저 로그인하여 토큰 획득
        AuthController.SignInRequest signInRequest = new AuthController.SignInRequest(
                "test@example.com",
                "password123"
        );

        String signInResponse = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // JSON에서 accessToken 추출
        String accessToken = objectMapper.readTree(signInResponse).get("accessToken").asText();

        AuthController.SignOutRequest signOutRequest = new AuthController.SignOutRequest(accessToken);

        // When & Then
        mockMvc.perform(post("/api/auth/signout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signOutRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그아웃이 완료되었습니다."));
    }

    @Test
    @DisplayName("토큰 갱신 성공")
    void refreshToken_Success() throws Exception {
        // Given - 먼저 로그인하여 리프레시 토큰 획득
        AuthController.SignInRequest signInRequest = new AuthController.SignInRequest(
                "test@example.com",
                "password123"
        );

        String signInResponse = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // JSON에서 refreshToken 추출
        String refreshToken = objectMapper.readTree(signInResponse).get("refreshToken").asText();

        AuthController.RefreshTokenRequest refreshRequest = new AuthController.RefreshTokenRequest(refreshToken);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("토큰이 갱신되었습니다."))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    @DisplayName("유효하지 않은 리프레시 토큰으로 갱신 실패")
    void refreshToken_InvalidToken_Failure() throws Exception {
        // Given
        AuthController.RefreshTokenRequest request = new AuthController.RefreshTokenRequest("invalid-refresh-token");

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("유효하지 않은 리프레시 토큰입니다."));
    }

    @Test
    @DisplayName("필수 필드 누락 시 검증 실패")
    void signUp_MissingFields_Failure() throws Exception {
        // Given - 이메일 누락
        String requestJson = """
                {
                    "password": "password123",
                    "nickname": "testuser"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("입력값 검증에 실패했습니다"))
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    @DisplayName("빈 문자열로 회원가입 실패")
    void signUp_EmptyFields_Failure() throws Exception {
        // Given
        AuthController.SignUpRequest request = new AuthController.SignUpRequest(
                "", // 빈 이메일
                "",  // 빈 비밀번호
                ""   // 빈 닉네임
        );

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors").isArray())
                .andExpect(jsonPath("$.validationErrors", hasSize(greaterThan(0))));
    }
}