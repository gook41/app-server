
WMS (Warehouse Management System)

- 구매/입낙찰/공급사 평가 모듈 보조 결합

---
> - app-client: Web frontend repository | (https://github.com/gook41/app-client)
> - app-desktop: Electron repository | (https://github.com/gook41/app-desktop)
> - app-server: Spring backend repository | (https://github.com/gook41/app-server)
> - app-meta: Project archive | (https://github.com/gook41/meta)
---
# RestAPI Server
- 개발 환경
  - `Java 21`, `Spring Boot 3.4.7`
  - IDE: `IntelliJ IDEA`
  - Source management: `Git, GitHub`
  - Build tools: `Gradle-jar`, `Docker`
  - Database: `h2, PostgreSQL`
  - CI/CD: `GitHub Actions, Docker`
  - profile: `dev`/`prod` | (http://localhost:8080)/
    - Server execution: `./gradlew bootRun`
    - Test execution: `./gradlew test`
    - Build: `./gradlew build`
  - Structure: `Hexagonal`
  - Key features: `JWT 12.3`, `OAuth2 api:{google,naver,kakao}`

## Client Requirements & Analyzed
- [보안 및 권한 관리]
  - "회사 내부 정보 및 계약 내용의 보호를 위해 로그인 시 보안이 강화되어야 합니다."
    - `OAuth2, JWT, 자체 SSO, MFA, HTTPS, Spring Security, Identity Management System`
  - "직원별 업무 역할에 따라 시스템 접근 및 기능 사용에 대한 권한을 차등 부여할 수 있어야 합니다."
    (예: 팀장급은 특정 계정에 '편집 권한'을 '특정 시간 동안, 특정 엔드포인트 까지' 부여하거나 생성 가능)
    - `RBAC (Role-Based Access Control), ABAC (Attribute-Based Access Control), Granular Permissions, Policy Engine, Admin UI for Role Management`
- [재고 관리 기능]
  - "물품의 입고, 출고 현황을 실시간으로 정확하게 파악하고 싶습니다."
    - `Real-time Database, WebSocket, Event-driven Architecture, Inventory Dashboard, Stock Level Alerts`
  - "바코드나 QR코드를 활용해 입출고가 이뤄지고 정확한 수량 처리가 이뤄지게 해주세요."
    - `Barcode/QR Code Scanner Integration (Mobile/Web Camera API), REST API, Data Validation, Transaction Management`
  - "해외 거래 특성상 여러 단위를 선택할 수 있게 해주세요. 예를 들면 컨테이너 단위의 입출고, 패킹 관리도 가능해야 합니다."
    - `Unit of Measure (UOM) Management, Hierarchical Inventory, Batch/Lot Tracking, Packing List Generation, Customizable Units`
- [구매 및 입찰 기능]
  - "현재 이메일 및 수동 처리되는 구매 요청, 발주, 입찰 공고, 제안서 접수, 비교, 낙찰 등 모든 과정을 시스템 내에서 한 번에 처리하고 싶습니다."
    - `Workflow Engine (e.g., Camunda), BPM (Business Process Management), E-Procurement Module, Digital Signature Integration, Document Management System (DMS)`
  - "입찰 등록, 낙찰 시 관련 업체 및 내부에 자동으로 알림이 발송되게 해주세요."
    - `Notification Service (Email, SMS, In-app push), SMTP Gateway, Message Queue, Event Bus`
- [공급사 평가 및 관리 기능]
  - 거래 중인 공급사의 정보를 체계적으로 기록하고 관리할 수 있어야 합니다.
    - `Vendor Master Data Management (MDM), Supplier Profile Management, CRM-like features`
  - 특정 기준(정량/정성)을 입력하면 자동으로 공급사 점수가 산출되어 평가 및 관리가 용이해야 합니다.
    - `Scoring Algorithm, Configurable Evaluation Criteria, Weighted Scoring, Reporting & Analytics, Data Visualization`
  - 이력 관리: 공급사별 거래 이력 및 평가 이력이 지속적으로 기록되어야 합니다. 이 부분은 비고란을 만들어주시면 수기로 작성해도 됩니다.
    - `Audit Trail, Historical Data Storage, Timestamping, Free-form Text Field, Version Control`
- [데이터 기록 및 추적]
  - 시스템 내에서 이루어지는 모든 활동(접속, 데이터 수정, 거래 내역 등)에 대한 로그 기록이 상세히 남아 누가, 언제, 무엇을 했는지 추후 확인 및 검색이 가능해야 합니다. (감사 대비)
    - `Audit Logging, Centralized Logging System (e.g., ELK Stack), Immutable Logs, User Activity Tracking, Search & Filter Functionality, Regulatory Compliance`
- [추가 (장기적인 관점 - 우선순위 낮음)]
  - QR코드 활용 외에, 향후 블록체인을 통한 투명한 관리나 AI를 활용한 재고량 예측 등의 신기술 접목 가능성도 검토 부탁드립니다.
    - `Blockchain for Supply Chain Traceability, Smart Contracts, DLT (Decentralized Ledger Technology), Machine Learning (ML) for Demand Forecasting, Predictive Analytics, Time Series Analysis, Big Data Platform`

## Features to implement
- [보안 및 권한 관리]
  - 인증: JWT 기반 로그인, OAuth2 연동, 자체 SSO 기능 통합 (Spring Security 활용).
  - 다단계 인증 (MFA): 로그인 시 MFA 기능 구현.
  - 권한 관리: RBAC 및 ABAC 모델을 적용한 세분화된 권한 부여 시스템 개발 (Spring Security).
  - 관리자 UI: 역할 및 권한을 관리할 수 있는 관리자용 UI 개발.
  - 보안 통신: HTTPS 적용을 위한 설정 및 가이드 제공.
- [재고 관리 기능 개발]
  - 실시간 입/출고: WebSocket 및 이벤트 기반 아키텍처를 활용한 실시간 재고 현황 업데이트 및 대시보드 구현.
  - 바코드/QR 코드 연동: 바코드/QR 코드 스캐너 (모바일/웹 카메라 API)를 통한 입출고 처리 로직 개발.
  - 다중 단위 관리 (UOM): 컨테이너, 패킹 등 다양한 단위로 물품을 관리할 수 있는 기능 구현 (Hierarchical Inventory, Batch/Lot Tracking 포함).
  - 데이터 유효성 검사 및 트랜잭션 관리: 입출고 시 데이터의 정확성을 위한 유효성 검사 및 트랜잭션 처리 로직 구현.
- [구매 및 입찰 기능 개발]
  - 워크플로우 엔진 통합: Camunda BPM과 같은 워크플로우 엔진을 활용하여 구매 요청, 발주, 입찰 공고, 제안서 접수, 비교, 낙찰 등 전체 프로세스 자동화.
  - 전자 조달 모듈: E-Procurement 모듈의 핵심 기능 (입찰 등록, 제안서 관리 등) 개발.
  - 알림 서비스: 입찰 등록 및 낙찰 시 관련 업체 및 내부에 자동으로 이메일, SMS, 인앱 푸시 알림 발송 기능 구현 (SMTP Gateway, Message Queue 활용).
  - 문서 관리 시스템 (DMS) 연동: 관련 문서 (제안서, 계약서 등)를 시스템 내에서 관리할 수 있도록 DMS 연동 방안 제시 및 구현.
- [공급사 평가 및 관리 기능 개발]
  - 공급사 마스터 데이터 관리 (MDM): 공급사 정보 (Vendor Master Data)를 체계적으로 기록하고 관리하는 기능 구현 (CRM-like features 포함).
  - 자동 평가 시스템: 정량/정성적 기준에 따라 공급사 점수를 자동으로 산출하는 알고리즘 및 설정 가능한 평가 기준 구현.
  - 이력 관리: 공급사별 거래 및 평가 이력을 지속적으로 기록하고 조회할 수 있는 감사 추적 (Audit Trail) 및 버전 관리 기능 구현 (비고란 포함).
- [데이터 기록 및 추적 시스템 구축]
  - 감사 로깅: 시스템 내 모든 활동 (접속, 데이터 수정, 거래 내역 등)에 대한 상세한 로그를 기록하는 기능 구현.
  - 중앙 집중식 로깅: ELK Stack과 같은 중앙 집중식 로깅 시스템을 구축하여 로그를 수집, 저장, 검색 및 필터링할 수 있도록 지원.
  - 불변 로그: 로그 데이터의 무결성을 보장하기 위한 방안 제시 및 구현.
- [추가 (장기적인 관점 - 우선순위 낮음)]
  - 블록체인 연동: 공급망 투명성 및 추적성 강화를 위한 블록체인 (DLT, Smart Contracts) 기술 접목 가능성 검토 및 POC (개념 증명) 코드 작성.
  - AI 기반 재고 예측: 머신러닝 (ML)을 활용한 재고량 예측 및 수요 예측 모델 개발을 위한 초기 탐색 및 데이터 분석 방안 제시.
