testcase 필요 기능
1. 대량의 입차 출차 기능
-입차중인 차량 제한 : 최대 20개, 위치 중복 불가
- 입차할 때 여러 테이블 동시 등록
2. 회원 추가 여럿 등록
3. 금액정책 변경사항 여럿 등록
4. 필요하다면 일부 수량 랜덤 삭제까지

# Dashboard 주차 구역 null 문제 해결 가이드

## 발견된 문제점

### 1. 주차 구역 포맷 불일치 ⚠️
**문제**: DB는 `A01` 형식으로 저장하는데, 코드는 `A-01` 형식으로 처리
**위치**: 
- `DashboardDAO.java` - getAllParkingSpots() 메서드
- `dash_board.js` - createEmptySpots() 함수

**해결**:
```java
// 수정 전
String spotNumber = String.format("A-%02d", i);

// 수정 후
String spotNumber = String.format("A%02d", i);  // 하이픈 제거
```

### 2. JSP에서 JSON 변환 오류 ⚠️
**문제**: JSTL += 연산자 잘못 사용으로 JSON 생성 실패
**위치**: `dashboard.jsp` - JavaScript 데이터 전달 부분

**해결**:
```jsp
<!-- 수정 전 (잘못된 += 사용) -->
plateNumber: ${spot.plateNumber != null ? '"' += spot.plateNumber += '"' : 'null'}

<!-- 수정 후 (조건문으로 변경) -->
plateNumber: <c:choose>
    <c:when test="${spot.plateNumber != null}">"${spot.plateNumber}"</c:when>
    <c:otherwise>null</c:otherwise>
</c:choose>
```

### 3. 검색 결과 강조 변수 누락 ⚠️
**문제**: 검색 결과를 JavaScript로 전달하는 변수 미선언
**위치**: `dashboard.jsp`

**해결**:
```jsp
<!-- 추가 -->
<c:if test="${not empty searchResult}">
let searchResultSpot = "${searchResult.spotNumber}";
</c:if>
```

### 4. Ajax 응답 처리 미구현 ⚠️
**문제**: Controller에서 Ajax 요청 구분 및 JSON 응답 미구현
**위치**: `DashboardController.java`

**해결**:
- Jackson ObjectMapper 추가
- Ajax 파라미터로 요청 구분
- JSON 응답 메서드 구현

### 5. VO 클래스 누락 ⚠️
**문제**: `DashboardStatsVO`, `ParkingSpotVO` 클래스 정의 필요

**해결**: 새로 생성한 VO 클래스 파일 사용


## 수정된 파일 목록

1. **DashboardDAO.java**
   - 주차 구역 포맷 A01로 수정
   - 주석 추가

2. **DashboardController.java**
   - Ajax 요청 처리 추가
   - JSON 응답 메서드 구현
   - 에러 처리 개선

3. **dash_board.js**
   - 주차 구역 포맷 A01로 수정
   - Ajax 응답 처리 개선
   - 주석 보강

4. **dashboard.jsp**
   - JSON 변환 오류 수정
   - 검색 결과 변수 추가
   - 불필요한 디버깅 코드 제거

5. **DashboardStatsVO.java** (신규)
   - 통계 정보 VO 클래스

6. **ParkingSpotVO.java** (신규)
   - 주차 구역 정보 VO 클래스


## 추가 필요 작업

### 1. DB 데이터 확인
주차 구역이 올바른 형식으로 저장되어 있는지 확인:

```sql
-- A01, A02 형식이어야 함 (하이픈 없음)
SELECT parking_spot FROM car_info WHERE parking_spot IS NOT NULL;

-- 만약 A-01 형식으로 저장되어 있다면 업데이트
UPDATE car_info 
SET parking_spot = REPLACE(parking_spot, '-', '') 
WHERE parking_spot LIKE 'A-%';
```

### 2. 파일 경로 확인
수정된 파일들을 올바른 경로에 배치:

```
src/main/java/com/exmple/parkinglot/
├── controller/
│   └── DashboardController.java
├── dao/
│   └── DashboardDAO.java
├── domain/
│   ├── DashboardStatsVO.java
│   └── ParkingSpotVO.java
└── service/
    └── DashboardService.java

src/main/webapp/
├── static/js/
│   └── dash_board.js
└── web/
    └── dashboard.jsp
```


## 테스트 방법

1. **서버 재시작** 후 `/dashboard` 접속
2. **브라우저 콘솔(F12)** 확인:
   - `parkingSpotsData` 배열 출력 확인
   - 20개 구역 데이터 확인
3. **차량 검색** 기능 테스트
4. **Ajax 업데이트** 기능 테스트 (주석 해제 필요)


## 주의사항

⚠️ **주차 구역 형식 통일**: DB와 코드 모두 `A01` 형식 사용 (하이픈 없음)
⚠️ **실시간 업데이트**: 필요시 `dash_board.js` 마지막 줄 주석 해제