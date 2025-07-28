package com.app.server.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "outbound_order")
public class OutboundOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Integer totalQuantity;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime processedAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(nullable = false)
    private String updatedBy;

    private boolean deleted = false; // Soft delete 필드

    // DTO Classes - 도메인 응집도를 높이기 위한 static inner classes
    public static record CreateRequest(
            @NotBlank(message = "주문 번호는 필수입니다")
            String orderNumber,
            @NotNull(message = "고객 ID는 필수입니다")
            Long customerId,
            @NotNull(message = "총 수량은 필수입니다")
            @Min(value = 1, message = "총 수량은 1 이상이어야 합니다")
            Integer totalQuantity,
            @NotNull(message = "사용자 ID는 필수입니다")
            Long userId
    ) {}

    public static record UpdateRequest(
            OrderStatus status,
            Integer totalQuantity,
            LocalDateTime processedAt
    ) {}

    public static record Response(
            Long id,
            String orderNumber,
            Long customerId,
            OrderStatus status,
            Integer totalQuantity,
            Long userId,
            String userNickname,
            LocalDateTime createdAt,
            LocalDateTime processedAt,
            LocalDateTime updatedAt,
            String createdBy,
            String updatedBy,
            Boolean deleted
    ) {}
}