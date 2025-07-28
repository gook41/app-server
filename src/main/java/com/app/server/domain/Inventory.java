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
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false, unique = true)
    private String itemCode;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String qrCode;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

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
            @NotBlank(message = "상품명은 필수입니다")
            String itemName,
            @NotBlank(message = "상품 코드는 필수입니다")
            String itemCode,
            @NotNull(message = "수량은 필수입니다")
            @Min(value = 0, message = "수량은 0 이상이어야 합니다")
            Integer quantity,
            @NotBlank(message = "위치는 필수입니다")
            String location,
            @NotBlank(message = "QR 코드는 필수입니다")
            String qrCode
    ) {}

    public static record UpdateRequest(
            String itemName,
            @Min(value = 0, message = "수량은 0 이상이어야 합니다")
            Integer quantity,
            String location
    ) {}

    public static record Response(
            Long id,
            String itemName,
            String itemCode,
            Integer quantity,
            String location,
            String qrCode,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String createdBy,
            String updatedBy,
            Boolean deleted
    ) {}
}