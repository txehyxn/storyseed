# StorySeed Database Design

> Version : 1.0
>
> Project : StorySeed
>
> Database : MySQL 8
>
> ORM : Spring Data JPA
>
> Language : Java 25
>
> Last Update : 2026

---

# 1. Document Overview

## 1.1 Purpose

본 문서는 StorySeed 프로젝트에서 사용하는 데이터베이스 구조를 정의한다.

StorySeed는 사용자의 선택을 기반으로 AI가 이야기를 생성하는 인터랙티브 스토리 플랫폼이다.

일반적인 게시판 프로젝트와 달리,

- AI 생성 결과
- 사용자 선택
- 이야기 진행 상태
- 캐릭터 정보
- 프롬프트 버전
- AI 호출 기록

등 다양한 데이터를 장기간 관리해야 한다.

따라서 확장성과 유지보수성을 고려한 데이터베이스 구조를 설계한다.

---

## 1.2 Scope

본 문서에서 정의하는 범위는 다음과 같다.

- Database Design
- Entity Structure
- Table Definition
- Relationship
- Primary Key
- Foreign Key
- Index Strategy
- Cascade Policy
- Naming Convention
- JPA Mapping Strategy

---

## 1.3 Target Architecture

StorySeed는 다음 구조를 기준으로 개발한다.

```
Client

↓

Spring Boot

↓

Service

↓

JPA

↓

MySQL
```

AI 생성은 별도의 AI Service Layer를 통해 수행한다.

```
Controller

↓

Service

↓

AI Service

↓

OpenAI / Claude / Gemini

↓

Database
```

---

# 2. Database Design Principles

## 2.1 Design Goals

StorySeed Database는 다음 목표를 가진다.

- 높은 확장성
- 높은 가독성
- 데이터 무결성 유지
- AI 기능 확장 가능
- 장편 스토리 지원
- 유지보수 용이성

---

## 2.2 Normalization

모든 테이블은 기본적으로 제3정규형(3NF)을 유지한다.

중복 데이터를 최소화하고 데이터 무결성을 유지한다.

예외적으로 성능 향상이 필요한 경우에만 비정규화를 적용한다.

---

## 2.3 Primary Key

모든 테이블은 BIGINT Primary Key를 사용한다.

```sql
id BIGINT AUTO_INCREMENT PRIMARY KEY
```

UUID는 현재 사용하지 않는다.

향후 분산 시스템으로 확장할 경우 UUID 적용을 검토한다.

---

## 2.4 Foreign Key

모든 연관관계는 Foreign Key를 사용한다.

예외

- Enum 값
- 시스템 설정값

---

## 2.5 Audit Columns

모든 테이블은 생성일과 수정일을 가진다.

```text
created_at

updated_at
```

모든 Entity는 BaseEntity를 상속한다.

---

## 2.6 Soft Delete

현재 프로젝트에서는 Soft Delete를 사용하지 않는다.

삭제 요청 시 실제 데이터를 삭제한다.

향후 운영 정책 변경 시 archived_at 컬럼을 이용한 Archive 전략으로 확장 가능하도록 설계한다.

---

## 2.7 Character Encoding

모든 테이블은 다음 설정을 사용한다.

```
UTF8MB4
```

Collation

```
utf8mb4_unicode_ci
```

이를 통해

- Emoji
- 다국어
- 특수문자

저장을 지원한다.

---

# 3. Naming Convention

## 3.1 Table Name

모든 테이블은

- snake_case
- 복수형

을 사용한다.

예시

```
users

stories

story_summaries

prompt_templates

ai_generation_logs
```

---

## 3.2 Column Name

모든 컬럼은 snake_case를 사용한다.

예시

```
created_at

updated_at

cover_image_url

story_type
```

---

## 3.3 Foreign Key

Foreign Key는

```
참조테이블_id
```

형식을 사용한다.

예시

```
user_id

story_id

genre_id

world_id
```

---

## 3.4 Index

모든 인덱스는

```
idx_

uk_
```

접두사를 사용한다.

예시

```
idx_story_user

idx_story_status

uk_users_email

uk_users_nickname
```

---

# 4. BaseEntity

모든 Entity는 BaseEntity를 상속한다.

공통 컬럼

| Column | Type | Description |
|---------|------|-------------|
| created_at | DATETIME | 생성일 |
| updated_at | DATETIME | 수정일 |

예시

```java
@MappedSuperclass
@Getter
public abstract class BaseEntity {

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
```

BaseEntity를 사용함으로써

- 코드 중복 제거
- 유지보수 향상
- Audit 자동 관리

가 가능하다.

---

# 5. Entity Overview

StorySeed는 다음 Entity를 사용한다.

## Core

```
users

stories

chapters

characters

choices

reports
```

---

## Metadata

```
genres

worlds
```

---

## AI

```
prompt_templates

story_summaries

ai_generation_logs
```

---

## User

```
bookmarks

refresh_tokens

user_settings
```

---

# 6. Database ERD (Logical)

```
User
 │
 ├──────────────┐
 │              │
 ▼              ▼
Story      UserSetting
 │
 ├──────────────┐
 │              │
 ▼              ▼
Character   StorySummary
 │
 ▼
Chapter
 │
 ▼
Choice

Story
 │
 ▼
Report

Story
 │
 ├────────► Genre
 │
 └────────► World

Story
 │
 ▼
AIGenerationLog

Story
 │
 ▼
Bookmark

PromptTemplate
```

---

# 7. Entity Dependency

```
User

↓

Story

↓

Chapter

↓

Choice
```

Character는 Story 생성 시 함께 생성된다.

Report는 Story 종료 시 생성된다.

Summary는 일정 챕터마다 생성된다.

AI Generation Log는 AI 호출 시마다 생성된다.

---

# 8. Database Version Policy

Database 변경은 반드시 Migration을 통해 관리한다.

권장 도구

- Flyway
- Liquibase

직접 SQL 수정은 지양한다.

---

# 9. Database Package Structure

```
domain
 ├── user
 ├── story
 ├── chapter
 ├── choice
 ├── report
 ├── ai
 ├── common
 └── config
```

Entity는 기능별 패키지로 분리한다.

---

# 10. Next Section

다음 장에서는 User Entity를 상세하게 정의한다.

- users
- Constraints
- Index
- Column Definition
- JPA Mapping
- Business Rules
- Future Expansion

---
