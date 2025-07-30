package com.app.server.domain.service;


import com.app.server.infrastructure.controller.AuthController.RefreshTokenResponse;
import com.app.server.infrastructure.controller.AuthController.SignInResponse;


public interface AuthService {

    /**
     * 사용자 로그인 처리
     * @param password email
     * @return 로그인 응답 (JWT 토큰, 사용자 정보)
     */
    SignInResponse signIn(String email, String password);

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
    RefreshTokenResponse refreshToken(String request);
}