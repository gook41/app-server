package com.app.server.service;

import com.app.server.controller.AuthController.SignInRequest;
import com.app.server.controller.AuthController.SignInResponse;
import com.app.server.controller.AuthController.SignUpRequest;
import com.app.server.controller.AuthController.RefreshTokenRequest;
import com.app.server.controller.AuthController.RefreshTokenResponse;
import com.app.server.domain.User;

public interface AuthService {
    
    /**
     * 사용자 로그인 처리
     * @param request 로그인 요청 (이메일, 비밀번호)
     * @return 로그인 응답 (JWT 토큰, 사용자 정보)
     */
    SignInResponse signIn(SignInRequest request);
    
    /**
     * 사용자 회원가입 처리
     * @param request 회원가입 요청 (이메일, 비밀번호, 닉네임)
     * @return 생성된 사용자 엔티티
     */
    User signUp(SignUpRequest request);
    
    /**
     * 사용자 로그아웃 처리 (토큰 무효화)
     * @param accessToken 무효화할 액세스 토큰
     */
    void signOut(String accessToken);
    
    /**
     * 리프레시 토큰으로 새로운 액세스 토큰 발급
     * @param request 리프레시 토큰 요청
     * @return 새로운 토큰 응답
     */
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
    
    /**
     * JWT 토큰 생성
     * @param user 사용자 엔티티
     * @return JWT 액세스 토큰
     */
    String generateAccessToken(User user);
    
    /**
     * 리프레시 토큰 생성 및 저장
     * @param user 사용자 엔티티
     * @return 리프레시 토큰
     */
    String generateRefreshToken(User user);
    
    /**
     * 토큰 유효성 검증
     * @param token JWT 토큰
     * @return 유효성 여부
     */
    boolean validateToken(String token);
    
    /**
     * 토큰에서 사용자 이메일 추출
     * @param token JWT 토큰
     * @return 사용자 이메일
     */
    String getEmailFromToken(String token);
}