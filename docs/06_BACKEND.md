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
