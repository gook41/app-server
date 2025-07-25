---
filename: progress-roadmap.md
description: gook41 프로젝트의 서버 개발 환경과 일정 계획을 정의하는 문서입니다.
gemini_cli: 프로젝트 기술 스택 참조, 아키텍처 결정 시 가이드라인 제공, 개발 환경 설정 지원
---

# INDEX

[progress-roadmap.md]는 gook41 WMS(Warehouse Management System) 프로젝트의 전체적인 로드맵과 기술 스택을 정의하는 문서입니다.
프로젝트의 목표, 개발 환경, 기술적 요구사항, 엔티티 설계 등 프로젝트의 큰 그림을 제공합니다.

## 프로젝트 개요

### 목표
- **gook41 WMS 시스템**: Electron 기반 창고 관리 시스템
- **실시간 입/출고 처리**: QR 코드 기반 재고 관리
- **JWT/OAuth2 인증**: 보안 강화된 사용자 인증 시스템
- **RESTful API**: 프론트엔드와의 효율적인 데이터 통신

### 주요 기능
- 사용자 인증 및 권한 관리
- 재고 관리 (입고/출고/조회)
- QR 코드 생성 및 스캔
- 실시간 로그 조회 및 감사
- API 문서화 (Swagger)

## 개발 환경

### 백엔드 기술 스택
- **Java**: 17 (LTS)
- **Spring Boot**: 3.4.7
- **Spring Framework**: 6.x
- **Spring Security**: JWT + OAuth2
- **Spring Data JPA**: 데이터 액세스 계층
- **Gradle**: 8.x (빌드 도구)

### 데이터베이스
- **개발환경**: H2 Database (인메모리)
- **운영환경**: PostgreSQL (예정)
- **JPA/Hibernate**: ORM

### 개발 도구
- **IDE**: IntelliJ IDEA
- **버전 관리**: Git + GitHub
- **API 문서**: Swagger/OpenAPI 3
- **테스트**: JUnit 5, Mockito
- **로깅**: SLF4J + Logback

### 실행 명령어
```bash
# 개발 서버 실행
./gradlew bootRun

# 테스트 실행
./gradlew test

# 빌드
./gradlew build

# JAR 실행
java -jar build/libs/app-server-0.0.1-SNAPSHOT.jar
```

### 코드 스타일
- **Google Java Style Guide** 준수
- **패키지 구조**: 도메인 중심 설계
- **네이밍**: camelCase (Java), snake_case (DB)

## 패키지 구조

```
com.app.server
├── controller/     # REST API 컨트롤러
├── service/        # 비즈니스 로직
├── repository/     # 데이터 액세스
├── domain/         # 엔티티 및 도메인 모델
├── dto/           # 데이터 전송 객체
├── config/        # 설정 클래스
├── security/      # 보안 관련
├── exception/     # 예외 처리
└── util/          # 유틸리티
```

## 백엔드 엔티티 설계

### User (사용자)
- **id**: Long (PK)
- **email**: String (UK)
- **password**: String (암호화)
- **username**: String
- **nickname**: String
- **role**: UserRole (ADMIN, USER)
- **createdAt**: LocalDateTime
- **updatedAt**: LocalDateTime
- **createdBy**: String
- **updatedBy**: String
- **deleted**: Boolean (소프트 삭제)

### Inventory (재고)
- **id**: Long (PK)
- **itemName**: String
- **itemCode**: String (UK)
- **quantity**: Integer
- **location**: String
- **qrCode**: String
- **createdAt**: LocalDateTime
- **updatedAt**: LocalDateTime

### InboundOrder (입고 주문)
- **id**: Long (PK)
- **orderNumber**: String (UK)
- **supplierId**: Long
- **status**: OrderStatus
- **totalQuantity**: Integer
- **createdAt**: LocalDateTime
- **processedAt**: LocalDateTime
- **userId**: Long (FK)

### OutboundOrder (출고 주문)
- **id**: Long (PK)
- **orderNumber**: String (UK)
- **customerId**: Long
- **status**: OrderStatus
- **totalQuantity**: Integer
- **createdAt**: LocalDateTime
- **processedAt**: LocalDateTime
- **userId**: Long (FK)

### Log (로그)
- **id**: Long (PK)
- **action**: String
- **entityType**: String
- **entityId**: Long
- **userId**: Long (FK)
- **timestamp**: LocalDateTime
- **details**: String (JSON)

## CI/CD 및 배포

### Docker 지원
- **Dockerfile**: 멀티 스테이지 빌드
- **docker-compose.yml**: 개발 환경 구성
- **환경 변수**: .env 파일 관리

### 모니터링
- **Spring Boot Actuator**: 헬스 체크
- **로그 수집**: 구조화된 로깅
- **메트릭스**: 성능 모니터링

---