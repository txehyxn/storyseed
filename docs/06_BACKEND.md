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

# 20. Repository Design

## 20.1 Overview

Repository는 데이터베이스 접근(Data Access Layer)을 담당한다.

StorySeed에서는 Spring Data JPA를 사용하여 데이터 조회 및 저장을 수행한다.

Repository는 데이터 접근만 담당하며 비즈니스 로직을 포함하지 않는다.

---

## 20.2 Responsibilities

Repository의 역할은 다음과 같다.

- 데이터 저장(Create)
- 데이터 조회(Read)
- 데이터 수정(Update)
- 데이터 삭제(Delete)
- 조건 검색(Query)

다음 기능은 Repository에서 수행하지 않는다.

- 비즈니스 로직
- AI 호출
- 데이터 검증
- 권한 검사

이러한 로직은 Service 계층에서 처리한다.

---

## 20.3 Repository Package Structure

```
repository

├── UserRepository
├── StoryRepository
├── ChapterRepository
├── ChoiceRepository
├── BookmarkRepository
├── ReportRepository
├── PromptRepository
├── RefreshTokenRepository
└── AIGenerationLogRepository
```

Entity마다 하나의 Repository를 생성하는 것을 원칙으로 한다.

---

# 21. Repository Design Principles

StorySeed Repository는 다음 원칙을 따른다.

### Principle 1

Repository는 Entity 하나만 담당한다.

---

### Principle 2

Repository는 비즈니스 로직을 포함하지 않는다.

잘못된 예시

```java
public void createStoryAndCharacter() {

}
```

올바른 예시

```java
storyRepository.save(story);
```

---

### Principle 3

복잡한 로직은 Service에서 조합한다.

예시

```
Story 저장

↓

Character 저장

↓

Chapter 저장
```

Repository는 각각 저장만 수행한다.

---

### Principle 4

필요한 데이터만 조회한다.

조회 성능을 고려하여 불필요한 데이터를 함께 가져오지 않는다.

---

# 22. Query Method Strategy

StorySeed는 Spring Data JPA의 Query Method를 우선 사용한다.

예시

```java
findById()

findByEmail()

findByStoryId()

findAllByUser()

existsByEmail()

existsByNickname()

deleteByStoryId()
```

조회 조건이 단순한 경우에는 Query Method를 사용한다.

---

# 23. JPQL Usage

복잡한 조회가 필요한 경우에는 JPQL을 사용한다.

예시

- 북마크 목록 조회
- Story 검색
- 사용자별 Story 통계

JPQL은 필요한 경우에만 사용한다.

---

# 24. Pagination Strategy

Story 목록 조회는 Pagination을 적용한다.

예시

```java
Page<Story> findAll(Pageable pageable);
```

기본 조회는 Page 단위로 수행한다.

---

# 25. Repository Examples

## UserRepository

```java
public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

}
```

---

## StoryRepository

```java
public interface StoryRepository
        extends JpaRepository<Story, Long> {

    Page<Story> findAllByUser(
            User user,
            Pageable pageable);

}
```

---

## BookmarkRepository

```java
public interface BookmarkRepository
        extends JpaRepository<Bookmark, Long> {

    boolean existsByUserAndStory(
            User user,
            Story story);

}
```

---

# 26. MVP Scope

현재 프로젝트에서 구현하는 Repository는 다음과 같다.

- UserRepository
- StoryRepository
- ChapterRepository
- ChoiceRepository
- BookmarkRepository
- RefreshTokenRepository

AI Log, Prompt, Report 관련 Repository는 기능 구현 시점에 추가한다.

---

# 27. Future Expansion

프로젝트가 확장되면 다음 기술을 검토한다.

- QueryDSL
- EntityGraph
- Specification
- Redis Cache
- Elasticsearch

현재 MVP에서는 사용하지 않는다.

---

# 28. Next Section

다음 장에서는 DTO(Data Transfer Object) 설계 원칙을 정의한다.

StorySeed는 Entity를 직접 API에 노출하지 않으며, Request DTO와 Response DTO를 분리하여 사용한다.

# 29. DTO Design

## 29.1 Overview

StorySeed는 Entity를 API 응답으로 직접 반환하지 않는다.

모든 요청(Request)과 응답(Response)은 DTO(Data Transfer Object)를 사용한다.

DTO를 사용함으로써

- Entity 보호
- API 구조 분리
- 유지보수성 향상
- 보안 강화

를 달성할 수 있다.

---

## 29.2 DTO Responsibilities

DTO는 다음 역할을 담당한다.

- 클라이언트 요청 전달
- 응답 데이터 반환
- 입력값 검증
- API 데이터 구조 정의

DTO에는 비즈니스 로직을 작성하지 않는다.

---

## 29.3 DTO Package Structure

```
dto

├── auth
│   ├── LoginRequest
│   ├── LoginResponse
│   ├── SignupRequest
│   └── TokenResponse
│
├── user
│   ├── UserResponse
│   ├── UserUpdateRequest
│   └── UserSettingResponse
│
├── story
│   ├── StoryCreateRequest
│   ├── StoryUpdateRequest
│   ├── StoryResponse
│   └── StorySummaryResponse
│
├── chapter
│   ├── ChapterResponse
│   └── ChapterCreateResponse
│
├── choice
│   ├── ChoiceRequest
│   └── ChoiceResponse
│
├── bookmark
│   └── BookmarkResponse
│
└── report
    └── EndingReportResponse
```

기능(Feature) 단위로 DTO를 분리한다.

---

# 30. Request DTO

Request DTO는 클라이언트가 서버에 전달하는 데이터를 정의한다.

예시

```java
public class StoryCreateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String genre;

    @NotBlank
    private String world;

}
```

Request DTO는 Validation을 포함할 수 있다.

---

# 31. Response DTO

Response DTO는 클라이언트에게 반환되는 데이터를 정의한다.

예시

```java
public class StoryResponse {

    private Long id;

    private String title;

    private String genre;

    private String status;

}
```

Response DTO는 필요한 데이터만 포함한다.

---

# 32. Validation Strategy

StorySeed는 Jakarta Validation을 사용한다.

대표적으로 사용하는 Validation

- @NotNull
- @NotBlank
- @Email
- @Size
- @Pattern

예시

```java
@NotBlank
private String nickname;

@Email
private String email;

@Size(max = 50)
private String title;
```

Validation 실패 시 GlobalExceptionHandler에서 처리한다.

---

# 33. DTO Design Principles

### Principle 1

Entity를 Request로 사용하지 않는다.

---

### Principle 2

Entity를 Response로 반환하지 않는다.

---

### Principle 3

Request DTO와 Response DTO를 분리한다.

---

### Principle 4

DTO에는 비즈니스 로직을 작성하지 않는다.

---

### Principle 5

Validation은 DTO에서 수행한다.

---

# 34. Entity ↔ DTO Conversion

StorySeed에서는 Service 계층에서 Entity와 DTO를 변환한다.

예시

```
Controller

↓

Request DTO

↓

Service

↓

Entity

↓

Repository

↓

Entity

↓

Response DTO

↓

Controller
```

별도의 Mapper 라이브러리는 사용하지 않는다.

규모가 크지 않은 프로젝트이므로 필요한 변환 메서드를 직접 작성한다.

---

# 35. MVP Scope

현재 프로젝트에서 사용하는 DTO

- LoginRequest
- SignupRequest
- StoryCreateRequest
- StoryResponse
- ChapterResponse
- ChoiceRequest
- ChoiceResponse
- UserResponse
- BookmarkResponse

프로젝트 진행에 따라 필요한 DTO를 추가한다.

---

# 36. Future Expansion

프로젝트 규모가 커질 경우 다음 기술을 검토한다.

- MapStruct
- Record DTO
- Response Wrapper
- API Versioning

현재 MVP에서는 적용하지 않는다.

---

# 37. Next Section

다음 장에서는 Exception Handling 전략을 정의한다.

GlobalExceptionHandler를 통해 예외를 일관된 형식으로 처리하고, 사용자에게 명확한 오류 메시지를 제공하는 방법을 설명한다.

# 38. Exception Handling

## 38.1 Overview

StorySeed는 모든 예외를 일관된 형식으로 처리하기 위해 Global Exception Handler를 사용한다.

Controller마다 try-catch를 작성하지 않고, 발생한 예외는 GlobalExceptionHandler에서 처리한다.

이를 통해

- 일관된 API 응답
- 유지보수성 향상
- 코드 중복 제거
- 명확한 오류 메시지 제공

을 목표로 한다.

---

## 38.2 Exception Structure

```
exception

├── GlobalExceptionHandler
├── ErrorResponse
├── ErrorCode
│
├── UserNotFoundException
├── StoryNotFoundException
├── ChapterNotFoundException
├── ChoiceNotFoundException
├── BookmarkAlreadyExistsException
├── InvalidChoiceException
└── UnauthorizedException
```

MVP에서는 필요한 예외만 정의한다.

---

# 39. Error Response Format

모든 API는 오류 발생 시 동일한 형식으로 응답한다.

예시

```json
{
  "timestamp": "2026-07-19T20:30:00",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Story를 찾을 수 없습니다.",
  "path": "/api/stories/1"
}
```

응답 형식을 통일하여 프론트엔드에서 쉽게 처리할 수 있도록 한다.

---

# 40. GlobalExceptionHandler

모든 예외는 GlobalExceptionHandler에서 처리한다.

예시

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStoryNotFound(
            StoryNotFoundException e) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        HttpStatus.NOT_FOUND,
                        e.getMessage()));
    }

}
```

Controller에서는 개별 try-catch를 작성하지 않는다.

---

# 41. Custom Exception

비즈니스 로직에서 발생하는 예외는 Custom Exception으로 정의한다.

예시

```java
public class StoryNotFoundException
        extends RuntimeException {

    public StoryNotFoundException() {
        super("Story를 찾을 수 없습니다.");
    }

}
```

Service 계층에서 필요한 시점에 발생시킨다.

---

# 42. Validation Exception

DTO Validation 실패는 자동으로 처리한다.

예시

```java
@NotBlank
private String title;
```

Validation 실패 시

```
MethodArgumentNotValidException
```

이 발생하며 GlobalExceptionHandler에서 처리한다.

---

# 43. Common Exception Cases

StorySeed에서 자주 발생하는 예외는 다음과 같다.

| Exception | 설명 |
|------------|------|
| UserNotFoundException | 회원이 존재하지 않음 |
| StoryNotFoundException | Story가 존재하지 않음 |
| ChapterNotFoundException | Chapter가 존재하지 않음 |
| ChoiceNotFoundException | Choice가 존재하지 않음 |
| BookmarkAlreadyExistsException | 이미 북마크한 Story |
| InvalidChoiceException | 잘못된 선택 |
| UnauthorizedException | 인증 실패 |

필요한 경우 프로젝트 진행 중 추가한다.

---

# 44. Error Code Strategy

ErrorCode Enum을 사용하여 오류를 관리한다.

예시

```java
public enum ErrorCode {

    USER_NOT_FOUND,

    STORY_NOT_FOUND,

    CHAPTER_NOT_FOUND,

    INVALID_CHOICE,

    BOOKMARK_ALREADY_EXISTS,

    INTERNAL_SERVER_ERROR

}
```

서비스 규모가 커져도 오류를 일관되게 관리할 수 있다.

---

# 45. Design Principles

StorySeed는 다음 원칙을 따른다.

### Principle 1

Controller에서는 try-catch를 사용하지 않는다.

---

### Principle 2

모든 예외는 RuntimeException 기반으로 작성한다.

---

### Principle 3

비즈니스 예외는 Custom Exception으로 관리한다.

---

### Principle 4

예외 메시지는 사용자에게 이해하기 쉽게 작성한다.

---

### Principle 5

오류 응답 형식은 모든 API에서 동일하게 유지한다.

---

# 46. MVP Scope

현재 구현하는 예외

- UserNotFoundException
- StoryNotFoundException
- ChapterNotFoundException
- ChoiceNotFoundException
- BookmarkAlreadyExistsException
- GlobalExceptionHandler
- ErrorResponse

프로젝트 진행에 따라 필요한 예외를 추가한다.

---

# 47. Future Expansion

향후 프로젝트가 확장되면 다음 기능을 검토한다.

- ErrorCode 세분화
- 국제화(i18n) 오류 메시지
- 로그 추적 ID(Request ID)
- API 오류 문서 자동화

현재 MVP에서는 적용하지 않는다.

---

# 48. Next Section

다음 장에서는 Backend 보안(Security) 구조를 정의한다.

JWT 인증, Spring Security 설정, 권한(Role) 관리 및 인증 흐름을 설명한다.

# 49. Security Structure

## 49.1 Overview

StorySeed는 회원 인증과 접근 권한 관리를 위해 Spring Security를 사용한다.

MVP에서는 서버가 로그인 상태를 관리하는 **Session 기반 인증 방식**을 적용한다.

현재 프로젝트는 Spring Boot와 Thymeleaf를 함께 사용하는 웹 애플리케이션이므로 JWT 방식보다 Session 인증이 구현이 단순하고 안정적이다.

---

## 49.2 Security Goals

StorySeed의 보안 기능은 다음을 목표로 한다.

- 회원가입 및 로그인 처리
- 비밀번호 암호화
- 로그인 사용자 식별
- 비로그인 사용자의 접근 제한
- 다른 사용자의 Story 접근 차단
- 관리자 기능 접근 제한
- CSRF 공격 방어
- 안전한 로그아웃 처리

MVP에서는 실제 서비스 사용에 필요한 기본 보안 기능만 구현한다.

---

# 50. Authentication Strategy

StorySeed MVP는 Session 기반 인증을 사용한다.

인증 흐름은 다음과 같다.

```text
사용자가 이메일과 비밀번호 입력
        ↓
로그인 요청
        ↓
Spring Security 인증
        ↓
UserDetailsService 사용자 조회
        ↓
비밀번호 검증
        ↓
인증 성공
        ↓
Session 생성
        ↓
로그인 상태 유지
```

로그인 성공 후 서버는 Session에 인증 정보를 저장하며, 이후 요청마다 Session을 통해 로그인 여부를 확인한다.

---

# 51. Why Session Authentication

StorySeed는 다음 이유로 Session 인증을 선택하였다.

- Spring Security와 자연스럽게 연동된다.
- Thymeleaf 기반 프로젝트에 적합하다.
- 구현이 단순하다.
- JWT 및 Refresh Token 관리가 필요 없다.
- 프로젝트 완성 속도를 높일 수 있다.
- 유지보수가 쉽다.

향후 React 등으로 프론트엔드와 백엔드를 분리할 경우 JWT 인증으로 전환할 수 있다.

---

# 52. Security Package Structure

```text
security

├── SecurityConfig
├── CustomUserDetails
└── CustomUserDetailsService
```

MVP에서는 꼭 필요한 클래스만 구현한다.

---

# 53. User Authentication Data

로그인에 필요한 사용자 정보는 다음과 같다.

```text
User

├── id
├── email
├── password
├── nickname
├── role
├── createdAt
└── updatedAt
```

로그인 ID는 이메일을 사용하며, 비밀번호는 BCrypt로 암호화하여 저장한다.

---

# 54. Password Encryption

비밀번호 암호화에는 BCryptPasswordEncoder를 사용한다.

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

회원가입 시

```java
String encodedPassword =
        passwordEncoder.encode(request.getPassword());
```

로그인 시 Spring Security가 암호화된 비밀번호를 자동으로 비교한다.

---

# 55. CustomUserDetails

Spring Security에서 현재 로그인한 사용자의 정보를 관리하기 위해 CustomUserDetails를 사용한다.

CustomUserDetails에는 다음 정보를 포함한다.

- 사용자 ID
- 이메일
- 닉네임
- 권한(Role)

MVP에서는 계정 잠금, 만료 기능은 사용하지 않는다.

---

# 56. CustomUserDetailsService

CustomUserDetailsService는 이메일을 기준으로 사용자를 조회한다.

```java
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "사용자를 찾을 수 없습니다."
                        )
                );

        return new CustomUserDetails(user);
    }

}
```

---

# 57. Security Configuration

SecurityConfig에서는 URL 접근 권한과 로그인 설정을 관리한다.

기본 접근 권한

| URL | 권한 |
|------|------|
| / | 모두 허용 |
| /auth/** | 모두 허용 |
| /css/** | 모두 허용 |
| /js/** | 모두 허용 |
| /images/** | 모두 허용 |
| /stories/** | 로그인 사용자 |
| /users/** | 로그인 사용자 |
| /admin/** | 관리자 |

---

# 58. Role Strategy

StorySeed는 최소한의 권한만 사용한다.

```java
public enum Role {

    USER,

    ADMIN

}
```

- USER : 일반 회원
- ADMIN : 관리자

복잡한 권한 체계는 MVP 이후 확장한다.

---

# 59. Current User Access

현재 로그인한 사용자는 Session에서 가져온다.

```java
@AuthenticationPrincipal
CustomUserDetails userDetails
```

사용자 ID를 URL이나 요청 파라미터로 전달받지 않는다.

---

# 60. Story Ownership Validation

Story 조회, 수정, 삭제 시 반드시 Story 소유자를 확인한다.

Repository 예시

```java
Optional<Story> findByIdAndUserId(
        Long storyId,
        Long userId
);
```

이를 통해 다른 사용자의 Story 접근을 방지한다.

---

# 61. CSRF Protection

Spring Security의 기본 CSRF 보호 기능을 유지한다.

MVP에서는 CSRF를 비활성화하지 않는다.

---

# 62. Login & Logout

로그인 기능

- 이메일 로그인
- 로그인 실패 메시지 표시
- Session 생성

로그아웃 기능

- Session 삭제
- JSESSIONID 삭제
- 메인 페이지 이동

---

# 63. Security Design Principles

StorySeed는 다음 원칙을 따른다.

1. 비밀번호는 BCrypt로 암호화한다.
2. 로그인 사용자는 Session으로 관리한다.
3. Story는 소유자만 접근할 수 있다.
4. 관리자 기능은 Role 기반으로 제한한다.
5. CSRF 보호를 유지한다.
6. 내부 오류 정보를 사용자에게 노출하지 않는다.

---

# 64. MVP Scope

현재 MVP에서 구현하는 보안 기능

- Spring Security
- Session 로그인
- 회원가입
- 로그인
- 로그아웃
- BCrypt 암호화
- CustomUserDetails
- URL 권한 관리
- Story 소유권 검사
- CSRF 보호

---

# 65. Future Expansion

서비스 확장 시 다음 기능을 추가할 수 있다.

- JWT 인증
- Refresh Token
- OAuth2 로그인
- 이메일 인증
- 비밀번호 찾기
- 로그인 실패 횟수 제한
- 2단계 인증

현재 MVP에서는 구현하지 않는다.

---

# 66. Next Section

다음 장에서는 AI Service Layer를 설명한다.

AI 요청부터 Chapter 생성, 선택지 생성, 데이터 저장까지의 전체 흐름을 정의한다.
```md
# 67. AI Service Layer

## 67.1 Overview

AI Service Layer는 StorySeed의 핵심 기능인 AI 기반 이야기 생성을 담당한다.

사용자가 입력한 이야기 설정과 현재까지의 진행 내용을 기반으로 AI API를 호출하여 새로운 Chapter와 선택지를 생성한다.

AI 관련 로직은 Controller나 Service에 직접 작성하지 않고 AIService를 통해 관리한다.

---

## 67.2 Goals

AI Service Layer의 주요 목적은 다음과 같다.

- 첫 번째 Chapter 생성
- 다음 Chapter 생성
- 선택지 생성
- Prompt 생성
- AI 응답 검증
- AI 호출 실패 처리

MVP에서는 하나의 AI 모델만 사용한다.

---

# 68. Package Structure

```text
ai

├── AIService
├── AIClient
└── PromptBuilder
```

MVP에서는 최소한의 구조만 유지한다.

필요한 기능이 증가하면 DTO, Exception 등을 추가한다.

---

# 69. Responsibilities

## AIService

AI 생성 과정을 관리한다.

주요 역할

- Prompt 생성 요청
- AIClient 호출
- 응답 검증
- 생성 결과 반환

---

## AIClient

외부 AI API와 통신한다.

주요 역할

- API 요청
- 응답 수신
- 예외 처리

---

## PromptBuilder

AI에게 전달할 Prompt를 생성한다.

포함 정보

- Story 설정
- 이전 Chapter
- 사용자 선택
- 생성 규칙

---

# 70. AI Generation Flow

```text
사용자 요청
        ↓
StoryController
        ↓
StoryService
        ↓
AIService
        ↓
PromptBuilder
        ↓
AIClient
        ↓
AI API
        ↓
응답 검증
        ↓
Chapter 저장
        ↓
Choice 저장
        ↓
사용자 화면 반환
```

---

# 71. First Chapter Generation

Story 생성 시 AI가 첫 Chapter를 생성한다.

입력 정보

- 제목
- 장르
- 세계관
- 주인공
- 주인공 특징
- 시작 설정

생성 과정

```text
Story 저장
        ↓
Prompt 생성
        ↓
AI 호출
        ↓
Chapter 생성
        ↓
Choice 생성
        ↓
DB 저장
```

AI 생성 실패 시 Story는 저장하지 않고 생성을 취소한다.

---

# 72. Next Chapter Generation

사용자가 선택지를 선택하면 다음 Chapter를 생성한다.

AI에 전달하는 정보

- Story 설정
- 최근 Chapter
- 사용자 선택
- 현재 진행 정보

생성 과정

```text
선택지 클릭
        ↓
Prompt 생성
        ↓
AI 호출
        ↓
Chapter 생성
        ↓
Choice 생성
        ↓
DB 저장
```

---

# 73. Prompt Strategy

Prompt에는 다음 정보를 포함한다.

- Story 설정
- 이전 이야기
- 사용자 선택
- 생성 규칙

예시

```text
당신은 인터랙티브 소설 작가입니다.

다음 정보를 기반으로 새로운 Chapter를 작성하세요.

- 기존 설정을 유지합니다.
- 사용자의 선택을 반영합니다.
- 3개의 선택지를 생성합니다.
- JSON 형식으로 응답합니다.
```

---

# 74. AI Response Format

AI 응답은 JSON 형식으로 받는다.

```json
{
  "title": "추격자의 흔적",
  "content": "...",
  "choices": [
    {
      "text": "근위대를 따라간다."
    },
    {
      "text": "골목을 조사한다."
    },
    {
      "text": "왕궁으로 돌아간다."
    }
  ]
}
```

---

# 75. Response Validation

저장 전 다음 항목을 검증한다.

- 제목 존재 여부
- 본문 존재 여부
- 선택지 존재 여부
- 선택지 2개 이상
- 빈 문자열 여부

검증 실패 시 저장하지 않는다.

---

# 76. AIService Flow

```text
Prompt 생성
        ↓
AI 호출
        ↓
응답 검증
        ↓
Chapter 반환
```

AIService는 데이터 저장을 담당하지 않는다.

StoryService가 저장을 수행한다.

---

# 77. API Key Management

API Key는 코드에 작성하지 않는다.

예시

```yaml
ai:
  api-key: ${AI_API_KEY}
  model: ${AI_MODEL}
```

민감한 정보는 GitHub에 업로드하지 않는다.

---

# 78. Duplicate Request Prevention

AI 생성 중에는 중복 요청을 방지한다.

적용 내용

- 버튼 비활성화
- 로딩 화면 표시
- 서버 중복 요청 검사

---

# 79. Error Handling

다음 오류를 처리한다.

- API 연결 실패
- Timeout
- JSON 변환 실패
- 빈 응답
- 선택지 누락

사용자에게는 간단한 오류 메시지를 제공한다.

```text
이야기를 생성하는 중 문제가 발생했습니다.

잠시 후 다시 시도해 주세요.
```

---

# 80. Story Context Strategy

MVP에서는 다음 정보만 AI에게 전달한다.

- Story 설정
- 최근 Chapter
- 사용자 선택

Story 전체를 매번 전달하지 않는다.

---

# 81. Content Length

권장 길이

- 제목 : 50자 이하
- 본문 : 500~1000자
- 선택지 : 3개

---

# 82. Design Principles

StorySeed AI는 다음 원칙을 따른다.

1. Controller는 AI API를 직접 호출하지 않는다.
2. AI 통신은 AIClient가 담당한다.
3. Prompt 생성은 PromptBuilder가 담당한다.
4. AI 응답은 반드시 검증한다.
5. API Key는 외부 설정으로 관리한다.
6. 실패한 응답은 저장하지 않는다.
7. MVP에서는 하나의 AI 모델만 사용한다.

---

# 83. MVP Scope

MVP에서 구현하는 기능

- AI API 연동
- 첫 Chapter 생성
- 다음 Chapter 생성
- 선택지 생성
- PromptBuilder
- AIClient
- 응답 검증
- 오류 처리
- 로딩 화면
- 중복 요청 방지

---

# 84. Future Expansion

MVP 이후 추가 가능한 기능

- Story 요약
- 긴 문맥 관리
- Prompt 관리
- AI 모델 변경
- 이미지 생성
- AI 사용량 통계
- 비동기 생성

---

# 85. Next Section

다음 장에서는 Backend Flow를 정의한다.

회원가입부터 Story 생성, AI 생성, Story 진행까지 전체 요청 흐름을 설명한다.
```

```md
# 86. Backend Flow

## 86.1 Overview

Backend Flow는 사용자의 요청이 서버 내부에서 어떻게 처리되는지 정의한다.

StorySeed는 Controller → Service → Repository 구조를 기반으로 요청을 처리하며, AI 생성이 필요한 경우 AIService를 통해 외부 AI API와 통신한다.

모든 요청은 역할이 명확하게 분리된 계층을 거쳐 처리된다.

---

## 86.2 Overall Request Flow

```text
Browser
    ↓
Controller
    ↓
Service
    ↓
Repository
    ↓
Database

(필요 시)

Service
    ↓
AIService
    ↓
AIClient
    ↓
AI API
```

Controller는 요청과 응답만 담당하며, 실제 비즈니스 로직은 Service에서 처리한다.

---

# 87. User Registration Flow

회원가입 처리 과정은 다음과 같다.

```text
회원가입 요청
        ↓
Request DTO 검증
        ↓
이메일 중복 확인
        ↓
비밀번호 암호화
        ↓
User 저장
        ↓
회원가입 완료
```

회원가입 실패 시 저장을 수행하지 않는다.

---

# 88. Login Flow

로그인은 Spring Security가 처리한다.

```text
로그인 요청
        ↓
Spring Security
        ↓
User 조회
        ↓
비밀번호 검증
        ↓
Session 생성
        ↓
로그인 완료
```

로그인 성공 후 인증 정보는 Session에 저장된다.

---

# 89. Story Creation Flow

새로운 Story를 생성하는 과정이다.

```text
Story 생성 요청
        ↓
입력값 검증
        ↓
Story 저장
        ↓
AIService 호출
        ↓
첫 Chapter 생성
        ↓
Choice 생성
        ↓
DB 저장
        ↓
Story 화면 이동
```

AI 생성 실패 시 Story는 저장하지 않는다.

---

# 90. Story Continue Flow

사용자가 Story를 이어서 진행하는 과정이다.

```text
Story 조회
        ↓
현재 사용자 확인
        ↓
최근 Chapter 조회
        ↓
Story 화면 출력
```

본인의 Story만 조회할 수 있다.

---

# 91. Choice Selection Flow

사용자가 선택지를 클릭하면 다음 Chapter를 생성한다.

```text
선택지 클릭
        ↓
Story 소유권 확인
        ↓
Choice 조회
        ↓
AIService 호출
        ↓
다음 Chapter 생성
        ↓
Choice 생성
        ↓
DB 저장
        ↓
다음 화면 출력
```

---

# 92. Bookmark Flow

북마크 처리 과정이다.

```text
북마크 요청
        ↓
Story 조회
        ↓
중복 여부 확인
        ↓
Bookmark 저장
        ↓
완료
```

이미 북마크한 Story는 중복 저장하지 않는다.

---

# 93. Report Flow

사용자가 Story를 신고하는 과정이다.

```text
신고 요청
        ↓
Story 조회
        ↓
신고 내용 검증
        ↓
Report 저장
        ↓
완료
```

동일 사용자의 중복 신고는 제한할 수 있다.

---

# 94. Admin Flow

관리자 기능 처리 과정이다.

```text
관리자 요청
        ↓
ADMIN 권한 확인
        ↓
Service 실행
        ↓
DB 저장
        ↓
응답 반환
```

권한이 없는 사용자는 접근할 수 없다.

---

# 95. Exception Flow

예외 발생 시 처리 과정이다.

```text
Controller
        ↓
Service
        ↓
Exception 발생
        ↓
GlobalExceptionHandler
        ↓
ErrorResponse 반환
```

모든 예외는 공통 Exception Handler에서 처리한다.

---

# 96. Transaction Flow

데이터 저장 과정은 하나의 Transaction으로 처리한다.

```text
Transaction 시작
        ↓
데이터 저장
        ↓
AI 결과 저장
        ↓
Commit
```

오류 발생 시

```text
Transaction 시작
        ↓
오류 발생
        ↓
Rollback
```

불완전한 데이터가 저장되지 않도록 한다.

---

# 97. Validation Flow

사용자의 입력은 저장 전에 검증한다.

검증 항목

- 필수값
- 문자열 길이
- 이메일 형식
- 중복 여부
- 권한 확인

검증 실패 시 저장하지 않는다.

---

# 98. Authorization Flow

로그인 사용자의 권한을 확인한다.

```text
Request
    ↓
Session 확인
    ↓
Role 확인
    ↓
요청 처리
```

Story 조회 시에는 소유권도 함께 확인한다.

---

# 99. AI Generation Flow

AI 생성 과정은 다음과 같다.

```text
StoryService
        ↓
AIService
        ↓
PromptBuilder
        ↓
AIClient
        ↓
AI API
        ↓
응답 검증
        ↓
Chapter 저장
```

AI 관련 로직은 AIService에서만 처리한다.

---

# 100. Backend Design Principles

StorySeed Backend는 다음 원칙을 따른다.

1. Controller는 요청과 응답만 담당한다.
2. 비즈니스 로직은 Service에서 처리한다.
3. Repository는 데이터 접근만 담당한다.
4. AI 호출은 AIService에서 처리한다.
5. 모든 예외는 GlobalExceptionHandler에서 처리한다.
6. Transaction을 사용하여 데이터 정합성을 유지한다.
7. 사용자의 권한과 Story 소유권을 항상 확인한다.

---

# 101. MVP Scope

MVP에서 구현하는 Backend Flow

- 회원가입
- 로그인
- Story 생성
- Story 조회
- Story 이어하기
- AI Chapter 생성
- 선택지 생성
- 북마크
- 신고
- 관리자 기능
- 예외 처리
- 권한 관리
- Transaction 처리

---

# 102. Future Expansion

MVP 이후 확장 가능한 기능

- 비동기 AI 생성
- 메시지 큐
- Redis 캐시
- 이벤트 기반 처리
- 실시간 알림
- 분산 Transaction
- AI 작업 큐
- 서버 모니터링

현재는 단일 서버 환경을 기준으로 구현한다.

---

# 103. Next Section

다음 장에서는 Database Design을 정의한다.

ERD와 테이블 관계, 컬럼 설계, 인덱스 전략을 설명한다.
```

```md
# 104. Database Design

## 104.1 Overview

StorySeed는 MySQL을 사용하여 데이터를 저장한다.

데이터베이스는 정규화를 기본으로 설계하며, 조회 성능이 필요한 경우 인덱스를 추가한다.

MVP에서는 불필요한 테이블을 최소화하여 유지보수가 쉬운 구조를 목표로 한다.

---

## 104.2 Database Goals

데이터베이스 설계 목표는 다음과 같다.

- 데이터 무결성 유지
- 명확한 테이블 관계
- 쉬운 유지보수
- 빠른 조회
- AI Story 진행 데이터 저장
- 확장 가능한 구조

---

# 105. Database Structure

StorySeed MVP의 테이블 구성은 다음과 같다.

```text
users

stories

chapters

choices

bookmarks

reports
```

MVP에서는 핵심 기능에 필요한 테이블만 구현한다.

---

# 106. Entity Relationship

```text
User
 │
 ├────── Story
 │          │
 │          ├────── Chapter
 │          │            │
 │          │            └────── Choice
 │          │
 │          ├────── Bookmark
 │          │
 │          └────── Report
 │
 └──────────────────────────────
```

모든 Story는 하나의 User가 생성한다.

하나의 Story는 여러 Chapter를 가진다.

하나의 Chapter는 여러 Choice를 가진다.

---

# 107. User Table

사용자 정보를 저장한다.

주요 컬럼

| 컬럼 | 설명 |
|------|------|
| id | PK |
| email | 로그인 이메일 |
| password | 암호화된 비밀번호 |
| nickname | 닉네임 |
| role | USER / ADMIN |
| created_at | 생성일 |
| updated_at | 수정일 |

이메일은 UNIQUE 제약조건을 적용한다.

---

# 108. Story Table

Story 기본 정보를 저장한다.

주요 컬럼

| 컬럼 | 설명 |
|------|------|
| id | PK |
| user_id | 작성자 |
| title | 제목 |
| genre | 장르 |
| world | 세계관 |
| protagonist_name | 주인공 이름 |
| protagonist_description | 주인공 설명 |
| status | 진행 상태 |
| created_at | 생성일 |
| updated_at | 수정일 |

Story 설정 정보는 AI Prompt 생성에도 사용된다.

---

# 109. Chapter Table

각 Chapter를 저장한다.

주요 컬럼

| 컬럼 | 설명 |
|------|------|
| id | PK |
| story_id | Story |
| chapter_number | Chapter 번호 |
| title | Chapter 제목 |
| content | Chapter 내용 |
| created_at | 생성일 |

Story 하나에는 여러 Chapter가 존재한다.

---

# 110. Choice Table

사용자가 선택할 수 있는 선택지를 저장한다.

주요 컬럼

| 컬럼 | 설명 |
|------|------|
| id | PK |
| chapter_id | Chapter |
| choice_order | 표시 순서 |
| content | 선택지 내용 |

MVP에서는 선택지 개수를 3개로 고정한다.

---

# 111. Bookmark Table

사용자의 북마크를 저장한다.

주요 컬럼

| 컬럼 | 설명 |
|------|------|
| id | PK |
| user_id | 사용자 |
| story_id | Story |
| created_at | 생성일 |

동일 Story를 여러 번 북마크하지 못하도록 UNIQUE 제약조건을 적용한다.

---

# 112. Report Table

신고 정보를 저장한다.

주요 컬럼

| 컬럼 | 설명 |
|------|------|
| id | PK |
| user_id | 신고자 |
| story_id | 신고 대상 |
| reason | 신고 사유 |
| created_at | 신고일 |

동일 사용자의 중복 신고는 제한할 수 있다.

---

# 113. Relationship

테이블 관계는 다음과 같다.

```text
User (1)
    │
    └────── Story (N)
                  │
                  └────── Chapter (N)
                                │
                                └────── Choice (N)

User (1)
    └────── Bookmark (N)

Story (1)
    └────── Bookmark (N)

User (1)
    └────── Report (N)

Story (1)
    └────── Report (N)
```

---

# 114. Primary Key Strategy

모든 테이블은 Long 타입의 Auto Increment Primary Key를 사용한다.

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

UUID는 MVP에서 사용하지 않는다.

---

# 115. Foreign Key Strategy

외래키를 사용하여 데이터 무결성을 유지한다.

예시

```text
stories.user_id

chapters.story_id

choices.chapter_id

bookmarks.user_id

bookmarks.story_id

reports.user_id

reports.story_id
```

삭제 정책은 서비스 요구사항에 따라 설정한다.

---

# 116. Index Strategy

조회 성능을 위해 필요한 컬럼에 인덱스를 적용한다.

권장 인덱스

| 컬럼 | 목적 |
|------|------|
| users.email | 로그인 |
| stories.user_id | 내 Story 조회 |
| chapters.story_id | Chapter 조회 |
| choices.chapter_id | 선택지 조회 |
| bookmarks.user_id | 북마크 조회 |
| reports.story_id | 신고 조회 |

불필요한 인덱스는 추가하지 않는다.

---

# 117. Unique Constraints

중복 저장을 방지하기 위해 UNIQUE 제약조건을 적용한다.

예시

```text
users.email

bookmarks(user_id, story_id)
```

필요한 경우 신고에도 복합 UNIQUE를 적용할 수 있다.

---

# 118. Cascade Strategy

Story 삭제 시 관련 데이터도 함께 삭제한다.

```text
Story 삭제

↓

Chapter 삭제

↓

Choice 삭제
```

Bookmark와 Report도 함께 삭제한다.

Cascade는 필요한 관계에만 적용한다.

---

# 119. Naming Convention

테이블

```text
users

stories

chapters

choices

bookmarks

reports
```

컬럼

```text
created_at

updated_at

user_id

story_id

chapter_id
```

snake_case를 사용한다.

---

# 120. Audit Columns

모든 주요 테이블에는 생성일과 수정일을 저장한다.

```text
created_at

updated_at
```

Spring Data JPA Auditing을 사용하여 자동 관리한다.

---

# 121. Database Design Principles

StorySeed 데이터베이스는 다음 원칙을 따른다.

1. 정규화를 기본으로 설계한다.
2. 모든 관계는 외래키를 사용한다.
3. 필요한 곳에만 인덱스를 추가한다.
4. 중복 데이터 저장을 최소화한다.
5. Long Primary Key를 사용한다.
6. Audit 컬럼을 유지한다.
7. MVP에서는 단순한 구조를 우선한다.

---

# 122. MVP Scope

MVP에서 구현하는 테이블

- users
- stories
- chapters
- choices
- bookmarks
- reports

MVP에서는 다음 테이블을 제외한다.

- prompt_templates
- ai_logs
- inventories
- character_states
- story_flags
- user_settings
- refresh_tokens

---

# 123. Future Expansion

서비스 확장 시 다음 테이블을 추가할 수 있다.

- prompt_templates
- ai_generation_logs
- story_flags
- character_states
- inventories
- achievements
- notifications
- user_settings

현재는 구현하지 않는다.

---

# 124. Next Section

다음 장에서는 ERD(Entity Relationship Diagram)를 정의한다.

각 테이블의 관계와 컬럼 구성을 시각적으로 정리한다.
```



