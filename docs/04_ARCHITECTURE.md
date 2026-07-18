# StorySeed Architecture

## 1. 문서 목적

이 문서는 StorySeed의 전체 시스템 구조와 각 모듈의 역할을 정의한다.

StorySeed는 사용자의 선택을 기반으로 AI가 이야기를 생성하는 인터랙티브 스토리 플랫폼이다.

본 문서는 다음 내용을 설명한다.

- 전체 시스템 구조
- 기술 스택
- 프로젝트 패키지 구조
- 계층 구조(Layered Architecture)
- AI 처리 방식
- 데이터 흐름
- 확장 가능한 설계 원칙

---

# 2. 시스템 아키텍처

```
┌─────────────────────────────┐
│          Browser            │
│ (Thymeleaf / HTML / JS)     │
└──────────────┬──────────────┘
               │ HTTP
               ▼
┌─────────────────────────────┐
│      Spring Boot Server     │
├─────────────────────────────┤
│ Authentication Module       │
│ User Module                 │
│ Story Module                │
│ Chapter Module              │
│ Character Module            │
│ Choice Module               │
│ AI Module                   │
│ Report Module               │
└──────────────┬──────────────┘
               │ JPA
               ▼
┌─────────────────────────────┐
│          MySQL              │
└──────────────┬──────────────┘
               │ REST API
               ▼
┌─────────────────────────────┐
│         OpenAI API          │
└─────────────────────────────┘
```

---

# 3. 기술 스택

## Backend

| 기술 | 설명 |
|------|------|
| Java 25 | 메인 개발 언어 |
| Spring Boot 3.x | 백엔드 프레임워크 |
| Spring Security | 인증 및 권한 관리 |
| Spring Data JPA | ORM |
| Hibernate | JPA 구현체 |
| Gradle | 빌드 도구 |

---

## Frontend

| 기술 | 설명 |
|------|------|
| Thymeleaf | 서버 사이드 렌더링 |
| HTML5 | 화면 구성 |
| CSS3 | 스타일 |
| JavaScript | 사용자 인터랙션 |

---

## Database

| 기술 | 설명 |
|------|------|
| MySQL 8 | 메인 데이터베이스 |

---

## AI

초기 버전

- OpenAI GPT

향후 추가 예정

- 이미지 생성 AI
- 음성 생성 AI
- 영상 생성 AI

---

## Version Control

- Git
- GitHub

---

# 4. 프로젝트 구조

```
com.storyseed

├── config
├── auth
├── user
├── story
├── chapter
├── choice
├── character
├── genre
├── world
├── report
├── ai
├── common
└── exception
```

---

# 5. 패키지 설명

## config

프로젝트 전체 설정을 관리한다.

예시

- SecurityConfig
- OpenAIConfig
- WebConfig

---

## auth

회원가입

로그인

인증

권한 관리

---

## user

회원 정보 관리

프로필 수정

내 이야기 목록

---

## story

이야기 생성

조회

삭제

수정

진행 상태 관리

---

## chapter

챕터 생성

챕터 저장

챕터 조회

이어쓰기

---

## character

주인공 정보 관리

이름

성격

능력

목표

---

## genre

장르 목록 관리

---

## world

세계관 관리

---

## choice

사용자의 선택 기록

분기 관리

선택 통계

---

## ai

Prompt 생성

OpenAI 호출

응답 파싱

토큰 관리

---

## report

선택 리포트 생성

통계 생성

---

## common

공통 DTO

Enum

Response

Util

---

## exception

Global Exception Handler

Custom Exception

Error Code

---

# 6. 계층 구조

StorySeed는 Layered Architecture를 따른다.

```
Controller
      │
      ▼
Service
      │
      ▼
Repository
      │
      ▼
MySQL
```

각 계층의 책임은 명확하게 분리한다.

---

## Controller

HTTP 요청을 처리한다.

### 역할

- Request 수신
- DTO 검증
- Service 호출
- Response 반환

비즈니스 로직은 작성하지 않는다.

---

## Service

비즈니스 로직을 담당한다.

예시

- 이야기 생성
- Prompt 생성
- AI 호출
- 챕터 저장
- 리포트 생성

---

## Repository

Database 접근 전용 계층이다.

Spring Data JPA를 사용한다.

---

## Entity

Database 테이블과 1:1 매핑된다.

---

# 7. AI 처리 흐름

```
사용자 선택
      │
      ▼
Choice 저장
      │
      ▼
PromptBuilder
      │
      ▼
OpenAI API 호출
      │
      ▼
AI 응답 수신
      │
      ▼
ResponseParser
      │
      ▼
Chapter 생성
      │
      ▼
Story 진행률 업데이트
      │
      ▼
사용자에게 출력
```

---

# 8. 이야기 생성 흐름

```
사용자

↓

Story 생성

↓

Character 생성

↓

첫 Prompt 생성

↓

OpenAI 호출

↓

Chapter 생성

↓

Choice 생성

↓

사용자 선택

↓

Prompt 재생성

↓

OpenAI 호출

↓

Chapter 생성

↓

(반복)

↓

결말 생성

↓

선택 리포트 생성

↓

완료
```

---

# 9. Prompt 구성 요소

PromptBuilder는 다음 정보를 조합하여 AI에게 전달한다.

| 항목 | 설명 |
|------|------|
| 장르 | 판타지, SF 등 |
| 세계관 | 우주, 학교 등 |
| 분위기 | 밝음, 긴장감 등 |
| 대상 연령 | 어린이, 청소년, 성인 |
| 이야기 길이 | 짧음, 보통, 김 |
| 주인공 정보 | 이름, 성격, 목표 |
| 이전 챕터 | 지금까지 이야기 |
| 사용자 선택 | 최근 선택 |
| 등장인물 | 주요 인물 |
| 현재 챕터 | 진행 단계 |
| 이야기 목표 | 최종 목표 |

---

# 10. AI 모듈 구조

```
AIService
     │
     ▼
PromptBuilder
     │
     ▼
OpenAIClient
     │
     ▼
ResponseParser
     │
     ▼
ChapterService
```

각 클래스는 하나의 책임만 가진다.

---

# 11. 예외 처리

## AI Timeout

- 재시도
- 사용자에게 안내

---

## OpenAI 오류

- Prompt 재생성
- 재요청

---

## 네트워크 오류

- 마지막 저장 지점 복구

---

## Database 오류

- Transaction Rollback

---

# 12. 보안

Spring Security를 사용한다.

비밀번호는 BCrypt로 암호화한다.

권한은 다음과 같이 구분한다.

| 권한 | 설명 |
|------|------|
| USER | 일반 사용자 |
| ADMIN | 관리자 |

관리자 기능은 ADMIN만 접근 가능하다.

---

# 13. 확장 가능한 구조

StorySeed는 AI 서비스를 쉽게 교체하거나 추가할 수 있도록 설계한다.

```
AIService
    │
    ├── TextGenerator
    ├── ImageGenerator
    ├── VoiceGenerator
    └── VideoGenerator
```

향후 OpenAI 외 다른 AI 모델도 연결할 수 있도록 인터페이스 기반으로 구현한다.

---

# 14. 설계 원칙

## Single Responsibility Principle

하나의 클래스는 하나의 책임만 가진다.

---

## Dependency Injection

모든 의존성은 Spring DI를 사용한다.

---

## Layer Separation

Controller → Service → Repository 순서만 허용한다.

---

## Exception Handling

모든 예외는 GlobalExceptionHandler에서 처리한다.

---

## AI Isolation

AI 관련 로직은 `ai` 패키지에서만 관리한다.

---

# 15. 향후 확장 기능

- 기존 동화 리메이크
- 이미지 생성
- 음성 생성
- 영상 생성
- PDF 저장
- 공동 창작
- 모바일 앱
- 구독 시스템
- 결제 시스템

현재 구조는 위 기능을 추가하더라도 기존 구조를 크게 변경하지 않도록 설계한다.
