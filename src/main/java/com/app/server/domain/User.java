package com.app.server.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames ={"provider","providerId"})
})
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String name;

    @Column(length = 20)
    private String provider;

    @Column(length = 255)
    private String providerId;

    private boolean deleted = false; // delete 필드
    // 비밀번호 암호화는 Spring Security에서 처리

    // DTO Classes - 도메인 응집도를 높이기 위한 static inner classes
    public static record CreateRequest(
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

    public static record UpdateRequest(
            @Email(message = "올바른 이메일 형식이 아닙니다")
            String email,
            String nickname,
            String name,
            UserRole role
    ) {
    }

    public static record Response(
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
    ) {
    }
}
