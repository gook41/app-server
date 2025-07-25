---
filename: progress-kanban.md
description: 칸반 보드와 작업 목록을 통합 관리하는 문서입니다.
gemini_cli: 작업 상태 변경, 진행률 추적, 우선순위 관리, 스프린트 계획을 수행합니다.
---

# INDEX

[REPORT] 문단을 참고하여 작업 상태 변경, 진행률 추적, 우선순위 관리, 스프린트 계획을 수행합니다.

---


# [REPORT-${kanban}]

## 현재 스프린트 상태 (2025-07-25 18:38 업데이트)

### 📋 To Do (백로그)
**우선순위 High**
- [ ] 서비스 계층 구현 (Epic)
    - [ ] UserService 확장 (CRUD 완성)
    - [ ] InventoryService 구현
    - [ ] OrderService 구현 (입고/출고)
    - [ ] LogService 구현

**우선순위 Medium**
- [ ] 유효성 검증 구현 (Story)
- [ ] API 컨트롤러 확장 (Story)
    - [ ] 재고 관리 API
    - [ ] 주문 관리 API
    - [ ] 로그 조회 API

**우선순위 Low**
- [ ] API 문서화 (Swagger)
- [ ] Docker 지원 구현
- [ ] CI/CD 파이프라인 설정

### 🔄 In Progress (진행 중) - 0/3
현재 진행 중인 작업이 없습니다.

### 👀 Review/QC (리뷰 대기) - 0/2
현재 리뷰 대기 중인 작업이 없습니다.

### ✅ Done (완료)
- [x] **계층형 아키텍처 구현** (Epic) - 2025-07-24 완료
    - [x] 서비스 계층 인터페이스 및 구현체 생성
    - [x] 컨트롤러에서 비즈니스 로직 분리
    - [x] 적절한 의존성 주입 구현

- [x] **패키지 일관성 수정** (Task) - 2025-07-24 완료
    - [x] 테스트 패키지를 `com.example.server`에서 `com.app.server`로 업데이트
    - [x] 코드베이스 전체 패키지 명명 일관성 확보

- [x] **User 엔티티 강화** (Story) - 2025-07-24 완료
    - [x] 비밀번호 암호화 구현
    - [x] @PreUpdate를 사용한 updatedAt 필드 추가
    - [x] 사용자 역할/권한 추가
    - [x] 소프트 삭제 기능 추가
    - [x] 감사 필드(createdBy, updatedBy) 구현

- [x] **DTO 패턴 구현** (Epic) - 2025-07-24 22:40 완료
    - [x] Static Inner Class로 DTO 구현 (User.CreateRequest, User.Response)
    - [x] MapStruct 매퍼 구현 (UserMapper 인터페이스)
    - [x] 컨트롤러 매핑 로직 제거 (15줄 → 2줄)
    - [x] 유효성 검증 추가 (@Valid, @NotBlank, @Email, @Size)
    - [x] 기존 DTO 파일 삭제 (UserRequest.java, UserResponse.java)
    - [x] 빌드 및 테스트 성공

- [x] **글로벌 예외 처리 강화** (Story) - 2025-07-25 14:26 완료
    - [x] 400 Bad Request 핸들링 구현 (BadRequestException, MethodArgumentNotValid, ConstraintViolation, TypeMismatch)
    - [x] 500 Internal Server Error 핸들링 구현 (Generic Exception)
    - [x] 커스텀 예외 클래스 확장 (BusinessException, ResourceNotFoundException)
    - [x] 구조화된 에러 응답 (ErrorResponse with ValidationError)
    - [x] 적절한 로깅 레벨 적용 및 한국어 에러 메시지

- [x] **Spring Security 설정** (Epic) - 2025-07-25 14:36 완료
    - [x] JWT 토큰 기반 인증 구현 (JwtUtil, JwtRequestFilter)
    - [x] BCrypt 비밀번호 암호화 설정 (PasswordEncoder)
    - [x] 역할 기반 접근 제어 구현 (USER/ADMIN 권한)
    - [x] 커스텀 UserDetailsService 구현 (CustomUserDetailsService)
    - [x] JWT 인증 예외 처리 구현 (JwtAuthenticationEntryPoint)
    - [x] Spring Security 설정 완료 (SecurityConfig)

- [x] **도메인 모델 확장** (Epic) - 2025-07-25 16:01-16:04 완료
    - [x] Inventory 엔티티 구현 (재고 관리)
    - [x] InboundOrder 엔티티 구현 (입고 주문)
    - [x] OutboundOrder 엔티티 구현 (출고 주문)
    - [x] Log 엔티티 구현 (감사 로그)
    - [x] OrderStatus enum 구현 (주문 상태)

- [x] **Repository 계층 확장** (Story) - 2025-07-25 16:03-16:04 완료
    - [x] InventoryRepository 구현 (재고 조회/관리)
    - [x] InboundOrderRepository 구현 (입고 주문 관리)
    - [x] OutboundOrderRepository 구현 (출고 주문 관리)
    - [x] LogRepository 구현 (로그 조회/검색)

## 스프린트 메트릭스

- **완료된 작업**: 8개 (Epic 4개, Story 3개, Task 1개)
- **진행률**: 아키텍처 + DTO 패턴 + 글로벌 예외 처리 + Spring Security + 도메인 모델 + Repository 계층 100% 완료 (전체 95%)
- **다음 스프린트 목표**: 서비스 계층 구현 및 API 확장

## 다음 우선순위 작업

1. **서비스 계층 구현** (예상 소요: 2일)
    - UserService 확장 (CRUD 완성)
    - InventoryService 구현 (재고 관리 로직)
    - OrderService 구현 (입고/출고 처리)
    - LogService 구현 (감사 로그 관리)

2. **API 컨트롤러 확장** (예상 소요: 1일)
    - 재고 관리 API (조회/수정/삭제)
    - 주문 관리 API (입고/출고/상태변경)
    - 로그 조회 API (검색/필터링)

3. **API 문서화 및 테스트** (예상 소요: 1일)
    - Swagger/OpenAPI 3 설정
    - API 문서 자동 생성
    - 통합 테스트 작성

---

