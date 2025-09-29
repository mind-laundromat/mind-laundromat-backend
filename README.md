# 🧠 Mind Laundromat - Backend
CBT(인지행동치료) 기반 감정관리 앱 Mind Laundromat(마음 세탁소)의 백엔드 프로젝트입니다.  
Spring Boot 기반으로 구축되었으며, JWT 인증 및 RESTful API를 제공하여  
**Gemini API 상담 챗봇, 감정 기록 관리, 인지 왜곡 분석** 기능을 지원합니다.

> ⚠️ 본 앱은 의료 행위를 대체하지 않습니다. 위기 상황에서는 반드시 긴급 서비스를 이용하세요.

<br>

## 🖥 Development Environment
- JDK 21 (Java Toolchain)
- Spring Boot 3.4.4
- Gradle 8.x
- MySQL 8.x
- IDE: IntelliJ IDEA / Eclipse (권장: IntelliJ)

<br>

## 📁 프로젝트 구조
```
com.example.mind_laundromat
│
├── cbt/                    # CBT 관련 도메인 (감정일기, 인지 왜곡 등)
│   ├── controller          # 요청 처리 컨트롤러
│   ├── dto                 # 데이터 전송 객체
│   ├── entity              # JPA 엔티티 클래스
│   ├── repository          # JPA 레포지토리 인터페이스
│   └── service             # 비즈니스 로직 처리
│
├── user/                   # 사용자 도메인
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
│
├── config/                 # 글로벌 설정 (CORS, Security)
│   ├── CORSConfig
│   └── SecurityConfig
│
├── jwt/                    # JWT 인증 관련 유틸 및 필터
│   ├── JWTFilter
│   ├── JWTUtil
│   └── LoginFilter
│
├── entity/                 # 공통 엔티티 (BaseEntity 등)
│
└── response/               # 응답 처리 관련 클래스
    ├── CommonResponse
    ├── GlobalExceptionHandler
    ├── ResponseBuilder
    └── ResponseCode
```
<br>

## ✨ 시스템 구성도
<img width="3218" height="2278" alt="그림1" src="https://github.com/user-attachments/assets/f193da0f-7283-4592-8ecb-12203a6fdba2" />

<br>

## 🔗 Gemini API 연동 방법

본 프로젝트는 Google Vertex AI Gemini API를 활용하여 상담 챗봇 기능을 제공합니다.  
이를 위해 다음과 같은 환경 변수를 설정해야 합니다.

### 1. 환경 변수 등록
Google Cloud 서비스 계정 키 파일(JSON)을 발급받아 환경 변수에 등록합니다.

```
GOOGLE_APPLICATION_CREDENTIALS=\path\to\your-service-account.json
```


### 2.Spring Boot 설정 (application.yml / application.properties)

아래 값들은 예시이며, 실제 프로젝트 환경에 맞게 수정해야 합니다.
```
spring.ai.model.chat=vertexai
spring.ai.vertex.ai.gemini.projectId=mind-laundromat   # 실제 GCP 프로젝트 ID로 수정 필요
spring.ai.vertex.ai.gemini.location=us-central1        # GCP 리전 (예: asia-northeast3 등)으로 수정 가능
spring.ai.vertex.ai.gemini.chat.options.model=gemini-2.0-flash-001
spring.ai.vertex.ai.gemini.chat.options.temperature=0.5
```
### 3. 주의사항
1. projectId와 location은 본인의 GCP 환경에 맞게 반드시 수정해야 합니다.
2. 모델(gemini-2.0-flash-001)과 파라미터(temperature)는 필요에 따라 변경 가능합니다.
3. 서비스 계정 키 파일은 절대 외부에 공개되지 않도록 주의하세요.

<br>

## 🔗  서버 배포 방법

본 프로젝트는 **AWS EC2**와 **RDS**를 활용한 배포를 권장합니다.  
아래 절차를 따라 배포를 진행할 수 있습니다.

### ✨ 아키텍처
`master` 브랜치에 코드가 Push 되면, GitHub Actions가 자동으로 애플리케이션을 빌드하여 EC2 서버에 배포합니다. EC2 서버는 RDS 데이터베이스와 통신합니다.

### ⚙️ 사전 준비
* **AWS 계정**: EC2, RDS 리소스를 생성할 수 있는 계정
* **GCP 서비스 계정 키**: Gemini API 연동에 필요한 `.json` 키 파일
* **EC2 키 페어**: EC2 인스턴스 접속을 위한 `.pem` 키 파일

### 🚀 배포 절차

#### 1. 최초 설정 (1회)
CI/CD 자동화를 위해 최초 1회 아래의 AWS 인프라 설정 및 GitHub 키 등록이 필요합니다.

* **AWS 인프라 생성**
    * **RDS (MySQL)**: `프리티어` 템플릿, 초기 DB 이름 `mind`로 생성 후 **엔드포인트, ID/PW 기록**.
    * **EC2 (Server)**: `t2.micro` (Amazon Linux) 타입, `.pem` 키 저장 후 **퍼블릭 IP 기록**.
    * **보안 그룹**: EC2 인바운드 `SSH (22, 내 IP)`, `TCP (8080, 위치 무관)` 추가. RDS 인바운드에 EC2 보안 그룹 ID 추가.
    * **서버 설정**: EC2 접속 후 `Java 17` 설치 (`sudo dnf install java-17-amazon-corretto -y`).

* **GitHub Secrets 등록**
    * 프로젝트의 `Settings > Secrets and variables > Actions`에 아래 값들을 등록합니다.

| Secret 이름 | 값 (Value) |
| :--- | :--- |
| `AWS_HOST_IP` | EC2 퍼블릭 IP 주소 |
| `AWS_HOST_USERNAME` | `ec2-user` |
| `AWS_EC2_PEM_KEY` | EC2 `.pem` 키 파일의 내용 전체 |
| `DEPLOY_SCRIPT_ENV` | 앱 실행용 `-D` 옵션 전체 |
| `GCP_KEY_JSON` | GCP `.json` 키 파일의 내용 전체 |

#### 2. 자동 배포
최초 설정이 완료된 이후의 모든 배포는 `master` 브랜치에 **`git push`**만 하면 자동으로 실행됩니다. 배포 상태는 GitHub Actions 탭에서 확인할 수 있습니다.

<br>

## 👥 Contributors

<table>
  <tr>
    <td align="center"><a href="https://github.com/mainsprout"><img src="https://avatars.githubusercontent.com/u/143585656?s=400&u=c4fc8317d32cc54a7091f164a2667cbbc14fa482&v=4" width="100px;" alt=""/><br /><sub><b>mainsprout</b></sub></a><br />🌱</td>
    <td align="center"><a href="https://github.com/hym7196"><img src="https://avatars.githubusercontent.com/u/64295988?v=4" width="100px;" alt=""/><br /><sub><b>hym7196</b></sub></a><br />🎨</td>
    <td align="center"><a href="https://github.com/dusal1111"><img src="https://avatars.githubusercontent.com/u/147612119?v=4" width="100px;" alt=""/><br /><sub><b>dusal1111</b></sub></a><br />🎨</td>
  </tr>
</table>

<br>

## 📜 라이선스
이 프로젝트는 [MIT License](./LICENSE)를 따릅니다.  
활용된 오픈소스 및 외부 서비스의 라이선스는 [THIRD_PARTY_NOTICES.md](./THIRD_PARTY_NOTICES.md)를 참고하세요.
