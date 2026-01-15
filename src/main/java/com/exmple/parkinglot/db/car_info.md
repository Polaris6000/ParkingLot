classDiagram
direction BT
class node1 {
   plate_number  /* 차량번호 (예: 111가1111) */ varchar(20)
   parking_spot  /* 주차 위치 (예: A01) */ varchar(10)
   int(11) id  /* PK: 시스템 관리 번호 */
}
class node4 {
   is_need_help  /* 장애인 할인 대상 ("Y"|"N") */ char(1)
   is_compact_car  /* 경차 여부 ("Y"|"N") */ char(1)
   int(11) id  /* PK: car_info의 id와 매칭 */
}
class node3 {
   unit_fee  /* 단위당 요금 (예: 10분당 1000원) */ int(11)
   billing_unit_minutes  /* 추가 과금 단위 (예: 10분당) */ int(11)
   int(11) help_discount_rate  /* 장애인 할인 비율 */
   int(11) compact_discount_rate  /* 경차 할인 비율 */
   datetime created_at  /* 등록 날짜 */
   int(11) grace_period_minutes  /* 회차인정시간 */
   max_cap_amount  /* 최대 비용(cap) */ int(11)
   int(11) id  /* PK: 관리 번호 */
}
class node2 {
   varchar(20) plate_number  /* 차량번호 */
   date expiry_date  /* 만료날짜 */
   int(11) id  /* PK: 월주차 등록 번호 */
}
class node0 {
   datetime entry_time  /* 입차시간 */
   datetime exit_time  /* 출차시간 */
   int(11) id  /* PK: car_info의 id와 매칭 */
}

node4  -->  node1 : id
node0  -->  node1 : id
