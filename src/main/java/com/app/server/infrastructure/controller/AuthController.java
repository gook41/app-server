package com.app.server.infrastructure.controller;

import com.app.server.application.mapper.UserMapper;
import com.app.server.domain.User;
import com.app.server.domain.UserRole;
import com.app.server.domain.service.AuthService;
import com.app.server.domain.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> signIn(@Valid @RequestBody SignInRequest request) {
        SignInResponse response = authService.signIn(request.email(),request.password());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        User newUser = userService.signUp(request.email(), request.password(), request.nickname(), UserRole.USER); // role은 여기서 기본값으로 전달
        UserResponse responseDto = userMapper.toResponse(newUser);
        SignUpResponse response = new SignUpResponse("회원가입이 완료되었습니다.", responseDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signout")
    public ResponseEntity<SignOutResponse> signOut(@Valid @RequestBody SignOutRequest request) {

        authService.signOut(request.accessToken());
        SignOutResponse response = new SignOutResponse("로그아웃이 완료되었습니다.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = authService.refreshToken(request.refreshToken());
        return ResponseEntity.ok(response);
    }

    // ==================== DTO Classes ====================
    // ==== User DTOs ====
    public record UserUpdateRequest(
            @Email String email,
            String nickname,
            String name,
            UserRole role
    ) {}
    public record UserResponse(
            Long id,
            String email,
            String nickname,
            String name,
            UserRole role,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String createdBy,
            String updatedBy,
            Boolean deleted
    ) {}
    // ==== Sign-In ====
    public static record SignInRequest(
            @NotBlank(message = "이메일은 필수입니다")
            @Email(message = "올바른 이메일 형식이 아닙니다")
            String email,
            @NotBlank(message = "비밀번호는 필수입니다")
            String password
    ) {}
    public static record SignInResponse(
            String message,
            String accessToken,
            String refreshToken,
            UserResponse user
    ) {}

    // ==== Sign-Up ====
    public static record SignUpRequest(
            @NotBlank(message = "이메일은 필수입니다")
            @Email(message = "올바른 이메일 형식이 아닙니다")
            String email,
            @NotBlank(message = "비밀번호는 필수입니다")
            @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
            String password,
            @NotBlank(message = "닉네임은 필수입니다")
            String nickname
    ) {}
    public static record SignUpResponse(
            String message,
            UserResponse user
    ) {}
    // ==== Sign-Out ====
    public static record SignOutRequest(
            @NotBlank(message = "액세스 토큰은 필수입니다")
            String accessToken
    ) {}
    public static record SignOutResponse(
            String message
    ) {}
    // ==== Token ====
    public static record RefreshTokenRequest(
            @NotBlank(message = "리프레시 토큰은 필수입니다")
            String refreshToken
    ) {}
    public static record RefreshTokenResponse(
            String message,
            String accessToken,
            String refreshToken
    ) {}
}