package com.app.server.application.service;


import com.app.server.application.mapper.UserMapper;
import com.app.server.domain.RefreshToken;
import com.app.server.domain.RefreshTokenRepository;
import com.app.server.domain.User;
import com.app.server.domain.UserRepository;
import com.app.server.domain.exceptions.InvalidCredentialsException;
import com.app.server.domain.service.AuthService;
import com.app.server.domain.service.TokenService;
import com.app.server.infrastructure.controller.AuthController;
import com.app.server.infrastructure.exceptions.ResourceNotFoundException;
import com.app.server.infrastructure.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UserMapper userMapper;

    /**
     * 사용자 로그인 검증 처리
     * @param password email
     * @return 로그인 응답 (JWT 토큰, 사용자 정보)
     */
    @Override
    public AuthController.SignInResponse signIn(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
        if (user.isDeleted()) {
            throw new InvalidCredentialsException("삭제된 계정입니다.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        // 올바른 인증 객체 생성 방식
        // principal(누구), credentials(증거, 여긴 null), authorities(권한) 3개를 다 넣어줘야 "인증된" 토큰이 됨.
        CustomUserPrincipal principal = new CustomUserPrincipal(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenService.generateAccessToken(authentication);
        String refreshToken = tokenService.createAndSaveRefreshToken(user);

        AuthController.UserResponse userResponse = userMapper.toResponse(user);

        return new AuthController.SignInResponse("로그인이 완료되었습니다.", accessToken, refreshToken, userResponse);
    }
    @Override
    public void signOut(String accessToken) {
        // TODO: 액세스 토큰을 블랙리스트에 추가하는 로직 (e.g., Redis 사용)
        // 지금은 리프레시 토큰만 무효화하는 방식으로 대체 가능
    }

    /**
     * 리프레시 토큰으로 새로운 액세스 토큰 발급
     *
     * @param refreshTokenValue 리프레시 토큰 요청
     * @return 새로운 토큰 응답
     */
    @Override
    public AuthController.RefreshTokenResponse refreshToken(String refreshTokenValue) {
        // 1. 리프레시 토큰 유효성 검증 (만료, 서명 등)
        if (!tokenService.validateToken(refreshTokenValue)) {
            throw new InvalidCredentialsException("유효하지 않거나 만료된 리프레시 토큰입니다.");
        }

        // 2. DB에서 토큰 조회하고 무효화되지 않았는지 확인
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .filter(RefreshToken::isValid)
                .orElseThrow(() -> new InvalidCredentialsException("존재하지 않거나 무효화된 리프레시 토큰입니다."));

        // 3. 토큰에서 사용자 정보 조회
        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("토큰에 해당하는 사용자를 찾을 수 없습니다. ID: " + refreshToken.getUserId()));

        // 4. 새로운 액세스 토큰 생성
        CustomUserPrincipal principal = new CustomUserPrincipal(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        String newAccessToken = tokenService.generateAccessToken(authentication);

        // 5. (선택적) 리프레시 토큰 로테이션: 기존 토큰은 무효화하고 새 토큰 발급
        refreshToken.revoke();
        String newRefreshToken = tokenService.createAndSaveRefreshToken(user);

        return new AuthController.RefreshTokenResponse("토큰이 성공적으로 갱신되었습니다.", newAccessToken, newRefreshToken);
    }

}