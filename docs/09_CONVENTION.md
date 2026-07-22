# Development Convention

> Version : 1.0
>
> Project : StorySeed
>
> Last Update : 2026

---

# 1. Overview

본 문서는 StorySeed 프로젝트의 개발 규칙을 정의한다.

모든 개발은 본 문서의 규칙을 기준으로 진행하며, 일관성 있는 코드 작성과 유지보수를 목표로 한다.

---

# 2. Project Structure

프로젝트는 계층형 구조(Layered Architecture)를 따른다.

```text
controller
service
repository
domain
dto
config
security
common
```

각 패키지는 하나의 역할만 담당한다.

---

# 3. Package Rules

## Controller

- HTTP 요청 처리
- Request 검증
- Service 호출
- View 또는 Response 반환

비즈니스 로직을 작성하지 않는다.

---

## Service

- 비즈니스 로직 처리
- Repository 호출
- 필요한 데이터 가공

DB 접근 코드를 직접 작성하지 않는다.

---

## Repository

- 데이터 조회 및 저장
- Spring Data JPA 사용

비즈니스 로직을 작성하지 않는다.

---

## Domain

- Entity 클래스 작성
- JPA 매핑
- 데이터 표현

---

## DTO

- Request DTO
- Response DTO

Entity를 외부에 직접 반환하지 않는다.

---

# 4. Naming Convention

## Class

PascalCase를 사용한다.

예시

```text
StoryService
UserController
BookmarkRepository
```

---

## Method

camelCase를 사용한다.

예시

```text
createStory()
findStory()
deleteStory()
```

---

## Variable

camelCase를 사용한다.

예시

```text
storyTitle
createdAt
userId
```

---

## Package

모두 소문자를 사용한다.

예시

```text
controller
service
repository
```

---

# 5. Git Convention

커밋 메시지는 다음 형식을 사용한다.

```text
type: 내용
```

예시

```text
feat: Story 생성 기능 추가
fix: 로그인 오류 수정
docs: README 수정
refactor: StoryService 리팩토링
style: 코드 포맷 수정
test: 테스트 코드 추가
```

---

# 6. Code Style

- 메서드는 하나의 역할만 수행한다.
- 중복 코드를 최소화한다.
- 의미 있는 변수명을 사용한다.
- 매직 넘버 대신 상수를 사용한다.
- 필요한 경우에만 주석을 작성한다.

---

# 7. Exception Handling

예외는 Controller에서 처리하지 않는다.

공통 예외 처리 클래스를 통해 처리한다.

```text
@ControllerAdvice
```

를 사용하여 예외를 관리한다.

---

# 8. Security Rules

- Session 기반 인증 사용
- 비밀번호는 BCrypt로 암호화
- 인증이 필요한 기능은 로그인 후 접근 가능

---

# 9. Documentation Rules

기능 구현 후 다음 문서를 함께 업데이트한다.

- README
- API
- TROUBLESHOOTING

필요한 경우 ERD와 DATABASE 문서도 함께 수정한다.

---

# 10. MVP Development Rules

MVP에서는 필요한 기능만 구현한다.

새로운 기능은 실제 필요성이 확인된 이후 추가한다.

과도한 설계보다 동작하는 기능 구현을 우선한다.
