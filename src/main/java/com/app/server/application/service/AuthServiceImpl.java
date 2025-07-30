package com.app.server.application.service;

import com.app.server.application.mapper.UserMapper;
import com.app.server.domain.User;
import com.app.server.domain.UserRepository;
import com.app.server.domain.UserRole;
import com.app.server.service.AuthService;
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
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService; // JwtUtil -> TokenService
    private final UserMapper userMapper;

    @Override
    public SignInResponse signIn(SignInRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (user.isDeleted()) {
            throw new com.app.server.exception.InvalidCredentialsException("삭제된 계정입니다.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new com.app.server.exception.InvalidCredentialsException();
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenService.generateAccessToken(authentication);
        String refreshToken = tokenService.createAndSaveRefreshToken(user);

        User.Response userResponse = userMapper.toResponse(user);

        return new SignInResponse("로그인이 완료되었습니다.", accessToken, refreshToken, userResponse);
    }

    @Override
    public User signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new com.app.server.exception.DuplicateEmailException(request.email());
        }

        User newUser = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .role(UserRole.USER)
                .build();
        return userRepository.save(newUser);
    }

    @Override
    public void signOut(String accessToken) {
        // Implement sign-out logic if needed, e.g., blacklisting token
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        // This logic needs to be re-evaluated based on the new TokenProvider
        // For now, returning a placeholder
        return null;
    }
}