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

# 15. characters

## 15.1 Overview

characters 테이블은 이야기의 주인공 정보를 저장한다.

StorySeed에서 Character는 단순한 이름 저장용 테이블이 아니다.

AI가 여러 Chapter를 생성하더라도 캐릭터의 성격, 말투, 외형, 목표 등을 일관성 있게 유지하기 위한 핵심 데이터이다.

Story 하나는 Character 하나를 가진다.

향후 다중 주인공 기능이 추가될 경우 CharacterMember 테이블을 추가하여 확장할 수 있도록 설계한다.

---

## 15.2 Responsibilities

Character는 다음 정보를 관리한다.

- 이름
- 성별
- 나이
- 직업
- 외형
- 성격
- 말투
- 능력
- 목표
- 배경 설정
- 좋아하는 것
- 싫어하는 것
- 두려움

---

## 15.3 Table

```
characters
```

---

## 15.4 Columns

| Column | Type | Null | Key | Description |
|---------|------|------|-----|-------------|
| id | BIGINT | NO | PK | 캐릭터 PK |
| story_id | BIGINT | NO | FK | Story FK |
| name | VARCHAR(100) | NO | | 이름 |
| gender | ENUM | YES | | 성별 |
| age | VARCHAR(30) | YES | | 나이 |
| occupation | VARCHAR(100) | YES | | 직업 |
| appearance | TEXT | YES | | 외형 |
| personality | TEXT | YES | | 성격 |
| speaking_style | TEXT | YES | | 말투 |
| background | TEXT | YES | | 성장 배경 |
| ability | TEXT | YES | | 능력 |
| goal | TEXT | YES | | 목표 |
| favorite | TEXT | YES | | 좋아하는 것 |
| dislike | TEXT | YES | | 싫어하는 것 |
| fear | TEXT | YES | | 두려움 |
| created_at | DATETIME | NO | | 생성일 |
| updated_at | DATETIME | NO | | 수정일 |

---

## 15.5 Gender Enum

```
MALE

FEMALE

OTHER

UNKNOWN
```

---

## 15.6 Column Description

### story_id

Character가 속한 Story

Story 하나당 Character 하나만 존재한다.

Unique Key를 생성한다.

---

### name

캐릭터 이름

AI 생성 또는 사용자 입력

---

### gender

캐릭터 성별

Prompt 생성 시 사용된다.

---

### age

예시

```
17세

25세

30대

노인
```

숫자가 아닌 이유는

동화에서는

```
어린 왕자

수백 살의 용

천년 묵은 마법사
```

같은 표현도 가능하기 때문이다.

---

### occupation

예시

```
학생

기사

탐험가

마법사

요리사
```

---

### appearance

외형 설명

예시

```
검은 머리

파란 눈

안경

교복

큰 키
```

---

### personality

AI가 가장 많이 참고하는 컬럼이다.

예시

```
소심함

용감함

낙천적

냉정함

호기심이 많음
```

---

### speaking_style

예시

```
존댓말

반말

사투리

귀여운 말투

차가운 말투
```

---

### background

캐릭터 성장 배경

예시

```
고아

왕족

시골 출신

군인 집안
```

---

### ability

특별한 능력

예시

```
마법

검술

추리

요리
```

---

### goal

이야기의 핵심 목표

예시

```
집으로 돌아가기

왕이 되기

친구 구하기
```

---

### favorite

좋아하는 것

AI가 대사를 생성할 때 활용한다.

---

### dislike

싫어하는 것

---

### fear

두려워하는 것

긴장감 있는 전개에서 활용된다.

---

## 15.7 Constraints

Primary Key

```
PK_characters
```

Foreign Key

```
story_id

↓

stories.id
```

Unique

```
UK_character_story

story_id
```

Story 하나당 Character 하나만 존재한다.

---

## 15.8 Index Strategy

| Index | Purpose |
|---------|----------|
| idx_character_story | Story 조회 |
| idx_character_name | 이름 검색 |

---

## 15.9 Relationship

Story

↓

Character

```
1 : 1
```

---

## 15.10 Business Rules

### Rule 1

Story 생성 시 Character가 자동 생성된다.

---

### Rule 2

Character 삭제는 직접 수행하지 않는다.

Story 삭제 시 함께 삭제된다.

---

### Rule 3

Character 수정 시 이후 생성되는 모든 Chapter에 반영된다.

---

### Rule 4

Character 이름 변경은 기존 Chapter 내용을 수정하지 않는다.

새로운 Chapter부터 적용된다.

---

## 15.11 JPA Mapping

Story

```java
@OneToOne(
    mappedBy = "story",
    cascade = CascadeType.ALL,
    orphanRemoval = true,
    fetch = FetchType.LAZY
)
private Character character;
```

Character

```java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "story_id")
private Story story;
```

---

## 15.12 Future Expansion

향후 추가 예정

- 캐릭터 이미지
- AI 캐릭터 음성
- MBTI
- 캐릭터 관계도
- 감정 변화 기록
- 캐릭터 성장 시스템

---

## 15.13 Entity Summary

| Item | Value |
|------|------|
| Table | characters |
| PK | id |
| FK | story_id |
| Parent | stories |
| Child | 없음 |
| Cascade | ALL |
| Audit | BaseEntity |

---

# 16. Next Section

다음 장에서는 Chapter Entity를 정의한다.

Chapter는 StorySeed의 핵심 데이터이며,

- AI가 생성한 본문
- 사용자 선택지
- 생성 순서
- AI Context
- StorySummary 생성 기준

까지 관리한다.

Chapter 설계는 이후 AI Prompt 설계와 직접 연결된다.

---

# 17. chapters

## 17.1 Overview

chapters 테이블은 StorySeed에서 생성되는 실제 이야기 본문을 저장한다.

Story 하나는 여러 개의 Chapter를 가진다.

각 Chapter는 AI가 생성한 결과이며,
사용자의 선택에 따라 다음 Chapter가 생성된다.

Chapter는 단순한 본문 저장 테이블이 아니라,

- 이야기 진행
- AI 생성 결과
- Context 관리
- 토큰 관리
- 이어쓰기

를 담당하는 핵심 엔티티이다.

---

## 17.2 Responsibilities

Chapter는 다음 정보를 관리한다.

- Chapter 번호
- 제목
- 본문
- AI 생성 결과
- 생성 순서
- 이전 Chapter 연결
- 다음 Chapter 연결
- 생성 토큰
- Prompt Version
- Story Summary 생성 기준

---

## 17.3 Table

```
chapters
```

---

## 17.4 Columns

| Column | Type | Null | Key | Description |
|---------|------|------|-----|-------------|
| id | BIGINT | NO | PK | 챕터 PK |
| story_id | BIGINT | NO | FK | Story |
| chapter_number | INT | NO | | 챕터 번호 |
| title | VARCHAR(200) | NO | | 챕터 제목 |
| content | LONGTEXT | NO | | AI 생성 본문 |
| previous_chapter_id | BIGINT | YES | FK | 이전 Chapter |
| next_chapter_id | BIGINT | YES | FK | 다음 Chapter |
| prompt_template_id | BIGINT | YES | FK | Prompt Template |
| prompt_version | VARCHAR(20) | YES | | Prompt 버전 |
| ai_model | VARCHAR(100) | YES | | GPT / Claude |
| prompt_tokens | INT | YES | | Prompt Token |
| completion_tokens | INT | YES | | Completion Token |
| total_tokens | INT | YES | | 전체 Token |
| generation_time_ms | INT | YES | | 생성 시간(ms) |
| regenerated | BOOLEAN | NO | | 재생성 여부 |
| created_at | DATETIME | NO | | 생성일 |
| updated_at | DATETIME | NO | | 수정일 |

---

## 17.5 Column Description

### story_id

Story FK

하나의 Story는 여러 Chapter를 가진다.

---

### chapter_number

이야기 진행 순서

예시

```
1

2

3

4

...
```

Story 내부에서 중복될 수 없다.

---

### title

Chapter 제목

예시

```
숲속의 만남

첫 번째 시험

배신

최후의 결전
```

---

### content

AI가 생성한 이야기 본문

LONGTEXT를 사용한다.

장편 소설도 저장 가능해야 한다.

---

### previous_chapter_id

이전 Chapter

첫 번째 Chapter는 NULL이다.

---

### next_chapter_id

다음 Chapter

마지막 Chapter는 NULL이다.

---

### prompt_template_id

사용된 Prompt Template

Prompt 버전 관리에 사용된다.

---

### prompt_version

Prompt Template Version

예시

```
v1.0

v1.1

v2.0
```

---

### ai_model

AI 모델

예시

```
GPT-5

Claude Sonnet

Gemini 3
```

---

### prompt_tokens

Prompt Token 사용량

---

### completion_tokens

AI 응답 Token

---

### total_tokens

Prompt

+

Completion

---

### generation_time_ms

AI 응답 시간

밀리초 단위

---

### regenerated

재생성 여부

true

AI 다시 생성

false

최초 생성

---

## 17.6 Constraints

Primary Key

```
PK_chapters
```

Foreign Key

```
story_id

↓

stories.id
```

```
prompt_template_id

↓

prompt_templates.id
```

---

Unique

```
(story_id, chapter_number)
```

Story 내부에서 Chapter 번호는 중복될 수 없다.

---

## 17.7 Index Strategy

| Index | Purpose |
|---------|----------|
| idx_chapter_story | Story 조회 |
| idx_chapter_number | 이어쓰기 |
| idx_chapter_created | 최신 Chapter |
| idx_chapter_prompt | Prompt 분석 |

---

## 17.8 Relationship

Story

↓

Chapter

```
1 : N
```

---

Chapter

↓

Choice

```
1 : N
```

---

Chapter

↓

AIGenerationLog

```
1 : N
```

---

## 17.9 Business Rules

### Rule 1

Chapter는 Story 없이 존재할 수 없다.

---

### Rule 2

Chapter 번호는 1부터 시작한다.

---

### Rule 3

Chapter 삭제는 허용하지 않는다.

Story 삭제 시 함께 삭제된다.

---

### Rule 4

Chapter 생성 후에는 본문 수정이 불가능하다.

재생성 기능을 사용한다.

---

### Rule 5

Prompt Version은 생성 이후 변경하지 않는다.

---

### Rule 6

Chapter 생성이 완료되면 Choice가 생성된다.

---

## 17.10 JPA Mapping

Story

```java
@OneToMany(
    mappedBy = "story",
    cascade = CascadeType.ALL,
    orphanRemoval = true
)
private List<Chapter> chapters;
```

---

Chapter

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "story_id")
private Story story;
```

---

## 17.11 AI Generation Flow

Story 생성

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

사용자 선택

↓

다음 Chapter 생성

---

## 17.12 Story Summary Policy

LONG Story에서는

모든 Chapter를 AI에게 보내지 않는다.

예시

```
1~10 Chapter

↓

Summary 생성

↓

11 Chapter 생성

↓

Summary

+

최근 2개 Chapter

↓

AI 호출
```

이 구조를 통해 Token 비용을 줄인다.

---

## 17.13 Future Expansion

향후 추가 예정

- Chapter 이미지
- 음성 생성
- 애니메이션 생성
- PDF Export
- 읽기 완료 여부
- 좋아요
- 댓글

---

## 17.14 Entity Summary

| Item | Value |
|------|------|
| Table | chapters |
| PK | id |
| FK | story_id |
| Parent | stories |
| Child | choices, ai_generation_logs |
| Cascade | ALL |
| Audit | BaseEntity |

---

# 18. Next Section

다음 장에서는 Choice Entity를 정의한다.

Choice는 StorySeed에서 가장 중요한 사용자 입력 데이터이다.

사용자가 선택한 내용에 따라 AI가 다음 Chapter를 생성한다.

Choice는 단순한 버튼이 아니라

- 선택지
- 선택 결과
- AI Prompt 연결
- 이야기 분기

를 담당하는 핵심 Entity이다.

---

# 19. choices

## 19.1 Overview

choices 테이블은 사용자가 선택할 수 있는 선택지를 저장한다.

Chapter 하나에는 여러 개의 Choice가 존재한다.

사용자는 그중 하나를 선택하며,

선택 결과는 ChoiceResult를 통해 AI에게 전달된다.

Choice 자체는 결과를 가지지 않는다.

Choice는

"무엇을 선택했는가"

만 저장한다.

실제 결과는 ChoiceResult가 담당한다.

---

## 19.2 Responsibilities

Choice는 다음 정보를 관리한다.

- 선택지 내용
- 표시 순서
- 선택 가능 여부
- 기본 선택 여부
- 선택 결과 연결

---

## 19.3 Table

```
choices
```

---

## 19.4 Columns

| Column | Type | Null | Key | Description |
|---------|------|------|-----|-------------|
| id | BIGINT | NO | PK | Choice PK |
| chapter_id | BIGINT | NO | FK | Chapter |
| choice_order | INT | NO | | 표시 순서 |
| content | VARCHAR(500) | NO | | 선택지 |
| is_default | BOOLEAN | NO | | 기본 선택 |
| is_enabled | BOOLEAN | NO | | 활성 여부 |
| created_at | DATETIME | NO | | 생성일 |
| updated_at | DATETIME | NO | | 수정일 |

---

## 19.5 Example

Chapter

```
용이 당신 앞에 나타났습니다.
```

Choice

```
① 검을 뽑는다.

② 도망친다.

③ 대화한다.
```

사용자는 하나만 선택한다.

---

## 19.6 Constraints

Primary Key

```
PK_choices
```

Foreign Key

```
chapter_id

↓

chapters.id
```

---

Unique

```
(chapter_id, choice_order)
```

---

## 19.7 Index

| Index | Purpose |
|---------|----------|
| idx_choice_chapter | Chapter 조회 |
| idx_choice_order | 정렬 |

---

## 19.8 Relationship

Chapter

↓

Choice

```
1:N
```

Choice

↓

ChoiceResult

```
1:1
```

---

## 19.9 Business Rules

Choice는 최소 2개 이상 생성한다.

권장 개수는

```
3~4개
```

이다.

Choice 삭제는 허용하지 않는다.

Story 삭제 시 함께 삭제된다.

---

# 20. choice_results

## 20.1 Why

StorySeed의 핵심 테이블이다.

일반 AI 소설 서비스에는 없는 기능이다.

ChoiceResult는

선택의 결과를

"텍스트"

가 아니라

"데이터"

로 저장한다.

AI는 이 데이터를 기반으로 다음 Chapter를 생성한다.

---

## 20.2 Responsibilities

ChoiceResult는

- 호감도 변화

- 능력치 변화

- 아이템 획득

- 플래그 저장

- 스토리 진행

을 관리한다.

---

## 20.3 Table

```
choice_results
```

---

## 20.4 Columns

| Column | Type | Null | Key | Description |
|---------|------|------|-----|-------------|
| id | BIGINT | NO | PK | PK |
| choice_id | BIGINT | NO | FK | Choice |
| summary | VARCHAR(500) | NO | | AI 전달용 요약 |
| affection_delta | INT | NO | | 호감도 |
| courage_delta | INT | NO | | 용기 |
| intelligence_delta | INT | NO | | 지능 |
| morality_delta | INT | NO | | 도덕성 |
| item_reward | VARCHAR(200) | YES | | 획득 아이템 |
| unlocked_flag | VARCHAR(100) | YES | | 플래그 |
| next_prompt_hint | TEXT | YES | | Prompt 힌트 |
| created_at | DATETIME | NO | | 생성일 |
| updated_at | DATETIME | NO | | 수정일 |

---

## 20.5 Example

Choice

```
용과 대화한다.
```

ChoiceResult

```
summary

용과 우호적인 관계를 맺었다.

affection_delta

+20

courage_delta

+5

item_reward

용의 비늘

flag

dragon_friend
```

AI는

```
dragon_friend
```

플래그를 참고하여

이후 Chapter에서

용이 도움을 주도록 생성할 수 있다.

---

## 20.6 Future Expansion

향후

다음 데이터를 추가할 수 있다.

```
- 관계 변화
- 감정 변화
- Story Item 획득
- StoryFlag 생성
- 다음 전개 힌트
```

---

## 20.7 AI Prompt Example

PromptBuilder는

ChoiceResult를

아래처럼 Prompt에 포함한다.

```
Player Choice

용과 대화했다.

Result

- 용과 친해짐

- 용의 비늘 획득

- 용기 +5

- dragon_friend 플래그 활성화
```

AI는

이를 기반으로

다음 Chapter를 생성한다.

---

## 20.8 Relationship

Choice

↓

ChoiceResult

```
1 : 1
```

---

## 20.9 Entity Summary

| Item | Value |
|------|------|
| Table | choice_results |
| PK | id |
| FK | choice_id |
| Parent | choices |
| Child | 없음 |
| Cascade | ALL |

---

# 21. Next Section

다음 장에서는 StorySummary와 PromptTemplate을 정의한다.

여기서부터는

AI 비용 절감,

Prompt 버전 관리,

장편 소설 지원 구조를 설계한다.

# 22. story_summaries

## 22.1 Overview

story_summaries 테이블은 장편 이야기에서 AI에게 전달할 Context를 압축하여 저장한다.

LLM은 모든 Chapter를 매번 입력으로 받으면 토큰 사용량이 급격히 증가한다.

StorySeed는 일정 개수의 Chapter가 생성될 때마다 StorySummary를 생성하여 이후 Prompt에 활용한다.

이 구조를 통해

- Token 비용 절감
- 응답 속도 향상
- 장편 소설 지원

을 가능하게 한다.

---

## 22.2 Responsibilities

StorySummary는 다음 정보를 관리한다.

- 요약 대상 Chapter 범위
- AI가 생성한 요약
- 요약 버전
- 생성 시점

---

## 22.3 Table

```
story_summaries
```

---

## 22.4 Columns

| Column | Type | Null | Key | Description |
|---------|------|------|-----|-------------|
| id | BIGINT | NO | PK | PK |
| story_id | BIGINT | NO | FK | Story |
| start_chapter | INT | NO | | 시작 Chapter |
| end_chapter | INT | NO | | 마지막 Chapter |
| summary | LONGTEXT | NO | | AI 요약 |
| summary_version | VARCHAR(20) | NO | | 요약 버전 |
| ai_model | VARCHAR(100) | YES | | 생성 모델 |
| created_at | DATETIME | NO | | 생성일 |
| updated_at | DATETIME | NO | | 수정일 |

---

## 22.5 Example

Chapter

```
1~5
```

↓

Summary

```
주인공은 숲에서 용을 만나 친구가 되었으며,
용의 비늘을 얻었다.

왕국으로 향하는 여정을 시작하였다.
```

↓

AI Prompt

```
Summary

+

최근 Chapter 2개

↓

새로운 Chapter 생성
```

---

## 22.6 Business Rules

Summary는 일정 기준마다 생성한다.

기본 정책

```
5 Chapter
```

마다 생성한다.

---

Summary는 수정하지 않는다.

새 Summary를 생성한다.

---

## 22.7 Relationship

Story

↓

StorySummary

```
1:N
```

---

## 22.8 Index Strategy

| Index | Purpose |
|---------|----------|
| idx_summary_story | Story 조회 |
| idx_summary_range | Chapter 범위 조회 |

---

## 22.9 Entity Summary

| Item | Value |
|------|------|
| Table | story_summaries |
| Parent | stories |
| Child | 없음 |
| Cascade | ALL |

---

# 23. prompt_templates

## 23.1 Overview

PromptTemplate은 AI에게 전달하는 Prompt를 관리한다.

Prompt를 코드에 직접 작성하지 않고 DB에서 관리하여 버전 변경과 실험(A/B Test)이 가능하도록 설계한다.

---

## 23.2 Responsibilities

PromptTemplate은

- Prompt 내용
- Prompt 목적
- Prompt 버전
- 활성 여부

를 관리한다.

---

## 23.3 Table

```
prompt_templates
```

---

## 23.4 Columns

| Column | Type | Null | Key | Description |
|---------|------|------|-----|-------------|
| id | BIGINT | NO | PK | PK |
| name | VARCHAR(100) | NO | | Prompt 이름 |
| version | VARCHAR(20) | NO | | 버전 |
| purpose | VARCHAR(100) | NO | | 사용 목적 |
| template | LONGTEXT | NO | | Prompt 내용 |
| is_active | BOOLEAN | NO | | 활성 여부 |
| created_at | DATETIME | NO | | 생성일 |
| updated_at | DATETIME | NO | | 수정일 |

---

## 23.5 Prompt Types

예시

```
Story Generation

Chapter Generation

Summary Generation

Ending Analysis

Image Prompt

Character Prompt
```

---

## 23.6 Example Template

```
You are a professional storyteller.

Character

{{character}}

Summary

{{summary}}

Current Chapter

{{chapter}}

Player Choice

{{choice}}

Current Flags

{{flags}}

Inventory

{{inventory}}

Write the next chapter.
```

---

## 23.7 Placeholder Rules

Prompt는 반드시 Placeholder를 사용한다.

예시

```
{{character}}

{{summary}}

{{inventory}}

{{flags}}

{{choice}}

{{language}}
```

직접 문자열을 연결하지 않는다.

PromptBuilder가 Placeholder를 치환한다.

---

## 23.8 Version Policy

Prompt 수정 시

UPDATE를 수행하지 않는다.

새 Version을 생성한다.

예시

```
v1.0

↓

v1.1

↓

v2.0
```

기존 Prompt는 유지한다.

---

## 23.9 Relationship

PromptTemplate

↓

Chapter

```
1:N
```

---

PromptTemplate

↓

AIGenerationLog

```
1:N
```

---

## 23.10 Business Rules

항상 하나 이상의 활성 Prompt가 존재해야 한다.

비활성 Prompt는 새로운 Chapter 생성에 사용할 수 없다.

Prompt Version은 생성 이후 변경하지 않는다.

---

## 23.11 Entity Summary

| Item | Value |
|------|------|
| Table | prompt_templates |
| Parent | 없음 |
| Child | chapters, ai_generation_logs |
| Audit | BaseEntity |

---

# 24. Next Section

다음 장에서는 AI Generation Log를 정의한다.

AI Generation Log는 운영 환경에서

- Prompt 추적
- 응답 시간 분석
- Token 사용량
- 비용 분석
- 오류 원인 추적

을 위한 핵심 테이블이다.

# 25. ai_generation_logs

## 25.1 Overview

ai_generation_logs 테이블은 모든 AI 생성 요청과 응답을 기록한다.

운영 환경에서는 AI 호출 실패, 응답 품질 저하, 토큰 사용량 증가, 응답 속도 저하 등의 문제를 분석할 수 있어야 한다.

이 테이블은 디버깅뿐 아니라 Prompt 개선과 비용 분석에도 활용된다.

---

## 25.2 Responsibilities

AI Generation Log는 다음 정보를 관리한다.

- 사용한 AI 모델
- Prompt Template
- 실제 Prompt
- AI 응답
- Token 사용량
- 응답 시간
- 예상 비용
- 요청 결과
- 오류 메시지

---

## 25.3 Table

```
ai_generation_logs
```

---

## 25.4 Columns

| Column | Type | Null | Key | Description |
|---------|------|------|-----|-------------|
| id | BIGINT | NO | PK | 로그 PK |
| story_id | BIGINT | NO | FK | Story |
| chapter_id | BIGINT | YES | FK | Chapter |
| prompt_template_id | BIGINT | YES | FK | Prompt Template |
| ai_model | VARCHAR(100) | NO | | 사용 모델 |
| prompt | LONGTEXT | NO | | 실제 Prompt |
| response | LONGTEXT | YES | | AI 응답 |
| prompt_tokens | INT | YES | | Prompt Token |
| completion_tokens | INT | YES | | Completion Token |
| total_tokens | INT | YES | | 총 Token |
| estimated_cost | DECIMAL(10,4) | YES | | 예상 비용 |
| response_time_ms | INT | YES | | 응답 시간 |
| status | ENUM | NO | | 요청 상태 |
| error_message | TEXT | YES | | 오류 메시지 |
| created_at | DATETIME | NO | | 생성일 |

---

## 25.5 Status Enum

```
SUCCESS

FAILED

TIMEOUT

CANCELLED
```

---

## 25.6 Example

| Model | Total Token | Time | Status |
|--------|------------:|-----:|--------|
| GPT-5 | 2,130 | 4,520 ms | SUCCESS |
| GPT-5 | 0 | 30,000 ms | TIMEOUT |
| Claude Sonnet | 1,845 | 3,102 ms | SUCCESS |

---

## 25.7 Business Rules

### Rule 1

AI 호출 시 반드시 Log를 생성한다.

---

### Rule 2

AI 호출 실패도 반드시 기록한다.

---

### Rule 3

Prompt는 그대로 저장한다.

Prompt를 수정하여 저장하지 않는다.

---

### Rule 4

응답이 없는 경우에도 상태(Status)는 저장한다.

---

### Rule 5

PromptTemplate 삭제 시 기존 Log는 유지한다.

---

## 25.8 Relationship

Story

↓

AI Generation Log

```
1:N
```

Chapter

↓

AI Generation Log

```
1:N
```

PromptTemplate

↓

AI Generation Log

```
1:N
```

---

## 25.9 Index Strategy

| Index | Purpose |
|---------|----------|
| idx_ai_story | Story별 조회 |
| idx_ai_model | 모델별 통계 |
| idx_ai_status | 실패 분석 |
| idx_ai_created | 기간별 조회 |

---

## 25.10 운영 활용 예시

운영자는 다음과 같은 질문에 답할 수 있다.

- 어떤 모델이 가장 빠른가?
- 어떤 Prompt Version의 품질이 좋은가?
- 하루 Token 사용량은 얼마인가?
- 실패율이 가장 높은 시간대는 언제인가?
- 평균 응답 시간은 얼마인가?

---

## 25.11 Entity Summary

| Item | Value |
|------|------|
| Table | ai_generation_logs |
| Parent | stories, chapters, prompt_templates |
| Child | 없음 |
| Audit | 생성일만 관리 |

---

# 26. bookmarks

## 26.1 Overview

bookmarks 테이블은 사용자가 관심 있는 Story를 저장한다.

북마크는 Story의 소유 여부와 관계없이 생성할 수 있다.

---

## 26.2 Responsibilities

Bookmark는 다음 정보를 관리한다.

- 사용자
- Story
- 북마크 생성일

---

## 26.3 Table

```
bookmarks
```

---

## 26.4 Columns

| Column | Type | Null | Key | Description |
|---------|------|------|-----|-------------|
| id | BIGINT | NO | PK | PK |
| user_id | BIGINT | NO | FK | 사용자 |
| story_id | BIGINT | NO | FK | Story |
| created_at | DATETIME | NO | | 생성일 |

---

## 26.5 Constraints

Primary Key

```
PK_bookmarks
```

Foreign Key

```
user_id

↓

users.id
```

```
story_id

↓

stories.id
```

Unique Key

```
(user_id, story_id)
```

동일 Story를 중복 북마크할 수 없다.

---

## 26.6 Relationship

User

↓

Bookmark

```
1:N
```

Story

↓

Bookmark

```
1:N
```

---

## 26.7 Business Rules

### Rule 1

동일 Story는 한 번만 북마크할 수 있다.

---

### Rule 2

Story 삭제 시 관련 Bookmark도 삭제한다.

---

### Rule 3

User 탈퇴 시 Bookmark를 함께 삭제한다.

---

## 26.8 Entity Summary

| Item | Value |
|------|------|
| Table | bookmarks |
| Parent | users, stories |
| Child | 없음 |
| Cascade | REMOVE |

---

# 27. Next Section

다음 장에서는 StorySeed의 **Narrative State Layer**를 정의한다.

여기서는 다음 테이블을 설계한다.

- character_states
- story_flags
- items
- inventories

이 계층은 AI가 현재 이야기의 상태를 이해하고, 선택에 따른 변화를 지속적으로 반영하기 위한 핵심 구조이다.

# 28. character_states

## 28.1 Overview

character_states 테이블은 캐릭터의 현재 서사 상태(Narrative State)를 관리한다.

Character가 캐릭터의 고정된 설정이라면,

CharacterState는 이야기가 진행되면서 변화하는 정보를 저장한다.

이 테이블의 목적은 RPG처럼 능력치를 관리하는 것이 아니라,

AI가 이야기의 흐름을 자연스럽게 이어갈 수 있도록 현재 상태를 제공하는 것이다.

---

## 28.2 Responsibilities

CharacterState는 다음 정보를 관리한다.

- 현재 감정
- 현재 목표
- 현재 위치
- 다른 인물과의 관계
- 이야기 진행에 따른 상태 변화

---

## 28.3 Table

```
character_states
```

---

## 28.4 Columns

| Column | Type | Null | Key | Description |
|---------|------|------|-----|-------------|
| id | BIGINT | NO | PK | PK |
| story_id | BIGINT | NO | FK | Story |
| character_id | BIGINT | NO | FK | Character |
| current_emotion | VARCHAR(100) | YES | | 현재 감정 |
| current_goal | VARCHAR(255) | YES | | 현재 목표 |
| current_location | VARCHAR(255) | YES | | 현재 위치 |
| relationship_summary | TEXT | YES | | 주요 관계 변화 |
| narrative_note | TEXT | YES | | AI 참고 메모 |
| updated_at | DATETIME | NO | | 마지막 갱신 |

---

## 28.5 Example

현재 상태

```
Emotion

불안

Goal

왕국에 도착하기

Location

깊은 숲

Relationship

용과 신뢰를 쌓고 있음
```

AI는 다음 Chapter를 생성할 때 이 정보를 참고하여 이야기의 일관성을 유지한다.

---

## 28.6 Business Rules

CharacterState는 AI가 새로운 Chapter를 생성한 후 갱신한다.

이전 상태는 삭제하지 않고 새로운 내용으로 갱신한다.

Character의 고정 정보는 변경하지 않는다.

---

## 28.7 Relationship

Story

↓

CharacterState

```
1:N
```

Character

↓

CharacterState

```
1:N
```

---

## 28.8 Entity Summary

| Item | Value |
|------|------|
| Table | character_states |
| Parent | stories, characters |
| Child | 없음 |

---

# 29. story_flags

## 29.1 Overview

story_flags 테이블은 이야기에서 이미 발생한 중요한 사건(Facts)을 기록한다.

StoryFlag는 게임 이벤트가 아니라,

AI가 반드시 기억해야 하는 이야기의 사실을 저장하는 역할을 한다.

---

## 29.2 Responsibilities

StoryFlag는 다음 정보를 관리한다.

- 중요한 사건 발생 여부
- 이야기에서 획득한 핵심 정보
- 특정 인물과의 관계 변화
- 이후 전개에 영향을 주는 사실

---

## 29.3 Table

```
story_flags
```

---

## 29.4 Columns

| Column | Type | Null | Key | Description |
|---------|------|------|-----|-------------|
| id | BIGINT | NO | PK | PK |
| story_id | BIGINT | NO | FK | Story |
| flag_key | VARCHAR(100) | NO | | 사건 식별자 |
| flag_value | BOOLEAN | NO | | 발생 여부 |
| description | TEXT | YES | | 설명 |
| created_at | DATETIME | NO | | 생성일 |

---

## 29.5 Example

| Flag | Value |
|------|-------|
| met_old_man | TRUE |
| dragon_friend | TRUE |
| accepted_crown | FALSE |
| found_secret_letter | TRUE |

---

## 29.6 Why StoryFlag?

AI는 StoryFlag를 통해

- 이미 만난 인물을 다시 처음 만나는 것처럼 행동하지 않고
- 이미 해결한 사건을 다시 반복하지 않으며
- 사용자의 선택을 지속적으로 반영한다.

StoryFlag는 이야기의 기억을 유지하는 핵심 데이터이다.

---

## 29.7 Business Rules

StoryFlag는 사용자의 선택 또는 AI가 생성한 Chapter 결과에 따라 생성된다.

이미 존재하는 Flag는 같은 의미로 중복 생성하지 않는다.

Flag 삭제는 일반적으로 수행하지 않는다.

---

## 29.8 Relationship

Story

↓

StoryFlag

```
1:N
```

---

## 29.9 Entity Summary

| Item | Value |
|------|------|
| Table | story_flags |
| Parent | stories |
| Child | 없음 |

---

# 30. Next Section

다음 장에서는 StorySeed의 Story Item 구조를 정의한다.

Story Item은 전투 장비가 아니라,

이야기의 흐름을 변화시키는 중요한 물건과 단서를 관리하기 위한 구조이다.

# 31. items

## 31.1 Overview

items 테이블은 StorySeed에서 이야기의 흐름에 영향을 주는 Story Item을 관리한다.

Story Item은 일반적인 게임의 장비나 소비 아이템이 아니다.

이야기 속에서 등장하는 중요한 물건, 단서, 증거, 유물 등을 저장하며, AI가 이후 전개를 생성할 때 참고하는 핵심 데이터이다.

예를 들어

- 오래된 열쇠
- 왕의 편지
- 용의 비늘
- 신비한 반지
- 낡은 지도

등이 Story Item이 될 수 있다.

---

## 31.2 Responsibilities

Item은 다음 정보를 관리한다.

- 아이템 이름
- 아이템 설명
- 아이템 종류
- AI가 참고할 수 있는 서사 정보

---

## 31.3 Table

```
items
```

---

## 31.4 Columns

| Column | Type | Null | Key | Description |
|---------|------|------|-----|-------------|
| id | BIGINT | NO | PK | Item PK |
| name | VARCHAR(100) | NO | | 아이템 이름 |
| description | TEXT | YES | | 아이템 설명 |
| category | VARCHAR(50) | YES | | 아이템 분류 |
| created_at | DATETIME | NO | | 생성일 |
| updated_at | DATETIME | NO | | 수정일 |

---

## 31.5 Example

| Name | Category |
|------|----------|
| 오래된 열쇠 | Key |
| 왕의 편지 | Document |
| 용의 비늘 | Material |
| 붉은 장미 | Gift |
| 낡은 지도 | Map |

---

## 31.6 Business Rules

Story Item은 여러 Story에서 재사용할 수 있다.

Story Item 자체는 변하지 않는다.

실제 사용자의 보유 여부는 inventories 테이블에서 관리한다.

---

## 31.7 Relationship

Item

↓

Inventory

```
1:N
```

---

## 31.8 Entity Summary

| Item | Value |
|------|------|
| Table | items |
| Parent | 없음 |
| Child | inventories |
| Audit | BaseEntity |

---

# 32. inventories

## 32.1 Overview

inventories 테이블은 사용자가 특정 Story에서 획득한 Story Item을 관리한다.

Item은 마스터 데이터이며,

Inventory는

"누가"

"어떤 Story에서"

"어떤 Item을"

획득했는지를 저장한다.

---

## 32.2 Responsibilities

Inventory는 다음 정보를 관리한다.

- Story
- 사용자
- 획득한 Item
- 획득 시점

---

## 32.3 Table

```
inventories
```

---

## 32.4 Columns

| Column | Type | Null | Key | Description |
|---------|------|------|-----|-------------|
| id | BIGINT | NO | PK | Inventory PK |
| story_id | BIGINT | NO | FK | Story |
| user_id | BIGINT | NO | FK | User |
| item_id | BIGINT | NO | FK | Item |
| acquired_at | DATETIME | NO | | 획득 시간 |

---

## 32.5 Example

| Story | User | Item |
|-------|------|------|
| 숲속의 모험 | Alice | 오래된 열쇠 |
| 숲속의 모험 | Alice | 왕의 편지 |
| 드래곤 전설 | Bob | 용의 비늘 |

---

## 32.6 Business Rules

동일 Story에서는 같은 Item을 중복 획득할 수 없다.

Story 종료 후에도 Inventory는 해당 Story 기록으로 유지된다.

AI는 Inventory를 참고하여 이후 선택지와 이야기를 생성한다.

---

## 32.7 Constraints

Primary Key

```
PK_inventories
```

Foreign Key

```
story_id

↓

stories.id
```

```
user_id

↓

users.id
```

```
item_id

↓

items.id
```

Unique

```
(story_id, item_id)
```

---

## 32.8 Relationship

Story

↓

Inventory

```
1:N
```

---

User

↓

Inventory

```
1:N
```

---

Item

↓

Inventory

```
1:N
```

---

## 32.9 Entity Summary

| Item | Value |
|------|------|
| Table | inventories |
| Parent | stories, users, items |
| Child | 없음 |
| Cascade | REMOVE |
| Audit | acquired_at |
