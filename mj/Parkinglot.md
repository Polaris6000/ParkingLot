# ParkingLot 프로젝트 폴더 구조

```
ParkingLot/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── parkinglot/
│   │   │               ├── controller/          # Servlet 컨트롤러
│   │   │               │   ├── parking/
│   │   │               │   │   ├── ParkingListController.java
│   │   │               │   │   ├── ParkingEntryController.java
│   │   │               │   │   ├── ParkingExitController.java
│   │   │               │   │   └── ParkingSearchController.java
│   │   │               │   │
│   │   │               │   ├── member/
│   │   │               │   │   ├── MemberListController.java
│   │   │               │   │   ├── MemberRegisterController.java
│   │   │               │   │   ├── MemberModifyController.java
│   │   │               │   │   └── MemberDeleteController.java
│   │   │               │   │
│   │   │               │   └── admin/
│   │   │               │       ├── DashboardController.java
│   │   │               │       ├── SettingController.java
│   │   │               │       └── StatisticsController.java
│   │   │               │
│   │   │               ├── service/             # 비즈니스 로직
│   │   │               │   ├── ParkingService.java
│   │   │               │   ├── MemberService.java
│   │   │               │   ├── FeeCalculationService.java
│   │   │               │   └── StatisticsService.java
│   │   │               │
│   │   │               ├── dao/                 # 데이터 접근 계층
│   │   │               │   ├── ConnectionUtil.java
│   │   │               │   ├── ParkingDAO.java
│   │   │               │   ├── MemberDAO.java
│   │   │               │   ├── PaymentDAO.java
│   │   │               │   └── SettingDAO.java
│   │   │               │
│   │   │               ├── domain/              # Entity (VO)
│   │   │               │   ├── Parking.java
│   │   │               │   ├── Member.java
│   │   │               │   ├── Payment.java
│   │   │               │   ├── Setting.java
│   │   │               │   └── ParkingZone.java
│   │   │               │
│   │   │               ├── dto/                 # 데이터 전송 객체
│   │   │               │   ├── ParkingDTO.java
│   │   │               │   ├── MemberDTO.java
│   │   │               │   ├── PaymentDTO.java
│   │   │               │   ├── ReceiptDTO.java
│   │   │               │   └── StatisticsDTO.java
│   │   │               │
│   │   │               ├── filter/              # 필터
│   │   │               │   ├── CharacterEncodingFilter.java
│   │   │               │   └── LoginCheckFilter.java
│   │   │               │
│   │   │               ├── util/                # 유틸리티
│   │   │               │   ├── DateUtil.java
│   │   │               │   ├── ValidationUtil.java
│   │   │               │   └── DiscountCalculator.java
│   │   │               │
│   │   │               └── exception/           # 예외 처리
│   │   │                   ├── ParkingException.java
│   │   │                   └── DatabaseException.java
│   │   │
│   │   ├── resources/
│   │   │   ├── log4j2.xml                       # 로그 설정
│   │   │   └── sql/
│   │   │       ├── schema.sql                   # 테이블 생성 스크립트
│   │   │       └── init-data.sql                # 초기 데이터
│   │   │
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── web.xml                      # 배포 서술자
│   │       │   └── views/                       # JSP 파일
│   │       │       ├── common/
│   │       │       │   ├── header.jsp
│   │       │       │   ├── footer.jsp
│   │       │       │   └── nav.jsp
│   │       │       │
│   │       │       ├── parking/
│   │       │       │   ├── list.jsp             # 주차 현황 (메인)
│   │       │       │   ├── entry.jsp            # 입차 등록
│   │       │       │   ├── exit.jsp             # 출차 처리
│   │       │       │   └── receipt.jsp          # 영수증
│   │       │       │
│   │       │       ├── member/
│   │       │       │   ├── list.jsp             # 회원 목록
│   │       │       │   ├── register.jsp         # 회원 등록
│   │       │       │   ├── modify.jsp           # 회원 수정
│   │       │       │   └── detail.jsp           # 회원 상세
│   │       │       │
│   │       │       ├── admin/
│   │       │       │   ├── dashboard.jsp        # 관리자 대시보드
│   │       │       │   ├── statistics.jsp       # 통계 페이지
│   │       │       │   └── settings.jsp         # 설정 관리
│   │       │       │
│   │       │       └── error/
│   │       │           ├── 404.jsp
│   │       │           └── 500.jsp
│   │       │
│   │       ├── resources/                       # 정적 리소스
│   │       │   ├── css/
│   │       │   │   ├── common.css
│   │       │   │   ├── parking.css
│   │       │   │   ├── member.css
│   │       │   │   └── admin.css
│   │       │   │
│   │       │   ├── js/
│   │       │   │   ├── common.js
│   │       │   │   ├── parking.js
│   │       │   │   ├── member.js
│   │       │   │   ├── modal.js
│   │       │   │   └── chart.js
│   │       │   │
│   │       │   └── images/
│   │       │       ├── logo.png
│   │       │       └── icons/
│   │       │
│   │       └── index.jsp                        # 시작 페이지
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── parkinglot/
│                       ├── service/
│                       │   ├── ParkingServiceTest.java
│                       │   └── FeeCalculationServiceTest.java
│                       │
│                       └── dao/
│                           ├── ParkingDAOTest.java
│                           └── MemberDAOTest.java
│
├── .gitignore
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
└── README.md
```

## 주요 패키지별 역할

### 1. **controller** (Servlet)
- HTTP 요청을 받아 Service 계층 호출
- 결과를 JSP로 전달 (RequestDispatcher)
- URL 매핑 및 요청/응답 처리

### 2. **service** (비즈니스 로직)
- 업무 규칙 처리 (요금 계산, 할인 적용 등)
- 트랜잭션 관리
- DAO를 조합하여 복잡한 업무 처리

### 3. **dao** (Data Access Object)
- 데이터베이스 CRUD 작업
- SQL 쿼리 실행
- Connection 관리

### 4. **domain** (Entity/VO)
- 데이터베이스 테이블과 1:1 매핑되는 객체
- 순수 데이터 저장용
- Lombok 활용 (@Getter, @Builder 등)

### 5. **dto** (Data Transfer Object)
- 계층 간 데이터 전송용 객체
- 화면 표시에 필요한 데이터 조합
- ModelMapper로 VO ↔ DTO 변환

### 6. **filter**
- 모든 요청에 공통으로 적용되는 전처리/후처리
- 인코딩 설정, 인증 체크 등

### 7. **util**
- 공통으로 사용하는 유틸리티 메서드
- 날짜 계산, 유효성 검사, 할인율 계산 등

## 주요 파일 설명

### Java 파일
- **ConnectionUtil.java**: HikariCP를 사용한 DB 커넥션 관리 (Singleton)
- **FeeCalculationService.java**: 주차 요금 계산 로직 (회차 시간, 할인 적용)
- **StatisticsService.java**: 통계 데이터 집계 (SUM, COUNT, GROUP BY)

### JSP 파일
- **list.jsp**: 20개 주차 구역 시각화 (메인 대시보드)
- **receipt.jsp**: 영수증 모달 팝업
- **statistics.jsp**: Chart.js를 활용한 통계 그래프

### 설정 파일
- **web.xml**: Servlet 매핑, 필터 설정, 에러 페이지 설정
- **log4j2.xml**: 로그 레벨 및 출력 형식 설정
- **schema.sql**: 데이터베이스 테이블 생성 스크립트

## IntelliJ에서 프로젝트 구조 생성 방법

1. **Java 패키지 생성**
   - `src/main/java` 우클릭 → New → Package
   - `com.example.parkinglot` 입력 후 하위 패키지 순차 생성

2. **리소스 폴더 생성**
   - `src/main/resources` 우클릭 → New → Directory
   - `sql` 폴더 생성

3. **웹 리소스 폴더 확인**
   - Gradle 기반 프로젝트는 `src/main/webapp` 자동 생성
   - WEB-INF, resources 하위 폴더 수동 생성

4. **Mark Directory as**
   - `webapp/resources` 우클릭 → Mark Directory as → Resources Root

이 구조는 MVC 패턴과 계층형 아키텍처를 따르며, 유지보수와 확장이 용이합니다.