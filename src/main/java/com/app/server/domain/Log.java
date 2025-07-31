package com.app.server.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "log")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private Long entityId;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(columnDefinition = "TEXT")
    private String details; // JSON 형태의 상세 정보

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createdBy;

    // 생성자
    public Log(String action, String entityType, Long entityId, Long userId, String details) {
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.userId = userId;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    // DTO Classes - 도메인 응집도를 높이기 위한 static inner classes
    public static record CreateRequest(
            @NotBlank(message = "액션은 필수입니다")
            String action,
            @NotBlank(message = "엔티티 타입은 필수입니다")
            String entityType,
            @NotNull(message = "엔티티 ID는 필수입니다")
            Long entityId,
            @NotNull(message = "사용자 ID는 필수입니다")
            Long userId,
            String details
    ) {}

    public static record Response(
            Long id,
            String action,
            String entityType,
            Long entityId,
            Long userId,
            String userNickname,
            LocalDateTime timestamp,
            String details,
            String createdBy
    ) {}

    public static record SearchRequest(
            String action,
            String entityType,
            Long entityId,
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {}
}