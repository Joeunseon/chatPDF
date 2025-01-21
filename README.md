# chat-PDF
'chat-PDF'는 ChatPDF API를 활용하여 PDF 파일을 대화형으로 다룰 수 있는 애플리케이션입니다. <br>
사용자는 PDF 파일을 업로드하여 요약 정보를 확인하고, 질문을 던져 실시간으로 답변을 받을 수 있습니다. <br>
이 애플리케이션은 ChatPDF의 주요 기능을 구현한 프로젝트입니다. 

<br>
<br>

## 🔎 주요 기능
**1. PDF 업로드 및 상호작용**
- PDF 파일을 업로드하여 ChatPDF API에 전송
- API에서 요약 정보와 예시 질문 가져오기 
- 예시 질문을 클릭하여 API에서 답변 받기 
- PDF 내용과 관련된 추가 질문을 API에 채팅으로 전달

**2. 사용자 인터페이스**
- 왼쪽 패널: 파일 업로드 및 대화방 목록
- 중앙 패널: 업로드된 PDF 파일 뷰어
- 오른쪽 패널: 채팅 인터페이스

**3. 동적 대화방 관리**
- 대화방 추가, 이름 변경, 삭제 기능
- 대화방에 입장해야만 PDF 뷰어와 채팅 인터페이스 사용 가능

<br>
<br>

## 📆 프로젝트 진행 기간
2025.01.02 ~ 2025.01.17

<br>
<br>

## ✨ 기술 스택
- **Frontend**: HTML, CSS, JavaScript, jQuery, PDF.js
- **Backend**: JAVA, Spring boot (JDK 17), Mybatis
- **Security**: Jasypt(암호화 처리)
- **DB**: mariadb-10.8.8
- **API**: ChatPDF API
- **Version Control**: GitHub
- **Logging**: Logback-Spring
- **Build Tool**: Gradle

<br>
<br>

## 🛠️ 프로젝트 구조
```
└── src
     └── main
           ├── java.com.project.chat_pdf
           │     ├── api                       // 비즈니스 로직 및 API 엔드포인트
           │     │    └── domain               // DDD 구조의 도메인 계층
           │     │         ├── application     // 서비스 레이어 (비즈니스 로직처리)
           │     │         │      ├── dto      // 계층간 데이터 전송을 위한 클래스
           │     │         │      └── domainService.java // 핵심 비즈니스 로직 클래스
           │     │         │
           │     │         ├── domain          // 도메인 entity 및 Aggregate Root 정의
           │     │         ├── infrastructure  // DB 연동 계층
           │     │         ├── presentation    // RestAPI Controller
           │     │         └── value           // Enum 클래스 및 상수 정의
           │     │
           │     ├── aspect                    // AOP 설정 (공통 로직)
           │     ├── common                    // 공통 클래스 모음
           │     ├── config                    // 프로젝트 설정 (에러페이지, 보안 등)
           │     ├── util                      // 유틸리티 클래스 모음
           │     ├── web                       // 웹 컨트롤러 및 뷰 렌더링 담당
           │     ├── ChatPdfApplication.java   // 메인 애플리케이션 실행 클래스
           │     └── ServletInitializer.java   // 서블릿 초기화 설정
           │
           └── resources
                 ├── banner                    // 커스터마이징된 배너 파일
                 ├── mapper                    // DB 매퍼 XML
                 ├── META-INF                  // 프로젝트 메타데이터
                 ├── static                    // 정적 리소스 (CSS, JS, 이미지)
                 ├── templates                 // HTML 템플릿 파일
                 ├── application.properties    // 프로젝트 설정 파일
                 ├── log4jdbc.log4j2.properties  // DB 로깅 설정 파일
                 └── logback-spring.xml        // 로깅 설정 파일
```

<br>
<br>

## 🖇️ 프로젝트 문서
### 🗂️ Page 👉[바로가기](docs/page.md)
### 🗂️ DATABASE 👉[바로가기](docs/database.md)