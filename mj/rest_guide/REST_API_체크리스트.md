# REST API 적용 체크리스트 ✅

프로젝트에 REST API를 성공적으로 적용하기 위한 단계별 체크리스트입니다.

## 📋 설정 단계

### 1단계: 라이브러리 추가
- [ ] build.gradle 파일 열기
- [ ] dependencies 섹션에 Gson 추가
  ```gradle
  implementation 'com.google.code.gson:gson:2.10.1'
  ```
- [ ] Gradle 새로고침 (IntelliJ 우측 Gradle 탭 → 🔄 버튼)
- [ ] 빌드 성공 확인

### 2단계: 컨트롤러 추가
- [ ] `TestDataRestController.java` 파일을 프로젝트 controller 패키지에 복사
- [ ] 패키지 경로 확인 (`com.exmple.parkinglot.controller`)
- [ ] 컴파일 오류 없는지 확인
- [ ] Service 클래스 경로 확인 (`TestDataService.INSTANCE`)

### 3단계: 서버 배포
- [ ] Tomcat 서버 재시작
- [ ] 서버 로그에서 오류 확인
- [ ] Servlet 매핑 확인 (로그에서 `/api/test/data/*` 확인)

### 4단계: API 테스트
- [ ] `test_rest_api.html` 파일을 `webapp` 폴더에 복사
- [ ] 브라우저에서 페이지 열기
- [ ] 통계 조회 버튼 클릭하여 정상 작동 확인
- [ ] POST 요청 테스트 (입차 생성 등)
- [ ] DELETE 요청 테스트

## 🔍 검증 체크리스트

### API 동작 확인
- [ ] GET /api/test/data/statistics → 200 OK 응답
- [ ] POST /api/test/data/entry?count=1 → 201 Created 응답
- [ ] DELETE /api/test/data/all → 200 OK 응답
- [ ] 잘못된 경로 → 404 Not Found 응답
- [ ] 잘못된 파라미터 → 400 Bad Request 응답

### 응답 형식 확인
- [ ] Content-Type이 application/json인지 확인
- [ ] success 필드 존재 확인
- [ ] 성공 시 data 필드 존재 확인
- [ ] 실패 시 error 필드 존재 확인
- [ ] 한글이 깨지지 않는지 확인 (UTF-8 인코딩)

### 로깅 확인
- [ ] 요청 시 로그 출력 확인
- [ ] 에러 발생 시 스택 트레이스 출력 확인
- [ ] 정상 처리 시 INFO 레벨 로그 확인

## 🎯 팀 협업 체크리스트

### 코드 공유
- [ ] Git에 REST Controller 커밋
- [ ] 브랜치 이름 확인 (예: `feature/rest-api`)
- [ ] 커밋 메시지 작성 (예: "Add REST API for test data management")
- [ ] Pull Request 생성
- [ ] 팀원에게 리뷰 요청

### 문서화
- [ ] README.md에 API 엔드포인트 목록 추가
- [ ] 팀원들에게 사용법 공유
- [ ] Postman Collection 생성 (선택사항)

### 충돌 방지
- [ ] 기존 TestDataController와 URL 패턴이 다른지 확인
  - 기존: `/test/data`
  - REST: `/api/test/data/*`
- [ ] 두 컨트롤러 모두 정상 작동하는지 확인

## 📱 프론트엔드 연동 체크리스트

### JavaScript 연동
- [ ] fetch API로 호출 테스트
- [ ] 응답 JSON 파싱 확인
- [ ] 에러 처리 구현
- [ ] async/await 또는 Promise 사용

### UI 업데이트
- [ ] API 호출 후 화면 갱신
- [ ] 로딩 인디케이터 표시
- [ ] 성공/실패 메시지 표시
- [ ] 사용자 경험 개선

## ⚠️ 주의사항

### 보안
- [ ] 민감한 정보를 로그에 출력하지 않기
- [ ] SQL Injection 방지 확인
- [ ] 인증/권한 체크 (필요 시)

### 성능
- [ ] 대량 데이터 처리 시 타임아웃 설정
- [ ] 커넥션 풀 설정 확인
- [ ] 불필요한 로그 제거

### 에러 처리
- [ ] 모든 예외에 대한 처리
- [ ] 사용자 친화적인 에러 메시지
- [ ] 개발자를 위한 상세 로그

## 🚀 다음 단계

완료 후 고려할 사항들:

### 기능 확장
- [ ] 페이징 기능 추가
- [ ] 검색/필터링 기능
- [ ] 정렬 기능
- [ ] 파일 업로드/다운로드

### 품질 개선
- [ ] 단위 테스트 작성
- [ ] 통합 테스트 작성
- [ ] API 문서 자동화 (Swagger)
- [ ] 로깅 레벨 최적화

### 아키텍처 개선
- [ ] DTO 클래스 분리
- [ ] Service 레이어 개선
- [ ] 에러 핸들링 전략 수립
- [ ] 공통 응답 포맷 정의

---

## ✨ 완료 확인

모든 체크리스트가 완료되었다면:

1. 팀원들과 API 테스트 진행
2. 발견된 이슈 정리
3. 개선사항 논의
4. 다음 스프린트 계획

---

## 📞 도움이 필요하신가요?

- 에러 발생 시: 로그 확인 후 팀원들과 공유
- API 동작 이상 시: Postman으로 직접 테스트
- 응답 형식 문제 시: REST_API_가이드.md 참고

화이팅! 🎉
