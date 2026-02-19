<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>스마트주차 반월당점 - 세팅</title>
</head>
<body>
3.2 요금 및 할인 정책 (DB 관리 필수)

1. 회차(무료) 시간: 입차 후 10분 이내 출차 시 요금 0원
2. 기본 요금: 최초 1시간 2,000원 (10분 초과 ~ 1시간 이내)
3. 추가 요금: 이후 30분당 1,000원
4. 일일 최대 요금: 15,000원 (입차 시점부터 24시간 기준)
5. 할인 혜택:
- 월정액 회원: 무료 (0원)
- 장애인: 50% 할인
- 경차: 30% 할인
- 중복 할인 불가 (높은 할인율 하나만 적용, 월정액 우선)

4.5 관리자 & 통계 대시보드

* 설정 관리: 기본 요금, 추가 요금, 할인율 수정 기능.



DB 조작 대상
base_fee
basic_unit_minute
unit_fee
billing_unit_minutes
help_discount_rate
compact_discount_rate
grace_period_minutes
max_cap_amount

기능 : DB insert,

List표시는 볼수 있으면 좋다.
</body>
</html>
