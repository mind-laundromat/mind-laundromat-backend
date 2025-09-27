# 🧠 Mind Laundromat - Backend
CBT(인지행동치료) 기반 감정관리 앱 **"마음 세탁소"**의 백엔드 프로젝트입니다.  
Spring Boot 기반으로 구축되었으며, JWT 인증 및 RESTful API를 제공하여  
**Gemini API 상담 챗봇, 감정 기록 관리, 인지 왜곡 분석** 기능을 지원합니다.

> ⚠️ 본 앱은 의료 행위를 대체하지 않습니다. 위기 상황에서는 반드시 긴급 서비스를 이용하세요.
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

## ✨ 시스템 구성도

## 🔗 Gemini API 연동 방법

본 프로젝트는 Google Vertex AI Gemini API를 활용하여 상담 챗봇 기능을 제공합니다.  
이를 위해 다음과 같은 환경 변수를 설정해야 합니다.

### 1. 환경 변수 등록
Google Cloud 서비스 계정 키 파일(JSON)을 발급받아 환경 변수에 등록합니다.

```bash
export GOOGLE_APPLICATION_CREDENTIALS=/path/to/your-service-account.json
```

Windows PowerShell에서는:
```
setx GOOGLE_APPLICATION_CREDENTIALS "C:\path\to\your-service-account.json"
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

## 📜 라이선스
이 프로젝트는 [MIT License](./LICENSE)를 따릅니다.  
활용된 오픈소스 및 외부 서비스의 라이선스는 [THIRD_PARTY_NOTICES.md](./THIRD_PARTY_NOTICES.md)를 참고하세요.
