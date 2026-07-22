# StorySeed

> AI가 사용자의 선택에 따라 이야기를 생성하는 인터랙티브 스토리 플랫폼

---

## 📖 Project Overview

StorySeed는 사용자의 선택에 따라 AI가 새로운 이야기를 생성하는 인터랙티브 스토리 플랫폼입니다.

기존의 정적인 소설과 달리 사용자의 선택이 다음 이야기에 영향을 주며, 하나의 선택이 새로운 스토리 흐름을 만들어갑니다.

본 프로젝트는 Spring Boot 기반의 웹 서비스 개발과 AI API 연동 경험을 목표로 하는 개인 프로젝트입니다.

---

## 🎯 Project Goals

- Spring Boot 기반 웹 서비스 개발
- RESTful API 설계 및 구현
- AI API를 활용한 이야기 생성
- Session 기반 로그인 구현
- GitHub 문서화를 통한 프로젝트 관리

---

## ✨ MVP Features

현재 MVP에서 제공하는 기능입니다.

- 회원가입
- 로그인 / 로그아웃
- Story 생성
- Story 목록 조회
- Story 상세 조회
- Story 이어하기
- 북마크
- 콘텐츠 신고

---

## 🛠 Tech Stack

### Backend

- Java 25
- Spring Boot 3
- Spring MVC
- Spring Data JPA
- Spring Security
- Thymeleaf

### Database

- MySQL

### Build Tool

- Gradle

### Version Control

- Git
- GitHub

### IDE

- IntelliJ IDEA

---

## 🏗 Architecture

```text
Browser

↓

Controller

↓

Service

↓

Repository

↓

MySQL
```

---

## 📂 Project Structure

```text
src
 ├── controller
 ├── service
 ├── repository
 ├── domain
 ├── dto
 ├── config
 ├── security
 └── common
```

---

## 🗄 Database

ERD는 아래 문서를 참고합니다.

- docs/05_DATABASE.md

---

## 🔗 API

API 명세는 아래 문서를 참고합니다.

- docs/07_API.md

---

## 📚 Documentation

| Document | Description |
|----------|-------------|
| PROJECT_IDEA | 프로젝트 아이디어 |
| REQUIREMENTS | 요구사항 정의 |
| USER_FLOW | 사용자 흐름 |
| ARCHITECTURE | 시스템 구조 |
| DATABASE | ERD 및 데이터베이스 설계 |
| BACKEND | 백엔드 설계 |
| API | API 명세 |

---

## 🚀 Getting Started

```bash
git clone https://github.com/txehyxn/storyseed.git
```

```bash
cd storyseed
```

```bash
./gradlew bootRun
```

---

## 📈 Development Roadmap

- [ ] 회원가입
- [ ] 로그인
- [ ] Story 생성
- [ ] Story 이어하기
- [ ] 북마크
- [ ] 신고 기능
- [ ] AI API 연동

---

## 🔮 Future Plans

MVP 구현 이후 다음 기능을 추가할 예정입니다.

- Story 검색
- 이미지 생성
- 음성 생성
- OAuth2 로그인
- 관리자 기능

---

## 👨‍💻 Developer

김태현

Information & Communication Engineering

Backend Developer
