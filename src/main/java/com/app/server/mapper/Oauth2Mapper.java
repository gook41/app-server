package com.app.server.mapper;

import com.app.server.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface Oauth2Mapper {


    public static record SignInRequest(
            @NotBlank(message = "이메일은 필수입니다")
            @Email(message = "올바른 이메일 형식이 아닙니다")
            String email,
            @NotBlank(message = "비밀번호는 필수입니다")
            String password
    ) {
    }

    public static record SignInResponse(
            String message,
            String accessToken,
            String refreshToken,
            User.Response user
    ) {
    }

    public static record SignUpRequest(
            @NotBlank(message = "이메일은 필수입니다")
            @Email(message = "올바른 이메일 형식이 아닙니다")
            String email,
            @NotBlank(message = "비밀번호는 필수입니다")
            @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
            String password,
            @NotBlank(message = "닉네임은 필수입니다")
            String nickname
    ) {
    }

    public static record SignUpResponse(
            String message,
            User.Response user
    ) {
    }

    public static record SignOutRequest(
            @NotBlank(message = "액세스 토큰은 필수입니다")
            String accessToken
    ) {
    }

    public static record SignOutResponse(
            String message
    ) {
    }

    public static record RefreshTokenRequest(
            @NotBlank(message = "리프레시 토큰은 필수입니다")
            String refreshToken
    ) {
    }

    public static record RefreshTokenResponse(
            String message,
            String accessToken,
            String refreshToken
    ) {
    }

}