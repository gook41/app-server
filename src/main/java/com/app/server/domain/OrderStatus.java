package com.app.server.domain;

public enum OrderStatus {
    PENDING,    // 대기 중
    PROCESSING, // 처리 중
    COMPLETED,  // 완료
    CANCELLED   // 취소
}