# REST API 전환 가이드 📚

## 목차
1. [REST란 무엇인가?](#rest란-무엇인가)
2. [기존 코드 vs REST 코드 비교](#기존-코드-vs-rest-코드-비교)
3. [주요 변경사항](#주요-변경사항)
4. [설치 및 설정](#설치-및-설정)
5. [API 엔드포인트 목록](#api-엔드포인트-목록)
6. [사용 예시](#사용-예시)
7. [프론트엔드 연동](#프론트엔드-연동)
8. [자주 묻는 질문](#자주-묻는-질문)

---

## REST란 무엇인가?

### REST (REpresentational State Transfer)
- 웹의 장점을 최대한 활용하는 아키텍처 스타일
- HTTP 프로토콜을 그대로 활용하여 웹의 장점을 최대한 활용

### REST의 6가지 원칙

1. **클라이언트-서버 구조**
   - 클라이언트와 서버가 독립적으로 개발/배포 가능
   - 프론트엔드와 백엔드의 명확한 분리

2. **Stateless (무상태)**
   - 서버는 클라이언트의 상태를 저장하지 않음
   - 각 요청은 독립적이며 필요한 모든 정보를 포함

3. **Cacheable (캐시 가능)**
   - HTTP의 캐싱 기능 활용
   - 성능 향상

4. **Layered System (계층화)**
   - 클라이언트는 서버의 내부 구조를 알 필요 없음
   - 중간 서버(프록시, 게이트웨이) 추가 가능

5. **Uniform Interface (일관된 인터페이스)**
   - URI로 자원을 식별
   - HTTP 메서드로 행위를 표현
   - 자기 서술적 메시지

6. **Code-On-Demand (선택사항)**
   - 서버가 클라이언트에게 실행 가능한 코드 전송 가능

### HTTP 메서드와 CRUD 매핑

| HTTP 메서드 | CRUD 작업 | 설명 | 예시 |
|------------|----------|------|-----|
| GET | Read | 조회 | `GET /api/cars` - 차량 목록 조회 |
| POST | Create | 생성 | `POST /api/cars` - 새 차량 등록 |
| PUT | Update | 전체 수정 | `PUT /api/cars/123` - 차량 123 정보 전체 수정 |
| PATCH | Update | 부분 수정 | `PATCH /api/cars/123` - 차량 123 정보 일부 수정 |
| DELETE | Delete | 삭제 | `DELETE /api/cars/123` - 차량 123 삭제 |

---

## 기존 코드 vs REST 코드 비교

### 기존 방식 (JSP Forward)
```java
// 기존: doPost에서 모든 작업 처리
protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    String action = request.getParameter("action");
    
    switch (action) {
        case "bulkEntry":
            // 작업 수행
            int inserted = service.bulkInsertParking(count);
            
            // JSP로 forward
            request.setAttribute("message", inserted + "대 입차 완료");
            request.setAttribute("statistics", service.getStatistics());
            request.getRequestDispatcher("/web/test_data.jsp")
                   .forward(request, response);
            break;
    }
}
```

**문제점:**
- ❌ 모든 작업이 하나의 메서드(doPost)에 집중
- ❌ action 파라미터로 작업 구분 (RESTful하지 않음)
- ❌ JSP에 강하게 결합 (프론트엔드 변경 시 백엔드도 수정 필요)
- ❌ 다른 클라이언트(모바일 앱, 외부 시스템)에서 사용 불가
- ❌ 응답 형식이 HTML(JSP)로 고정

### REST 방식 (JSON Response)
```java
// REST: URI와 HTTP 메서드로 작업 구분
@WebServlet(value = "/api/test/data/*")
protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    String pathInfo = request.getPathInfo();
    
    switch (pathInfo) {
        case "/entry":  // POST /api/test/data/entry
            handleBulkEntry(request, response);
            break;
    }
}

private void handleBulkEntry(HttpServletRequest request, HttpServletResponse response) {
    int inserted = service.bulkInsertParking(count);
    
    // JSON 응답 생성
    Map<String, Object> data = new HashMap<>();
    data.put("inserted", inserted);
    
    // HTTP 201 Created 상태 코드와 함께 JSON 반환
    sendSuccessResponse(response, HttpServletResponse.SC_CREATED,
        inserted + "대 입차 완료", data);
}
```

**장점:**
- ✅ URI가 자원을 명확히 표현 (`/api/test/data/entry`)
- ✅ HTTP 메서드로 행위 표현 (POST = 생성)
- ✅ JSON 응답으로 프론트엔드 독립성 확보
- ✅ 다양한 클라이언트에서 사용 가능
- ✅ 상태 코드로 결과를 명확히 전달

---

## 주요 변경사항

### 1. URL 패턴 변경
```
기존: /test/data?action=bulkEntry
REST: POST /api/test/data/entry
```

### 2. HTTP 메서드 활용
```java
// 기존: 모든 작업이 doPost
doPost(request, response) {
    String action = request.getParameter("action");
    // 분기 처리
}

// REST: HTTP 메서드로 작업 구분
doGet()    → 조회
doPost()   → 생성
doPut()    → 수정
doDelete() → 삭제
```

### 3. 응답 형식 변경
```
기존: JSP 페이지 (HTML)
REST: JSON 데이터

{
  "success": true,
  "message": "10대 입차 완료",
  "data": {
    "inserted": 10
  }
}
```

### 4. 에러 처리 개선
```java
// HTTP 상태 코드 활용
200 OK          - 성공
201 Created     - 생성 성공
400 Bad Request - 잘못된 요청
404 Not Found   - 리소스 없음
500 Internal Server Error - 서버 오류
```

---

## 설치 및 설정

### 1. build.gradle에 Gson 라이브러리 추가
```gradle
dependencies {
    // 기존 라이브러리들...
    
    // JSON 처리를 위한 Gson 추가
    implementation 'com.google.code.gson:gson:2.10.1'
}
```

### 2. Gradle 새로고침
- IntelliJ IDEA 우측의 Gradle 탭 클릭
- 🔄 새로고침 버튼 클릭
- 또는 `./gradlew build` 명령어 실행

### 3. 컨트롤러 배포
- `TestDataRestController.java` 파일을 프로젝트의 controller 패키지에 추가
- 서버 재시작

---

## API 엔드포인트 목록

### 📊 조회 (GET)
```
GET /api/test/data/statistics
→ 주차장 통계 조회
```

### ➕ 생성 (POST)
```
POST /api/test/data/entry?count=5
→ 입차 데이터 5건 생성

POST /api/test/data/exit?count=3
→ 출차 데이터 3건 처리

POST /api/test/data/monthly?count=10
→ 월정액 회원 10명 등록

POST /api/test/data/fee-policy?count=2
→ 요금 정책 2건 등록
```

### 🗑️ 삭제 (DELETE)
```
DELETE /api/test/data/monthly?count=2
→ 월정액 회원 2명 삭제

DELETE /api/test/data/fee-policy?count=1
→ 요금 정책 1건 삭제

DELETE /api/test/data/all
→ 전체 데이터 삭제
```

---

## 사용 예시

### 1. JavaScript (fetch API)
```javascript
// 통계 조회
fetch('/api/test/data/statistics')
  .then(response => response.json())
  .then(data => {
    console.log('통계:', data);
  });

// 입차 데이터 생성
fetch('/api/test/data/entry?count=5', {
  method: 'POST'
})
  .then(response => response.json())
  .then(data => {
    if (data.success) {
      alert('입차 완료: ' + data.message);
    }
  });

// 데이터 삭제
fetch('/api/test/data/monthly?count=2', {
  method: 'DELETE'
})
  .then(response => response.json())
  .then(data => {
    console.log('삭제 결과:', data);
  });
```

### 2. jQuery (AJAX)
```javascript
// 통계 조회
$.ajax({
  url: '/api/test/data/statistics',
  method: 'GET',
  success: function(data) {
    console.log('통계:', data);
  }
});

// 입차 데이터 생성
$.ajax({
  url: '/api/test/data/entry?count=5',
  method: 'POST',
  success: function(data) {
    alert('입차 완료: ' + data.message);
  }
});
```

### 3. cURL (커맨드라인 테스트)
```bash
# 통계 조회
curl http://localhost:8080/api/test/data/statistics

# 입차 데이터 생성
curl -X POST "http://localhost:8080/api/test/data/entry?count=5"

# 데이터 삭제
curl -X DELETE "http://localhost:8080/api/test/data/monthly?count=2"
```

### 4. Postman (API 테스트 도구)
```
1. Postman 실행
2. New Request 생성
3. Method 선택 (GET, POST, DELETE)
4. URL 입력: http://localhost:8080/api/test/data/statistics
5. Send 버튼 클릭
6. 응답 확인
```

---

## 프론트엔드 연동

### HTML + JavaScript 예시
```html
<!DOCTYPE html>
<html>
<head>
    <title>주차장 관리</title>
</head>
<body>
    <h1>주차장 관리 시스템</h1>
    
    <button onclick="loadStatistics()">통계 조회</button>
    <button onclick="createEntry()">입차 생성</button>
    
    <div id="result"></div>
    
    <script>
        // 통계 조회 함수
        async function loadStatistics() {
            try {
                const response = await fetch('/api/test/data/statistics');
                const data = await response.json();
                
                if (data.success) {
                    document.getElementById('result').innerHTML = 
                        data.data.statistics;
                }
            } catch (error) {
                console.error('오류:', error);
            }
        }
        
        // 입차 생성 함수
        async function createEntry() {
            try {
                const response = await fetch('/api/test/data/entry?count=1', {
                    method: 'POST'
                });
                const data = await response.json();
                
                if (data.success) {
                    alert(data.message);
                    loadStatistics();  // 통계 새로고침
                }
            } catch (error) {
                console.error('오류:', error);
            }
        }
    </script>
</body>
</html>
```

### React 컴포넌트 예시
```jsx
import React, { useState, useEffect } from 'react';

function ParkingDashboard() {
    const [statistics, setStatistics] = useState('');
    
    // 컴포넌트 마운트 시 통계 로드
    useEffect(() => {
        loadStatistics();
    }, []);
    
    // 통계 조회
    const loadStatistics = async () => {
        const response = await fetch('/api/test/data/statistics');
        const data = await response.json();
        
        if (data.success) {
            setStatistics(data.data.statistics);
        }
    };
    
    // 입차 생성
    const handleEntry = async () => {
        const response = await fetch('/api/test/data/entry?count=1', {
            method: 'POST'
        });
        const data = await response.json();
        
        if (data.success) {
            alert(data.message);
            loadStatistics();
        }
    };
    
    return (
        <div>
            <h1>주차장 관리</h1>
            <button onClick={handleEntry}>입차 생성</button>
            <pre>{statistics}</pre>
        </div>
    );
}
```

---

## 자주 묻는 질문

### Q1. 기존 JSP 방식과 함께 사용할 수 있나요?
**A:** 네, 가능합니다. REST API는 별도의 URL 패턴(`/api/*`)을 사용하므로 기존 코드와 충돌하지 않습니다.

### Q2. Gson 대신 Jackson을 사용해도 되나요?
**A:** 네, 가능합니다. Jackson도 우수한 JSON 라이브러리입니다.
```gradle
implementation 'com.fasterxml.jackson.core:jackson-databind:2.16.0'
```

### Q3. CORS 오류가 발생합니다.
**A:** 프론트엔드가 다른 도메인에서 실행되는 경우 CORS 설정이 필요합니다.
```java
response.setHeader("Access-Control-Allow-Origin", "*");
response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
response.setHeader("Access-Control-Allow-Headers", "Content-Type");
```

### Q4. 인증/권한은 어떻게 처리하나요?
**A:** JWT(JSON Web Token) 또는 Session 기반 인증을 추가할 수 있습니다.
```java
// JWT 토큰 검증 예시
String token = request.getHeader("Authorization");
if (!isValidToken(token)) {
    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "인증 실패");
    return;
}
```

### Q5. 파라미터가 많을 때는 어떻게 하나요?
**A:** Request Body에 JSON을 전달할 수 있습니다.
```java
// 클라이언트
fetch('/api/test/data/entry', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ count: 5, type: 'compact' })
});

// 서버
BufferedReader reader = request.getReader();
String json = reader.lines().collect(Collectors.joining());
Map<String, Object> params = gson.fromJson(json, Map.class);
```

### Q6. 에러 처리를 더 세밀하게 하려면?
**A:** 커스텀 Exception을 만들고 에러 코드를 정의하세요.
```java
{
  "success": false,
  "error": {
    "code": "PARKING_FULL",
    "message": "주차장이 만차입니다.",
    "details": {
      "available": 0,
      "total": 20
    }
  }
}
```

---

## REST API의 장점 정리

1. **독립성**
   - 프론트엔드와 백엔드가 독립적으로 개발 가능
   - 프론트엔드 기술 변경 시 백엔드 수정 불필요

2. **확장성**
   - 웹, 모바일 앱, IoT 기기 등 다양한 클라이언트 지원
   - 마이크로서비스 아키텍처로 확장 용이

3. **재사용성**
   - 동일한 API를 여러 곳에서 재사용
   - 외부 시스템과의 연동 용이

4. **표준화**
   - HTTP 표준을 따르므로 이해하기 쉬움
   - 개발자들이 쉽게 이해하고 사용 가능

5. **테스트 용이성**
   - Postman, cURL 등으로 쉽게 테스트
   - 자동화된 테스트 작성 용이

---

## 다음 단계

1. **인증/권한 추가**
   - JWT 기반 인증 구현
   - Role-Based Access Control (RBAC)

2. **페이징 처리**
   - 대량 데이터 조회 시 페이징
   - `?page=1&size=10`

3. **검색/필터링**
   - 쿼리 파라미터로 검색 조건 전달
   - `?status=active&type=compact`

4. **API 문서화**
   - Swagger/OpenAPI 적용
   - 자동 문서 생성

5. **버전 관리**
   - API 버전 관리
   - `/api/v1/test/data`, `/api/v2/test/data`

6. **로깅/모니터링**
   - 요청/응답 로깅
   - 성능 모니터링

---

## 참고 자료

- [REST API 설계 가이드](https://restfulapi.net/)
- [HTTP 상태 코드](https://developer.mozilla.org/ko/docs/Web/HTTP/Status)
- [Gson 공식 문서](https://github.com/google/gson)
- [fetch API 사용법](https://developer.mozilla.org/ko/docs/Web/API/Fetch_API)
