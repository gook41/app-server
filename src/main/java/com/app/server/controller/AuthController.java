package com.app.server.controller;

import com.app.server.domain.User;
import com.app.server.mapper.UserMapper;
import com.app.server.service.AuthService;
import com.app.server.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final UserMapper userMapper;

    public AuthController(AuthService authService, UserService userService, UserMapper userMapper) {
        this.authService = authService;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> signIn(@Valid @RequestBody SignInRequest request) {
        // 인증 처리 및 JWT 토큰 생성
        SignInResponse response = authService.signIn(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        // 회원가입 처리
        User newUser = authService.signUp(request);
        User.Response userResponse = userMapper.toResponse(newUser);
        
        SignUpResponse response = new SignUpResponse(
            "회원가입이 완료되었습니다.",
            userResponse
        );
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signout")
    public ResponseEntity<SignOutResponse> signOut(@Valid @RequestBody SignOutRequest request) {
        // 로그아웃 처리 (토큰 무효화)
        authService.signOut(request.accessToken());
        
        SignOutResponse response = new SignOutResponse("로그아웃이 완료되었습니다.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        // 리프레시 토큰으로 새로운 액세스 토큰 발급
        RefreshTokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    // ==================== DTO Classes ====================

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
            User.Response user
    ) {}

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
            User.Response user
    ) {}

    public static record SignOutRequest(
            @NotBlank(message = "액세스 토큰은 필수입니다")
            String accessToken
    ) {}

    public static record SignOutResponse(
            String message
    ) {}

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