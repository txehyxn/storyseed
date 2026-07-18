# StorySeed Database Design

## 1. 문서 목적

이 문서는 StorySeed 프로젝트의 데이터베이스 구조를 정의한다.

StorySeed는 사용자의 선택을 기반으로 AI가 이야기를 생성하는 서비스이므로, 일반적인 게시판 프로젝트보다 데이터 간의 관계가 중요하다.

본 문서는 다음 내용을 포함한다.

- 데이터베이스 설계 원칙
- ERD 구조
- 테이블 관계
- 핵심 엔티티 정의
- 컬럼 및 데이터 타입
- 제약조건
- 인덱스 전략
- JPA 매핑 기준

---

# 2. 데이터베이스 설계 원칙

StorySeed는 다음 원칙을 기준으로 데이터베이스를 설계한다.

## 2.1 정규화

최소 3NF(Third Normal Form)를 유지한다.

중복 데이터를 최소화하고 데이터 무결성을 유지한다.

---

## 2.2 FK 사용

모든 관계는 Foreign Key를 사용한다.

예외

- Enum 데이터
- 시스템 설정값

---

## 2.3 Soft Delete 미사용

StorySeed에서는 Soft Delete를 사용하지 않는다.

삭제된 데이터는 실제 삭제한다.

다만 추후 운영 정책에 따라 Story Archive 기능을 추가할 수 있다.

---

## 2.4 Audit 컬럼

모든 테이블은 생성일과 수정일을 가진다.

```sql
created_at
updated_at
```

BaseEntity를 통해 관리한다.

---

## 2.5 Primary Key

모든 PK는 BIGINT AUTO_INCREMENT를 사용한다.

예시

```sql
id BIGINT AUTO_INCREMENT PRIMARY KEY
```

UUID는 사용하지 않는다.

---

# 3. ERD

```
User
 │
 │ 1:N
 ▼
Story
 │
 ├──────────────┐
 │              │
 │1:N           │1:1
 ▼              ▼
Chapter      Character
 │
 │1:N
 ▼
Choice

Story
 │
 └───────1:1──────► Report

Story
 │
 ├────────► Genre
 │
 └────────► World
```

---

# 4. 테이블 목록

| 테이블 | 설명 |
|---------|------|
| users | 회원 정보 |
| stories | 이야기 |
| chapters | 챕터 |
| characters | 주인공 |
| choices | 사용자 선택 |
| reports | 선택 리포트 |
| genres | 장르 |
| worlds | 세계관 |

---

# 5. users

회원 정보를 저장한다.

## Table

users

---

## Columns

| 컬럼 | 타입 | NULL | 설명 |
|------|------|------|------|
| id | BIGINT | NO | PK |
| email | VARCHAR(255) | NO | 이메일 |
| password | VARCHAR(255) | NO | BCrypt 암호 |
| nickname | VARCHAR(50) | NO | 닉네임 |
| role | ENUM | NO | USER / ADMIN |
| created_at | DATETIME | NO | 생성일 |
| updated_at | DATETIME | NO | 수정일 |

---

## Constraints

Primary Key

```
id
```

Unique

```
email
nickname
```

---

## Index

```
email
nickname
```

---

## Relationship

User

↓

Story

1 : N

---

# 6. stories

사용자가 생성한 이야기 정보를 저장한다.

하나의 Story는 여러 개의 Chapter를 가진다.

---

## Table

stories

---

## Columns

| 컬럼 | 타입 | NULL | 설명 |
|------|------|------|------|
| id | BIGINT | NO | PK |
| user_id | BIGINT | NO | 작성자 |
| genre_id | BIGINT | NO | 장르 |
| world_id | BIGINT | NO | 세계관 |
| title | VARCHAR(200) | NO | 제목 |
| age_group | ENUM | NO | 대상 연령 |
| atmosphere | ENUM | NO | 이야기 분위기 |
| story_length | ENUM | NO | 이야기 길이 |
| status | ENUM | NO | 진행 상태 |
| current_chapter | INT | NO | 현재 챕터 |
| created_at | DATETIME | NO | 생성일 |
| updated_at | DATETIME | NO | 수정일 |

---

## Constraints

Primary Key

```
id
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

## Index

```
user_id
genre_id
world_id
status
```

---

## Relationship

Story

↓

Chapter

1 : N

---

Story

↓

Character

1 : 1

---

Story

↓

Report

1 : 1

---

# 7. characters

주인공 정보를 저장한다.

Story 하나당 Character 하나를 가진다.

---

## Table

characters

---

## Columns

| 컬럼 | 타입 | NULL | 설명 |
|------|------|------|------|
| id | BIGINT | NO | PK |
| story_id | BIGINT | NO | Story FK |
| name | VARCHAR(100) | NO | 이름 |
| age | VARCHAR(30) | YES | 나이 |
| personality | TEXT | YES | 성격 |
| ability | TEXT | YES | 능력 |
| favorite | TEXT | YES | 좋아하는 것 |
| goal | TEXT | YES | 목표 |
| created_at | DATETIME | NO | 생성일 |
| updated_at | DATETIME | NO | 수정일 |

---

## Constraints

Primary Key

```
id
```

Foreign Key

```
story_id
↓

stories.id
```

Unique

```
story_id
```

(Story 하나당 Character 하나)

---

## Index

```
story_id
```

---

## Relationship

Character

↓

Story

1 : 1

---

# 8. JPA 관계

## User

```java
@OneToMany(mappedBy = "user")
private List<Story> stories;
```

---

## Story

```java
@ManyToOne(fetch = FetchType.LAZY)
private User user;
```

```java
@OneToMany(mappedBy = "story")
private List<Chapter> chapters;
```

```java
@OneToOne(mappedBy = "story")
private Character character;
```

```java
@OneToOne(mappedBy = "story")
private Report report;
```

---

## Character

```java
@OneToOne(fetch = FetchType.LAZY)
private Story story;
```

---

# 9. Cascade 정책

| 관계 | Cascade |
|------|----------|
| Story → Chapter | ALL |
| Story → Character | ALL |
| Story → Report | ALL |
| User → Story | 없음 |

사용자가 삭제되어도 운영 정책에 따라 이야기를 보존할 수 있도록 User → Story에는 Cascade를 적용하지 않는다.

---

# 10. Fetch 전략

모든 연관관계는 기본적으로 Lazy Loading을 사용한다.

예외적으로 즉시 로딩이 필요한 경우는 JPQL Fetch Join 또는 EntityGraph를 사용한다.

현재까지 정의된 엔티티는 모두 FetchType.LAZY를 기본으로 한다.

---
