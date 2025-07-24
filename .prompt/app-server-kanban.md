---
filename: app-server-kanban.md
description: 칸반 보드와 작업 목록을 통합 관리하는 문서입니다.
gemini_cli: 작업 상태 변경, 진행률 추적, 우선순위 관리, 스프린트 계획을 수행합니다.
---

# INDEX

[REPORT] 문단을 참고하여 작업 상태 변경, 진행률 추적, 우선순위 관리, 스프린트 계획을 수행합니다.

---


# [REPORT-${kanban}]

## 현재 스프린트 상태 (2025-07-24)

### 📋 To Do (백로그)
**우선순위 High**
- [ ] 글로벌 예외 처리 강화 (Story) - 부분 완료
- [ ] Spring Security 설정 (Epic)

**우선순위 Medium**
- [ ] 도메인 모델 구현 (Epic)
    - [ ] Inventory 엔티티 생성
    - [ ] QR/Bidding 엔티티 생성
    - [ ] Log/Audit 엔티티 생성
- [ ] Repository 확장 (Story)
- [ ] 유효성 검증 구현 (Story)

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
    - [x] Static Inner Class로 DTO 구현 (User.CreateRequest, Us**e**r.Response)
    - [x] MapStruct 매퍼 구현 (UserMapper 인터페이스)
    - [x] 컨트롤러 매핑 로직 제거 (15줄 → 2줄)
    - [x] 유효성 검증 추가 (@Valid, @NotBlank, @Email, @Size)
    - [x] 기존 DTO 파일 삭제 (UserRequest.java, UserResponse.java)
    - [x] 빌드 및 테스트 성공

## 스프린트 메트릭스

- **완료된 작업**: 4개 (Epic 2개, Story 1개, Task 1개)
- **진행률**: 아키텍처 기반 작업 + DTO 패턴 100% 완료 (전체 75%)
- **다음 스프린트 목표**: 글로벌 예외 처리 강화 및 Spring Security 설정

## 다음 우선순위 작업

1. **글로벌 예외 처리 강화** (예상 소요: 1일)
    - 400 Bad Request 핸들링 추가
    - 500 Internal Server Error 핸들링 추가
    - 커스텀 예외 클래스 확장

2. **Spring Security 설정** (예상 소요: 2일)
    - 비밀번호 암호화 (BCrypt)
    - JWT 토큰 기반 인증
    - OAuth2 통합
    - 역할 기반 접근 제어

3. **도메인 모델 확장** (예상 소요: 2일)
    - Inventory 엔티티 구현
    - Order 엔티티 구현
    - 엔티티 간 관계 설정

---