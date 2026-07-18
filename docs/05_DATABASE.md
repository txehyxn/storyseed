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

# 11. users

## 11.1 Overview

users 테이블은 StorySeed의 회원 정보를 저장하는 핵심 테이블이다.

사용자의 인증, 권한, 로그인 방식, 계정 상태를 관리하며 모든 Story의 최상위 부모(Entity)가 된다.

---

## 11.2 Responsibilities

users 테이블은 다음 정보를 관리한다.

- 회원가입
- 로그인
- OAuth 계정 연동
- 권한 관리
- 계정 상태 관리
- 마지막 로그인 시간
- Story 소유자 관리

---

## 11.3 Table

```
users
```

---

## 11.4 Columns

| Column | Type | Null | Key | Default | Description |
|---------|------|------|-----|----------|-------------|
| id | BIGINT | NO | PK | AUTO_INCREMENT | 회원 PK |
| email | VARCHAR(255) | NO | UK | - | 로그인 이메일 |
| password | VARCHAR(255) | YES | - | NULL | BCrypt 암호화 비밀번호 |
| nickname | VARCHAR(50) | NO | UK | - | 닉네임 |
| provider | ENUM | NO | - | LOCAL | 로그인 제공자 |
| provider_id | VARCHAR(255) | YES | - | NULL | OAuth Provider 사용자 ID |
| role | ENUM | NO | - | USER | 사용자 권한 |
| status | ENUM | NO | - | ACTIVE | 계정 상태 |
| last_login_at | DATETIME | YES | - | NULL | 마지막 로그인 |
| created_at | DATETIME | NO | - | CURRENT_TIMESTAMP | 생성일 |
| updated_at | DATETIME | NO | - | CURRENT_TIMESTAMP | 수정일 |

---

## 11.5 Enum Definition

### Provider

```
LOCAL

GOOGLE

KAKAO

NAVER
```

---

### Role

```
USER

ADMIN

SUPER_ADMIN
```

---

### Status

```
ACTIVE

SUSPENDED

WITHDRAWN
```

---

## 11.6 Column Description

### id

모든 회원의 고유 식별자이다.

절대 변경되지 않는다.

---

### email

로그인에 사용하는 이메일이다.

동일한 이메일은 존재할 수 없다.

Unique Index를 생성한다.

---

### password

BCrypt 해시값을 저장한다.

OAuth 로그인 사용자는 NULL을 허용한다.

---

### nickname

서비스 내 표시되는 이름이다.

중복을 허용하지 않는다.

---

### provider

로그인 방식을 저장한다.

예시

```
LOCAL

GOOGLE

KAKAO

NAVER
```

---

### provider_id

OAuth 로그인 사용자의 고유 식별자이다.

LOCAL 회원은 NULL이다.

---

### role

서비스 권한을 정의한다.

예시

```
USER

ADMIN

SUPER_ADMIN
```

---

### status

회원 상태를 저장한다.

ACTIVE

정상 회원

---

SUSPENDED

관리자 정지

---

WITHDRAWN

탈퇴 처리

---

### last_login_at

가장 최근 로그인 시간을 저장한다.

로그인 성공 시 갱신한다.

---

## 11.7 Constraints

### Primary Key

```
PK_users

(id)
```

---

### Unique Key

```
UK_users_email

(email)
```

```
UK_users_nickname

(nickname)
```

---

### Check Constraints

provider는 Provider Enum만 허용한다.

role은 Role Enum만 허용한다.

status는 Status Enum만 허용한다.

---

## 11.8 Index Strategy

| Index | Purpose |
|---------|----------|
| idx_users_email | 로그인 |
| idx_users_nickname | 닉네임 검색 |
| idx_users_status | 활성 사용자 조회 |
| idx_users_provider | OAuth 조회 |

---

## 11.9 Relationship

User

↓

Story

```
1 : N
```

---

User

↓

Bookmark

```
1 : N
```

---

User

↓

RefreshToken

```
1 : N
```

---

User

↓

UserSetting

```
1 : 1
```

---

## 11.10 Business Rules

### Rule 1

이메일은 반드시 고유해야 한다.

---

### Rule 2

닉네임은 반드시 고유해야 한다.

---

### Rule 3

LOCAL 로그인은 password가 반드시 존재해야 한다.

---

### Rule 4

OAuth 로그인은 provider_id가 반드시 존재해야 한다.

---

### Rule 5

WITHDRAWN 계정은 로그인할 수 없다.

---

### Rule 6

SUSPENDED 계정은 관리자 해제 전까지 로그인할 수 없다.

---

## 11.11 JPA Mapping

```java
@OneToMany(
    mappedBy = "user",
    cascade = CascadeType.NONE,
    orphanRemoval = false
)
private List<Story> stories;
```

---

```java
@OneToMany(
    mappedBy = "user"
)
private List<Bookmark> bookmarks;
```

---

```java
@OneToOne(
    mappedBy = "user"
)
private UserSetting userSetting;
```

---

## 11.12 Future Expansion

향후 다음 기능을 추가할 수 있도록 설계한다.

- 이메일 인증
- 비밀번호 재설정
- 프로필 이미지
- 자기소개
- 국가
- 언어 설정
- AI 사용량 통계
- 유료 구독 여부

추가 컬럼은 별도 Migration을 통해 관리한다.

---

## 11.13 Entity Summary

| Item | Value |
|------|------|
| Table | users |
| PK | id |
| FK | 없음 |
| Unique | email, nickname |
| Parent | 없음 |
| Child | stories, bookmarks, refresh_tokens, user_settings |
| Soft Delete | 미사용 |
| Audit | BaseEntity |

---

# 12. Next Section

다음 장에서는 StorySeed의 핵심 테이블인 **stories**를 정의한다.

stories는 프로젝트에서 가장 중요한 Entity이며,

- AI 생성
- 챕터 진행
- 캐릭터
- 장르
- 세계관
- 공개 여부
- 진행 상태

를 모두 관리한다.

# 13. stories

## 13.1 Overview

stories 테이블은 StorySeed의 핵심 엔티티이다.

모든 이야기의 메타데이터를 저장하며, AI 생성 과정의 시작점이 된다.

Story 하나는 하나의 사용자가 생성하며, 여러 개의 Chapter를 가진다.

Story는 다음 정보를 관리한다.

- 이야기 제목
- 이야기 설명
- 장르
- 세계관
- 공개 여부
- 현재 진행 상태
- 이야기 길이
- AI 생성 방식
- 대상 연령
- 언어
- 현재 챕터

---

## 13.2 Responsibilities

stories 테이블은 다음 기능을 담당한다.

- 이야기 생성
- 이어쓰기
- AI 생성 시작
- 챕터 연결
- 캐릭터 연결
- 리포트 생성
- 북마크 대상
- 공개 여부 관리
- 진행 상태 관리

---

## 13.3 Table

```
stories
```

---

## 13.4 Columns

| Column | Type | Null | Key | Description |
|---------|------|------|-----|-------------|
| id | BIGINT | NO | PK | 이야기 PK |
| user_id | BIGINT | NO | FK | 작성자 |
| genre_id | BIGINT | NO | FK | 장르 |
| world_id | BIGINT | NO | FK | 세계관 |
| title | VARCHAR(200) | NO | | 제목 |
| description | TEXT | YES | | 이야기 소개 |
| story_type | ENUM | NO | | 이야기 종류 |
| visibility | ENUM | NO | | 공개 여부 |
| story_length | ENUM | NO | | 이야기 길이 |
| age_group | ENUM | NO | | 대상 연령 |
| language | VARCHAR(10) | NO | | 언어 |
| cover_image_url | VARCHAR(500) | YES | | 표지 이미지 |
| current_chapter | INT | NO | | 현재 챕터 |
| status | ENUM | NO | | 진행 상태 |
| completed_at | DATETIME | YES | | 종료일 |
| created_at | DATETIME | NO | | 생성일 |
| updated_at | DATETIME | NO | | 수정일 |

---

## 13.5 Enum Definition

### StoryType

```
ORIGINAL

FAIRYTALE

SELF_INSERT
```

설명

ORIGINAL

사용자가 직접 만드는 이야기

---

FAIRYTALE

기존 동화를 새롭게 각색

---

SELF_INSERT

사용자가 주인공인 이야기

---

### Visibility

```
PUBLIC

PRIVATE

UNLISTED
```

PUBLIC

검색 가능

---

PRIVATE

작성자만 조회

---

UNLISTED

링크가 있는 사람만 조회 가능

---

### StoryLength

```
SHORT

MEDIUM

LONG
```

SHORT

약 5 Chapter

---

MEDIUM

약 10 Chapter

---

LONG

20 Chapter 이상

---

### StoryStatus

```
DRAFT

IN_PROGRESS

COMPLETED

ARCHIVED
```

---

### AgeGroup

```
CHILD

TEEN

ADULT
```

---

## 13.6 Column Description

### title

이야기의 제목이다.

최대 200자를 허용한다.

AI가 자동 생성하거나 사용자가 직접 입력할 수 있다.

---

### description

이야기의 간단한 소개이다.

검색 및 목록 화면에서 사용한다.

---

### genre_id

장르 FK이다.

Fantasy

Mystery

Romance

Sci-Fi

Adventure

등을 참조한다.

---

### world_id

세계관 FK이다.

현대

중세

우주

학교

무협

등을 참조한다.

---

### story_type

스토리 생성 방식을 정의한다.

서비스의 핵심 기능을 구분하는 컬럼이다.

---

### visibility

공개 범위를 정의한다.

목록 조회 시 반드시 권한 검사를 수행한다.

---

### story_length

AI가 생성할 최대 Chapter 수를 결정하는 기준이다.

---

### age_group

AI Prompt 생성 시 안전성 기준으로 사용한다.

예시

CHILD

폭력성 최소화

---

TEEN

약한 긴장감 허용

---

ADULT

보다 자유로운 전개 허용

---

### language

이야기 생성 언어

예시

```
ko

en

ja
```

향후 다국어 서비스를 지원한다.

---

### cover_image_url

표지 이미지 주소

MVP에서는 Nullable

향후 AI 이미지 생성 기능과 연결된다.

---

### current_chapter

현재 진행 중인 Chapter 번호

Chapter 생성 시 자동 증가한다.

---

### status

현재 이야기 상태

Draft

↓

In Progress

↓

Completed

↓

Archived

순으로 변경된다.

---

### completed_at

이야기 종료 시간

종료 전에는 NULL

---

## 13.7 Constraints

Primary Key

```
PK_stories
```

Foreign Key

```
user_id

↓

users.id
```

```
genre_id

↓

genres.id
```

```
world_id

↓

worlds.id
```

---

## 13.8 Index Strategy

| Index | Purpose |
|---------|----------|
| idx_story_user | 사용자 이야기 조회 |
| idx_story_status | 진행 상태 조회 |
| idx_story_genre | 장르 검색 |
| idx_story_world | 세계관 검색 |
| idx_story_created | 최신순 조회 |
| idx_story_visibility | 공개 이야기 조회 |

---

## 13.9 Relationship

Story

↓

Character

```
1 : 1
```

---

Story

↓

Chapter

```
1 : N
```

---

Story

↓

StorySummary

```
1 : N
```

---

Story

↓

Report

```
1 : 1
```

---

Story

↓

Bookmark

```
1 : N
```

---

Story

↓

AIGenerationLog

```
1 : N
```

---

## 13.10 Business Rules

### Rule 1

Story 생성 시 Character가 함께 생성된다.

---

### Rule 2

Story 삭제 시

- Chapter
- Character
- Summary
- Report
- AI Log

모두 삭제된다.

---

### Rule 3

Completed Story는 수정할 수 없다.

---

### Rule 4

Private Story는 작성자만 조회 가능하다.

---

### Rule 5

Archived Story는 AI 이어쓰기를 수행할 수 없다.

---

## 13.11 Cascade Policy

| Parent | Child | Cascade |
|----------|----------|----------|
| Story | Chapter | ALL |
| Story | Character | ALL |
| Story | StorySummary | ALL |
| Story | Report | ALL |
| Story | AIGenerationLog | ALL |
| Story | Bookmark | REMOVE |

---

## 13.12 Fetch Strategy

기본 정책

```
LAZY
```

Story 조회 시

Character

Chapter

Report

Summary

를 즉시 가져오지 않는다.

필요 시

Fetch Join

또는

EntityGraph

를 사용한다.

---

## 13.13 Future Expansion

stories는 다음 기능을 고려하여 설계한다.

- 좋아요
- 조회수
- 댓글
- 협업 작성
- 번역본
- AI 표지 이미지
- 음성 생성
- 애니메이션 생성
- PDF 출판

---

## 13.14 Entity Summary

| Item | Value |
|------|------|
| Table | stories |
| PK | id |
| FK | user_id, genre_id, world_id |
| Parent | users |
| Child | chapters, characters, reports, summaries, bookmarks, ai_generation_logs |
| Soft Delete | 미사용 |
| Audit | BaseEntity |

---

# 14. Next Section

다음 장에서는 Character Entity를 정의한다.

Character는 StorySeed에서 AI가 일관성 있는 이야기를 생성하기 위한 가장 중요한 데이터 중 하나이며,

- 외형
- 성격
- 말투
- 목표
- 능력
- 배경

등을 관리한다.
