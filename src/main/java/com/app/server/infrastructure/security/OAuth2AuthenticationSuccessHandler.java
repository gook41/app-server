package com.app.server.infrastructure.security;

import com.app.server.domain.User;
import com.app.server.domain.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2 로그인 성공 시 처리하는 핸들러
 * JWT 토큰을 발급하고 프론트엔드로 리다이렉트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    @Value("${app.oauth2.authorized-redirect-uris:http://localhost:3000/oauth2/redirect}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        String targetUrl = determineTargetUrl(request, response, authentication);
        
        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        try {
            // CustomUserPrincipal에서 User 정보 추출
            CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();
            User user = userPrincipal.getUser();

            // JWT 토큰 생성
            String accessToken = tokenService.generateAccessToken(authentication);
            String refreshToken = tokenService.createAndSaveRefreshToken(user);

            log.info("OAuth2 로그인 성공 - 사용자: {}, Provider: {}", user.getEmail(), user.getProvider());

            // 프론트엔드로 토큰과 함께 리다이렉트
            return UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("token", accessToken)
                    .queryParam("refreshToken", refreshToken)
                    .queryParam("email", URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8))
                    .queryParam("nickname", URLEncoder.encode(user.getNickname(), StandardCharsets.UTF_8))
                    .queryParam("provider", user.getProvider())
                    .build().toUriString();

        } catch (Exception e) {
            log.error("OAuth2 로그인 성공 처리 중 오류 발생", e);
            
            // 오류 발생 시 에러 페이지로 리다이렉트
            return UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("error", "authentication_processing_error")
                    .queryParam("message", URLEncoder.encode("로그인 처리 중 오류가 발생했습니다.", StandardCharsets.UTF_8))
                    .build().toUriString();
        }
    }

    /**
     * JSON 응답으로 토큰 반환 (API 방식)
     * 필요시 사용할 수 있는 대안 메서드
     */
    protected void sendJsonResponse(HttpServletResponse response, Authentication authentication) throws IOException {
        try {
            CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();
            User user = userPrincipal.getUser();

            String accessToken = tokenService.generateAccessToken(authentication);
            String refreshToken = tokenService.createAndSaveRefreshToken(user);

            Map<String, Object> tokenResponse = new HashMap<>();
            tokenResponse.put("success", true);
            tokenResponse.put("accessToken", accessToken);
            tokenResponse.put("refreshToken", refreshToken);
            tokenResponse.put("user", Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "nickname", user.getNickname(),
                    "provider", user.getProvider(),
                    "role", user.getRole().name()
            ));

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));

        } catch (Exception e) {
            log.error("OAuth2 JSON 응답 생성 중 오류 발생", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "authentication_processing_error");
            errorResponse.put("message", "로그인 처리 중 오류가 발생했습니다.");

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }
}
