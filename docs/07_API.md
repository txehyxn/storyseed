# StorySeed API Design

> Version : 1.0
>
> Project : StorySeed
>
> Last Update : 2026

---

# 1. Overview

## 1.1 Purpose

본 문서는 StorySeed에서 사용하는 REST API의 설계 기준과 명세를 정의한다.

모든 API는 RESTful 원칙을 기반으로 설계하며, 프론트엔드와 백엔드 간의 일관된 데이터 통신을 목표로 한다.

---

## 1.2 Goals

StorySeed API의 목표는 다음과 같다.

- RESTful API 설계
- 일관된 Request / Response 구조
- 명확한 HTTP Status Code 사용
- 확장 가능한 URL 구조
- 유지보수가 쉬운 API 설계

---

## 1.3 API Architecture

```text
Client

↓

Spring Controller

↓

Service

↓

Repository

↓

Database

↓

Response DTO

↓

Client
```

Controller는 요청을 처리하고,

Service는 비즈니스 로직을 수행하며,

Repository는 데이터베이스와 통신한다.

Entity는 외부에 직접 노출하지 않는다.

---

# 2. REST API Principles

StorySeed는 REST API 설계 원칙을 따른다.

## 2.1 Resource 중심 설계

API는 기능(Function)이 아니라 Resource를 기준으로 설계한다.

예시

```text
/users

/stories

/chapters

/bookmarks
```

---

## 2.2 HTTP Method 사용

각 요청은 목적에 맞는 HTTP Method를 사용한다.

| Method | Description |
|---------|-------------|
| GET | 조회 |
| POST | 생성 |
| PUT | 전체 수정 |
| PATCH | 부분 수정 |
| DELETE | 삭제 |

---

## 2.3 Stateless

모든 요청은 독립적으로 처리된다.

필요한 인증 정보는 Session을 통해 확인하며,

각 요청은 이전 요청에 의존하지 않는다.

---

## 2.4 JSON 기반 통신

모든 Request와 Response는 JSON 형식을 사용한다.

---

## 2.5 DTO 사용

Entity는 외부에 직접 노출하지 않는다.

모든 API는 DTO를 사용한다.

```text
Controller

↓

Request DTO

↓

Service

↓

Entity

↓

Response DTO

↓

Client
```

---

# 3. Base URL

모든 API는 다음 URL을 기준으로 한다.

```text
/api
```

예시

```text
/api/auth/login

/api/stories

/api/bookmarks
```

---

# 4. URL Naming Convention

URL은 복수형 Resource를 사용한다.

예시

```text
/stories

/users

/bookmarks

/reports
```

동사를 URL에 포함하지 않는다.

올바른 예시

```text
POST /stories

DELETE /stories/{storyId}
```

잘못된 예시

```text
/createStory

/deleteStory
```

---

# 5. HTTP Status Code

StorySeed에서 사용하는 Status Code는 다음과 같다.

| Code | Description |
|------|-------------|
| 200 | OK |
| 201 | Created |
| 204 | No Content |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 409 | Conflict |
| 500 | Internal Server Error |

---

# 6. Common Response Format

모든 API는 동일한 응답 구조를 사용한다.

성공 응답

```json
{
  "success": true,
  "data": {
  },
  "message": "요청이 성공했습니다."
}
```

실패 응답

```json
{
  "success": false,
  "error": {
    "code": "STORY_NOT_FOUND",
    "message": "Story를 찾을 수 없습니다."
  }
}
```

---

# 7. Authentication

현재 MVP에서는 Session 기반 인증을 사용한다.

로그인 이후 Session을 통해 사용자를 식별한다.

JWT와 OAuth2는 Future Expansion으로 분리한다.

---

# 8. API Categories

StorySeed API는 다음과 같이 구성된다.

- Authentication API
- User API
- Story API
- Chapter API
- Choice API
- Bookmark API
- Report API

이후 각 API를 순서대로 정의한다.
