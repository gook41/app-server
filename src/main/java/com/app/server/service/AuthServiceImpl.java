package com.app.server.service;

import com.app.server.controller.AuthController.SignInRequest;
import com.app.server.controller.AuthController.SignInResponse;
import com.app.server.controller.AuthController.SignUpRequest;
import com.app.server.controller.AuthController.RefreshTokenRequest;
import com.app.server.controller.AuthController.RefreshTokenResponse;
import com.app.server.domain.RefreshToken;
import com.app.server.domain.User;
import com.app.server.domain.UserRole;
import com.app.server.exception.DuplicateEmailException;
import com.app.server.exception.InvalidCredentialsException;
import com.app.server.exception.ResourceNotFoundException;
import com.app.server.exception.TokenExpiredException;
import com.app.server.mapper.UserMapper;
import com.app.server.repository.RefreshTokenRepository;
import com.app.server.repository.UserRepository;
import com.app.server.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    // 리프레시 토큰 만료 시간 (7일)
    private static final long REFRESH_TOKEN_EXPIRY_DAYS = 7;

    public AuthServiceImpl(UserRepository userRepository, 
                          RefreshTokenRepository refreshTokenRepository,
                          PasswordEncoder passwordEncoder, 
                          JwtUtil jwtUtil,
                          UserMapper userMapper) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    @Override
    public SignInResponse signIn(SignInRequest request) {
        // 사용자 조회
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException());

        // 삭제된 사용자 체크
        if (user.isDeleted()) {
            throw new InvalidCredentialsException("삭제된 계정입니다.");
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        // JWT 토큰 생성
        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);

        // 사용자 응답 DTO 생성
        User.Response userResponse = userMapper.toResponse(user);

        return new SignInResponse(
                "로그인이 완료되었습니다.",
                accessToken,
                refreshToken,
                userResponse
        );
    }

    @Override
    public User signUp(SignUpRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }

        // 닉네임 중복 체크
        if (userRepository.existsByNickname(request.nickname())) {
            throw new DuplicateEmailException("이미 사용 중인 닉네임입니다: " + request.nickname());
        }

        // 새 사용자 생성
        User newUser = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .role(UserRole.USER) // 기본 역할은 USER
                .build();
        // 사용자 저장
        return userRepository.save(newUser);
    }

    @Override
    public void signOut(String accessToken) {
        try {
            // 토큰에서 사용자 이메일 추출
            String email = getEmailFromToken(accessToken);
            
            // 사용자 조회
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

            // 해당 사용자의 모든 리프레시 토큰 무효화
            refreshTokenRepository.revokeAllTokensByUserId(user.getId());
            
        } catch (Exception e) {
            // 토큰이 유효하지 않더라도 로그아웃은 성공으로 처리
            // (클라이언트에서 토큰을 삭제하면 되므로)
        }
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        // 리프레시 토큰 조회
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new InvalidCredentialsException("유효하지 않은 리프레시 토큰입니다."));

        // 토큰 유효성 검증
        if (!refreshToken.isValid()) {
            throw new TokenExpiredException("리프레시");
        }

        // 사용자 조회
        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // 새로운 액세스 토큰 생성
        String newAccessToken = generateAccessToken(user);
        
        // 새로운 리프레시 토큰 생성 (기존 토큰 무효화)
        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);
        String newRefreshToken = generateRefreshToken(user);

        return new RefreshTokenResponse(
                "토큰이 갱신되었습니다.",
                newAccessToken,
                newRefreshToken
        );
    }

//    @Override
//    public String generateAccessToken(User user) {
//        // JwtUtil을 사용하여 액세스 토큰 생성
//        return jwtUtil.generateToken(new org.springframework.security.core.userdetails.User(
//                user.getEmail(),
//                user.getPassword(),
//                java.util.Collections.singletonList(
//                        new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().name())
//                )
//        ));
//    }
//
//    @Override
//    public String generateRefreshToken(User user) {
//        // UUID를 사용하여 고유한 리프레시 토큰 생성
//        String tokenValue = UUID.randomUUID().toString();
//
//        // 만료 시간 설정 (7일 후)
//        LocalDateTime expiryDate = LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRY_DAYS);
//
//        // 리프레시 토큰 엔티티 생성 및 저장
//        RefreshToken refreshToken = new RefreshToken(tokenValue, user.getId(), expiryDate);
//        refreshTokenRepository.save(refreshToken);
//
//        return tokenValue;
//    }
//
//    @Override
//    public boolean validateToken(String token) {
//        try {
//            return !jwtUtil.isTokenExpired(token);
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    @Override
//    public String getEmailFromToken(String token) {
//        return jwtUtil.getUsernameFromToken(token);
//    }
}