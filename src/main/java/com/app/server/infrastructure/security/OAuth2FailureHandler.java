package com.app.server.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2 로그인 실패 시 처리하는 핸들러
 * 에러 정보와 함께 프론트엔드로 리다이렉트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Value("${app.oauth2.authorized-redirect-uris:http://localhost:3000/oauth2/redirect}")
    private String redirectUri;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        
        String targetUrl = determineTargetUrl(request, response, exception);
        
        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) {
        
        String errorCode = "oauth2_authentication_failed";
        String errorMessage = "OAuth2 로그인에 실패했습니다.";

        // OAuth2 관련 예외 처리
        if (exception instanceof OAuth2AuthenticationException) {
            OAuth2AuthenticationException oauth2Exception = (OAuth2AuthenticationException) exception;
            String errorCodeFromException = oauth2Exception.getError().getErrorCode();
            
            switch (errorCodeFromException) {
                case "invalid_request":
                    errorCode = "invalid_oauth2_request";
                    errorMessage = "잘못된 OAuth2 요청입니다.";
                    break;
                case "unauthorized_client":
                    errorCode = "unauthorized_oauth2_client";
                    errorMessage = "인증되지 않은 OAuth2 클라이언트입니다.";
                    break;
                case "access_denied":
                    errorCode = "oauth2_access_denied";
                    errorMessage = "OAuth2 접근이 거부되었습니다.";
                    break;
                case "unsupported_response_type":
                    errorCode = "unsupported_oauth2_response_type";
                    errorMessage = "지원하지 않는 OAuth2 응답 타입입니다.";
                    break;
                case "invalid_scope":
                    errorCode = "invalid_oauth2_scope";
                    errorMessage = "잘못된 OAuth2 스코프입니다.";
                    break;
                case "server_error":
                    errorCode = "oauth2_server_error";
                    errorMessage = "OAuth2 서버 오류가 발생했습니다.";
                    break;
                case "temporarily_unavailable":
                    errorCode = "oauth2_temporarily_unavailable";
                    errorMessage = "OAuth2 서비스가 일시적으로 사용할 수 없습니다.";
                    break;
                default:
                    errorCode = "unknown_oauth2_error";
                    errorMessage = "알 수 없는 OAuth2 오류가 발생했습니다.";
                    break;
            }
        }

        // 특정 예외 메시지 처리
        String exceptionMessage = exception.getMessage();
        if (exceptionMessage != null) {
            if (exceptionMessage.contains("이런 provider 없음")) {
                errorCode = "unsupported_provider";
                errorMessage = "지원하지 않는 OAuth2 제공자입니다.";
            } else if (exceptionMessage.contains("email")) {
                errorCode = "email_not_provided";
                errorMessage = "이메일 정보를 가져올 수 없습니다. OAuth2 제공자에서 이메일 권한을 확인해주세요.";
            }
        }

        log.error("OAuth2 로그인 실패 - 에러코드: {}, 메시지: {}, 원본 예외: {}",
                errorCode, errorMessage, exception.getMessage());

        // 프론트엔드로 에러 정보와 함께 리다이렉트
        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("error", errorCode)
                .queryParam("message", URLEncoder.encode(errorMessage, StandardCharsets.UTF_8))
                .build().toUriString();
    }

    /**
     * JSON 응답으로 에러 반환 (API 방식)
     * 필요시 사용할 수 있는 대안 메서드
     */
    protected void sendJsonErrorResponse(HttpServletResponse response, AuthenticationException exception) throws IOException {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", "oauth2_authentication_failed");
        errorResponse.put("message", "OAuth2 로그인에 실패했습니다.");
        errorResponse.put("details", exception.getMessage());

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
