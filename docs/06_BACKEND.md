# StorySeed Backend Design

> Version : 1.0
>
> Project : StorySeed
>
> Framework : Spring Boot 3
>
> Language : Java 25
>
> ORM : Spring Data JPA
>
> Last Update : 2026

---

# 1. Document Overview

## 1.1 Purpose

본 문서는 StorySeed의 Backend 구조를 정의한다.

Backend는 단순히 REST API를 제공하는 역할을 넘어,

- 사용자 인증
- Story 생성
- AI 호출
- 데이터 관리
- 비즈니스 로직 처리

를 담당한다.

StorySeed는 계층형(Layered Architecture)을 기반으로 설계하며,

유지보수성과 확장성을 최우선으로 고려한다.

---

## 1.2 Scope

본 문서에서는 다음 내용을 정의한다.

- Package Structure
- Layered Architecture
- Controller
- Service
- Repository
- Entity
- DTO
- Mapper
- Exception
- Validation
- Security
- AI Service Layer

---

## 1.3 Backend Goals

StorySeed Backend의 목표는 다음과 같다.

- 유지보수 용이성
- 높은 응집도
- 낮은 결합도
- 확장 가능한 구조
- 테스트하기 쉬운 코드
- AI 기능과 일반 비즈니스 로직의 분리

---

# 2. Backend Architecture

StorySeed는 Layered Architecture를 사용한다.

```
Client

↓

Controller

↓

Service

↓

Repository

↓

Database
```

AI 생성 기능은 별도의 계층으로 분리한다.

```
Controller

↓

Service

↓

AI Service

↓

Prompt Builder

↓

LLM API

↓

Database
```

---

# 3. Package Structure

```
com.storyseed

├── common
├── config
├── auth
├── user
├── story
├── chapter
├── choice
├── report
├── ai
├── prompt
├── item
├── flag
├── exception
└── util
```

기능(Feature) 단위로 패키지를 구성한다.

---

# 4. Layer Responsibilities

## Controller

Controller는 HTTP 요청과 응답만 담당한다.

비즈니스 로직은 작성하지 않는다.

역할

- Request 수신
- Validation
- Service 호출
- Response 반환

---

## Service

Service는 모든 비즈니스 로직을 담당한다.

예시

- Story 생성
- Choice 처리
- AI 호출
- Story 종료
- Bookmark 등록

---

## Repository

Repository는 데이터 접근만 담당한다.

Spring Data JPA를 사용한다.

복잡한 조회는 JPQL 또는 QueryDSL을 활용할 수 있다.

---

## Entity

Entity는 데이터베이스 테이블과 1:1로 대응한다.

Entity 내부에는 최소한의 도메인 로직만 포함한다.

---

## DTO

DTO는 API 입출력 전용 객체이다.

Entity를 직접 외부에 노출하지 않는다.

---

# 5. Backend Design Principles

StorySeed는 다음 원칙을 따른다.

- Controller는 얇게 유지한다.
- Service에 핵심 비즈니스 로직을 집중한다.
- Entity를 API 응답으로 직접 반환하지 않는다.
- DTO를 통해 데이터 전달을 수행한다.
- 공통 기능은 common 패키지로 분리한다.

---

# 6. Next Section

다음 장에서는 StorySeed Controller 계층의 설계 원칙과 Controller별 역할을 정의한다.

# 7. Controller Design

## 7.1 Overview

Controller는 클라이언트의 HTTP 요청을 받아 Service 계층으로 전달하는 역할을 담당한다.

StorySeed에서는 Controller가 비즈니스 로직을 수행하지 않으며,

요청 검증(Request Validation), Service 호출, 응답(Response) 생성만 수행한다.

모든 핵심 로직은 Service 계층에서 처리한다.

---

## 7.2 Responsibilities

Controller는 다음 역할만 수행한다.

- HTTP Request 수신
- Request DTO 검증
- 인증 사용자 확인
- Service 호출
- Response DTO 반환
- HTTP Status 반환

Controller 내부에서는

- Repository 호출
- Entity 생성
- AI 호출
- Business Logic 처리

를 수행하지 않는다.

---

## 7.3 Controller Package Structure

```
controller

├── AuthController
├── UserController
├── StoryController
├── ChapterController
├── ChoiceController
├── BookmarkController
├── ReportController
├── PromptController
└── AdminController
```

기능(Feature) 단위로 Controller를 분리한다.

---

# 8. Controller Responsibilities

## 8.1 AuthController

AuthController는 인증(Authentication) 관련 API를 제공한다.

담당 기능

- 회원가입
- 로그인
- 로그아웃
- Access Token 재발급
- OAuth 로그인

예시 API

```
POST /api/auth/signup

POST /api/auth/login

POST /api/auth/refresh

POST /api/auth/logout
```

---

## 8.2 UserController

UserController는 회원 정보를 관리한다.

담당 기능

- 내 정보 조회
- 회원정보 수정
- 비밀번호 변경
- 회원 탈퇴
- UserSetting 조회

예시 API

```
GET /api/users/me

PUT /api/users/me

DELETE /api/users/me
```

---

## 8.3 StoryController

StoryController는 Story 전체를 관리한다.

담당 기능

- Story 생성
- Story 조회
- Story 수정
- Story 삭제
- Story 목록 조회
- Story 검색

예시 API

```
POST /api/stories

GET /api/stories

GET /api/stories/{id}

PUT /api/stories/{id}

DELETE /api/stories/{id}
```

---

## 8.4 ChapterController

ChapterController는 AI가 생성한 Chapter를 관리한다.

담당 기능

- 다음 Chapter 생성
- Chapter 조회
- Chapter 목록 조회

예시 API

```
POST /api/stories/{storyId}/chapters

GET /api/chapters/{id}

GET /api/stories/{storyId}/chapters
```

---

## 8.5 ChoiceController

ChoiceController는 사용자의 선택을 처리한다.

담당 기능

- Choice 조회
- Choice 선택
- 다음 Chapter 생성 요청

예시 API

```
GET /api/chapters/{id}/choices

POST /api/choices/{choiceId}
```

---

## 8.6 BookmarkController

BookmarkController는 북마크 기능을 담당한다.

담당 기능

- 북마크 등록
- 북마크 삭제
- 북마크 목록 조회

예시 API

```
POST /api/bookmarks

DELETE /api/bookmarks/{storyId}

GET /api/bookmarks
```

---

## 8.7 ReportController

ReportController는 Story 종료 후 생성되는 Ending Report를 관리한다.

담당 기능

- 리포트 조회
- 리포트 생성
- 리포트 공유

예시 API

```
GET /api/reports/{storyId}

POST /api/reports/{storyId}
```

---

## 8.8 PromptController

PromptController는 관리자 전용 기능이다.

담당 기능

- Prompt 등록
- Prompt 수정
- Prompt 버전 조회
- Prompt 활성화

예시 API

```
GET /api/admin/prompts

POST /api/admin/prompts

PUT /api/admin/prompts/{id}
```

---

## 8.9 AdminController

관리자 기능을 제공한다.

담당 기능

- 사용자 관리
- Story 관리
- AI 로그 조회
- 통계 조회

예시 API

```
GET /api/admin/users

GET /api/admin/stories

GET /api/admin/logs
```

---

# 9. Request Flow

Story 생성 요청의 흐름은 다음과 같다.

```
Client

↓

StoryController

↓

StoryService

↓

StoryRepository

↓

Database
```

AI 생성이 필요한 경우에는 다음 흐름을 따른다.

```
Client

↓

StoryController

↓

StoryService

↓

PromptBuilder

↓

AIService

↓

LLM API

↓

StoryRepository

↓

Database
```

---

# 10. Controller Design Principles

StorySeed의 모든 Controller는 다음 원칙을 따른다.

### Principle 1

Controller는 Business Logic를 작성하지 않는다.

---

### Principle 2

Entity를 직접 반환하지 않는다.

항상 Response DTO를 사용한다.

---

### Principle 3

Request Body는 DTO로 받는다.

Entity를 직접 Request로 사용하지 않는다.

---

### Principle 4

모든 예외 처리는 GlobalExceptionHandler가 담당한다.

Controller에서 try-catch를 남발하지 않는다.

---

### Principle 5

Controller는 가능한 한 얇게(Thin Controller) 유지한다.

---

# 11. Example Controller

```java
@RestController
@RequestMapping("/api/stories")
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;

    @PostMapping
    public ResponseEntity<StoryResponse> create(
            @Valid @RequestBody StoryCreateRequest request) {

        StoryResponse response = storyService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

}
```

Controller는 요청을 전달하고 응답을 반환하는 역할만 수행한다.

---

# 12. Next Section

다음 장에서는 StorySeed의 핵심 비즈니스 로직을 담당하는 Service 계층을 정의한다.

Service는

- Story 생성
- Choice 처리
- AI 호출
- Prompt 생성
- Ending Report 생성

등 모든 핵심 로직을 담당한다.

# 13. Service Design

## 13.1 Overview

Service 계층은 StorySeed의 핵심 비즈니스 로직을 담당한다.

Controller는 요청을 전달하고 응답을 반환하는 역할만 수행하며,

실제 데이터 처리와 AI 호출, Story 진행, 검증 로직은 모두 Service 계층에서 수행한다.

StorySeed에서는 Service가 도메인 규칙(Business Rule)의 중심이 된다.

---

## 13.2 Responsibilities

Service는 다음 기능을 담당한다.

- Story 생성
- Chapter 생성
- Choice 처리
- AI 호출
- Prompt 생성
- Story 종료
- Bookmark 관리
- 회원 관리
- 인증 처리
- Report 생성

---

## 13.3 Service Package Structure

```
service

├── AuthService
├── UserService
├── StoryService
├── ChapterService
├── ChoiceService
├── BookmarkService
├── ReportService
├── PromptService
├── AIService
└── AdminService
```

기능(Feature) 단위로 Service를 분리한다.

---

# 14. Service Responsibilities

## 14.1 AuthService

AuthService는 인증 관련 비즈니스 로직을 담당한다.

주요 기능

- 회원가입
- 로그인
- JWT 발급
- Refresh Token 관리
- OAuth 로그인

---

## 14.2 UserService

UserService는 회원 관련 기능을 담당한다.

주요 기능

- 회원 조회
- 회원 수정
- 비밀번호 변경
- 회원 탈퇴
- 환경설정 관리

---

## 14.3 StoryService

StoryService는 StorySeed에서 가장 중요한 Service이다.

담당 기능

- Story 생성
- Story 수정
- Story 삭제
- Story 조회
- Story 종료
- Character 생성
- 최초 Prompt 생성

Story 생성 시

- Character 생성
- StoryState 초기화
- 첫 Chapter 생성

까지 함께 수행한다.

---

## 14.4 ChapterService

ChapterService는 Chapter 생성 및 조회를 담당한다.

주요 기능

- Chapter 생성
- Chapter 조회
- Chapter 목록 조회
- Summary 생성 여부 판단

---

## 14.5 ChoiceService

ChoiceService는 사용자의 선택을 처리한다.

담당 기능

- Choice 선택
- ChoiceResult 반영
- StoryFlag 업데이트
- CharacterState 갱신
- Inventory 갱신
- 다음 Chapter 생성 요청

ChoiceService는 Story 진행의 핵심 역할을 담당한다.

---

## 14.6 AIService

AIService는 외부 LLM과의 통신을 담당한다.

주요 기능

- Prompt 전달
- AI 응답 수신
- Token 사용량 저장
- AI Generation Log 생성

AIService는 Story 생성 로직을 알지 못한다.

Prompt를 전달하고 응답을 반환하는 역할만 수행한다.

---

## 14.7 PromptService

PromptService는 AI에게 전달할 Prompt를 생성한다.

Prompt 생성 시

- Character
- StorySummary
- CharacterState
- StoryFlag
- Inventory
- 최근 Chapter

를 조합하여 PromptBuilder를 통해 최종 Prompt를 생성한다.

---

## 14.8 BookmarkService

Bookmark 등록 및 삭제를 담당한다.

동일 Story 중복 등록 여부를 검증한다.

---

## 14.9 ReportService

Story 종료 후 Ending Report를 생성한다.

담당 기능

- Story 전체 분석
- AI Report 생성
- 플레이 스타일 분석
- 최종 요약 생성

---

## 14.10 AdminService

관리자 기능을 담당한다.

예시

- Prompt 관리
- AI Log 조회
- 사용자 관리
- Story 관리
- 통계 조회

---

# 15. Service Flow

## Story 생성

```
StoryController

↓

StoryService

↓

Character 생성

↓

Prompt 생성

↓

AI 호출

↓

Chapter 저장

↓

Choice 생성

↓

Response 반환
```

---

## Choice 선택

```
ChoiceController

↓

ChoiceService

↓

ChoiceResult 적용

↓

StoryFlag 갱신

↓

CharacterState 갱신

↓

Inventory 갱신

↓

Prompt 생성

↓

AI 호출

↓

Chapter 저장

↓

Choice 생성

↓

Response 반환
```

---

## Story 종료

```
StoryController

↓

StoryService

↓

Ending 분석

↓

AI 호출

↓

EndingReport 저장

↓

Story 상태 변경

↓

완료
```

---

# 16. Transaction Strategy

StorySeed에서는 Service 계층에서 Transaction을 관리한다.

예시

```java
@Transactional
public StoryResponse createStory(
        StoryCreateRequest request) {

}
```

Controller에서는 Transaction을 사용하지 않는다.

Repository에서도 Transaction을 직접 관리하지 않는다.

---

## Transaction 적용 대상

다음 기능에는 Transaction을 적용한다.

- 회원가입
- Story 생성
- Choice 선택
- Chapter 생성
- Ending Report 생성
- Bookmark 등록
- 회원 탈퇴

조회 기능은 기본적으로 ReadOnly Transaction을 사용한다.

```java
@Transactional(readOnly = true)
```

---

# 17. Service Design Principles

StorySeed의 모든 Service는 다음 원칙을 따른다.

### Principle 1

하나의 Service는 하나의 책임만 가진다.

---

### Principle 2

Service 간 순환 참조(Circular Dependency)를 허용하지 않는다.

---

### Principle 3

Controller는 Service만 호출한다.

Repository를 직접 호출하지 않는다.

---

### Principle 4

Service는 DTO를 입력받고 DTO를 반환한다.

Entity를 직접 외부 계층으로 전달하지 않는다.

---

### Principle 5

외부 API 호출은 AIService로만 수행한다.

---

### Principle 6

Prompt 생성은 PromptService에서만 수행한다.

---

### Principle 7

모든 비즈니스 규칙은 Service 계층에서 관리한다.

---

# 18. Example Service

```java
@Service
@RequiredArgsConstructor
@Transactional
public class StoryService {

    private final StoryRepository storyRepository;
    private final CharacterService characterService;
    private final PromptService promptService;
    private final AIService aiService;

    public StoryResponse create(
            StoryCreateRequest request) {

        // Story 생성

        // Character 생성

        // Prompt 생성

        // AI 호출

        // Chapter 저장

        return StoryResponse.from(...);

    }

}
```

---

# 19. Next Section

다음 장에서는 Repository 계층을 정의한다.

Repository는 데이터 접근만 담당하며,

- JPA Repository
- Query Method
- JPQL
- QueryDSL
- Fetch Join

등의 사용 원칙을 정의한다.
