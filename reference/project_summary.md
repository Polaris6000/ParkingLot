# 주차관리 프로젝트 구조 요약

## 개발 환경
- **스택**: JSP + Servlet + JDBC (Raw JDBC) + MariaDB
- **패턴**: MVC (Controller → Service → DAO → DTO/VO)
- **서버**: Tomcat 10.1 / Java Corretto 17 / Gradle
- **라이브러리**: Lombok, HikariCP, MariaDB Connector, ModelMapper, Log4j2, Gson
- **DB**: `parking_lot` / 접속정보: `admin` / `8282` / localhost:3306

---

## DB 테이블

| 테이블 | 주요 컬럼 | 비고 |
|---|---|---|
| `car_info` | id(PK, AUTO), plate_number, parking_spot | 입차 시 INSERT |
| `parking_times` | id(FK→car_info), entry_time, exit_time | 출차 전 exit_time IS NULL |
| `discount_info` | id(FK→car_info), kind | enum('normal','light','disabled','monthly','turn') — 할인 정보 단일 컬럼으로 통합 관리 |
| `monthly_parking` | id, plate_number(UNIQUE), name, phone_number, begin_date, expiry_date | |
| `fee_policy` | base_fee(2000), basic_unit_minute(60), unit_fee(1000), billing_unit_minutes(30), help_discount_rate(50), compact_discount_rate(30), grace_period_minutes(10), max_cap_amount(15000), monthly_pay(200000) | monthly_pay 컬럼 추가 |
| `pay_logs` | id, pay_time, kind_of_discount(normal/light/disabled/monthly/turn), pay_log | 출차 시 INSERT |
| `admin` | id(PK), password(bcrypt), name, email, authorization(user/master), authentication, uuid | |
| `auth_token` | token(uuid), id(FK→admin), use(signUp/findPw/upgrade), register_time, expiry_time, is_can_use | |

---

## 패키지 구조

```
com.example.parkinglot
├── controller
│   ├── AuthenticationController     # /auth/*  로그인·로그아웃·회원가입
│   ├── AdminController              # /admin/* 관리자 정보 수정
│   ├── DashboardController          # /dashboard/* 입출차 메인
│   ├── MonthlyParkingController     # /monthly/* 월정액 CRUD
│   ├── StatisticsController         # /statistics 통계
│   ├── SettingController            # /setting 요금정책 설정
│   └── TestDataController           # 테스트용 (배포 시 제거 필요)
│
├── service
│   ├── AuthService
│   ├── AdminService
│   ├── DashboardService             # enum 싱글톤 INSTANCE
│   ├── MonthlyParkingService        # enum 싱글톤 INSTANCE
│   ├── StatisticsService
│   ├── SettingService
│   ├── MailService
│   └── StasticsService.java         # ※오타 중복 파일 (삭제 필요)
│
├── dao
│   ├── CarInfoDAO                   # @Cleanup 방식
│   ├── ParkingTimesDAO              # @Cleanup 방식
│   ├── DiscountInfoDAO
│   ├── FeePolicyDAO
│   ├── MonthlyParkingDAO            # try-with-resources 방식 (혼용)
│   ├── PayLogsDAO
│   ├── StatisticsDAO
│   ├── AuthDAO
│   ├── AdminDAO
│   └── SettingDAO
│
├── dto                              # DB 컬럼 매핑 + 계층 간 전달
│   ├── ParkingCarDTO                # ※ View 전달에도 사용 중 (VO 역할 겸임)
│   ├── MonthlyParkingDTO
│   ├── FeePolicyDTO
│   ├── StatisticsDTO
│   ├── SettingDTO
│   └── AuthDTO
│
├── vo                               # View 전달용
│   ├── CarInfoVO
│   ├── DiscountInfoVO
│   ├── ParkingTimesVO
│   ├── PayLogsVO
│   ├── MonthlyParkingVO
│   ├── FeePolicyVO
│   ├── StatisticsVO
│   ├── SettingVO
│   ├── AdminVO
│   └── AuthVO
│
├── util
│   ├── ConnectionUtil               # enum 싱글톤, HikariCP 풀 관리
│   │                                # ConnectionUtil.INSTANCE.getConnection()
│   └── MapperUtil
│
├── enums
│   └── AuthKind
│
└── filter
    └── LoginCheckFilter             # 미로그인 접근 차단
```

---

## JSP / 정적 파일 구조

```
webapp
├── index.jsp                        # 루트 → 로그인 리다이렉트
├── WEB-INF
│   ├── web
│   │   ├── dashboard.jsp            # 메인 입출차 화면
│   │   ├── common
│   │   │   ├── header.jsp
│   │   │   ├── footer.jsp
│   │   │   └── car-info.jsp         # 입출차 폼 (include용)
│   │   ├── monthly
│   │   │   ├── monthly-list.jsp
│   │   │   ├── monthly-register.jsp
│   │   │   └── monthly-edit.jsp
│   │   ├── setting
│   │   │   └── setting.jsp
│   │   └── admin
│   │       ├── login.jsp
│   │       ├── sign-up.jsp
│   │       ├── sign-up-accept.jsp
│   │       ├── sign-up-fin.jsp
│   │       ├── find-info.jsp
│   │       ├── change-pw.jsp
│   │       └── token-can-not-use.jsp
│   └── views
│       ├── statistics.jsp
│       └── error.jsp
└── static
    ├── css
    │   ├── public.css               # 공통
    │   ├── dashboard.css
    │   ├── common/header.css
    │   └── admin/(login/sign-up/find-info/change-pw).css
    └── js
        ├── dashboard.js             # ※ 요금 계산 로직 여기 있음
        └── admin/(login/sign-up/find-info/change-pw).js
```

---

## 핵심 코딩 규칙 (작업 시 반드시 준수)

```java
// 1. DB 연결
@Cleanup Connection conn = ConnectionUtil.INSTANCE.getConnection();
@Cleanup PreparedStatement ps = conn.prepareStatement(sql);

// 2. VO 생성
CarInfoVO vo = CarInfoVO.builder()
    .plateNumber("123가4567")
    .parkingSpot("A01")
    .build();

// 3. Service는 enum 싱글톤
public enum DashboardService {
    INSTANCE;
    // ...
}
```

- JSP는 **JSTL 금지**, 스크립틀릿 사용
- SQL Injection 방지: **PreparedStatement 필수**
- DAO 자원 관리: **@Cleanup** (프로젝트 표준)
- 예외 처리 누락 금지

---

## URL 매핑 요약

| URL 패턴 | Controller | 주요 기능 |
|---|---|---|
| `/auth/*` | AuthenticationController | 로그인, 로그아웃, 회원가입 |
| `/dashboard/*` | DashboardController | 주차현황, 입차(POST /enter), 출차(POST /exit) |
| `/monthly/*` | MonthlyParkingController | 월정액 목록/등록/수정/삭제 |
| `/statistics` | StatisticsController | 일별/월별/차종별 통계 |
| `/setting` | SettingController | 요금정책 조회/수정 |
| `/admin/*` | AdminController | 관리자 정보 수정, 비밀번호 변경 |

---

## 알려진 주요 결함

| ID | 위치 | 내용 |
|---|---|---|
| D-001 | car_info / car-info.jsp | 연락처 저장 기능 미구현 |
| D-002 | dashboard.js | 일일 max_cap(15,000원) 미적용 |
| D-003 | dashboard.js | 자정 넘어 출차 시 요금 음수 계산 |
| D-004 | DashboardController | 클라이언트 요금값 서버 재검증 없음 |
| D-005 | DashboardService | 요금 계산 로직이 JS에만 있음 (Service 미분리) |
| D-006 | monthly_parking(DB) | plate_number UNIQUE → 만료 후 재등록 불가 |
| D-007 | ParkingTimesDAO.selectById() | exit_time NULL 시 NPE |
| D-008 | ParkingTimesDAO.selectTodayVisitor() | exit_time < ? 조건으로 주차중 차량 누락 |
| D-009 | MonthlyParkingDAO | ResultSet try-with-resources 미포함 |
| D-010 | dashboard.js enterProcess() | now.getMinutes() + 1 버그 |
