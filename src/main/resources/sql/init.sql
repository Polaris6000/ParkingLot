-- 기준 마리아db
-- 첫 접속은 마리아db에 직접 접속 후 아래 내용을 모두 실행.
-- 각 테이블의 생성에 대한 코드
create database if not exists `parking_lot`;

use `parking_lot`;

-- 전용 사용자 생성
create user 'admin'@'%' identified by '8282';

-- 사용자에게 데이터베이스 권한 부여
grant all privileges on `parking_lot`.* to 'admin'@'%';


-- 1. 차량 정보: 현재 주차장에 등록된 차량의 기본 정보 */
CREATE TABLE if not exists car_info
(
    id           INT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK: 시스템 관리 번호',
    plate_number VARCHAR(20) NOT NULL COMMENT '차량번호 (예: 111가1111)',
    parking_spot VARCHAR(10) COMMENT '주차 위치 (예: A01)'
) COMMENT ='차량 기본 정보 테이블';

-- 2. 월주차 등록: 정기권 이용 차량 관리 */
CREATE TABLE if not exists monthly_parking
(
    id           INT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK: 월주차 등록 번호',
    plate_number VARCHAR(20) UNIQUE NOT NULL COMMENT '차량번호',
    name         VARCHAR(20)        NOT NULL COMMENT '회원 이름',
    phone_number CHAR(13)           NOT NULL COMMENT '연락처',
    begin_date   DATE               NOT NULL COMMENT '시작날짜',
    expiry_date  DATE               NOT NULL COMMENT '만료날짜'
) COMMENT ='월주차 정기권 등록 명단';

-- 3. 입출차시간: 차량의 입고 및 출고 기록 */
CREATE TABLE if not exists parking_times
(
    id         INT PRIMARY KEY COMMENT 'PK: car_info의 id와 매칭',
    entry_time DATETIME NOT NULL COMMENT '입차시간',
    exit_time  DATETIME COMMENT '출차시간',
    CONSTRAINT fk_car_times FOREIGN KEY (id) REFERENCES car_info (id)
) COMMENT ='입출차 시간 기록';

-- 4. 할인정보: 차량별 할인 대상 여부 (순화된 표현 반영) */
CREATE TABLE if not exists discount_info
(
    id                     INT PRIMARY KEY COMMENT 'PK: car_info의 id와 매칭',
    is_disability_discount boolean  default false COMMENT '장애인 할인 대상 ("True"|"False")',
    is_compact_car         boolean  default false COMMENT '경차 여부 ("True"|"False")',
    CONSTRAINT fk_car_discount FOREIGN KEY (id) REFERENCES car_info (id)
) COMMENT ='차량별 할인 대상 정보';

-- 5. 비용 정보: 요금 계산을 위한 정책 설정 */
CREATE TABLE if not exists fee_policy
(
    id                    INT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK: 관리 번호',
    base_fee              INT      DEFAULT 2000 COMMENT '기본 요금',
    basic_unit_minute     INT      DEFAULT 60 COMMENT '최초 1시간(60분)',
    unit_fee              INT      DEFAULT 1000 COMMENT '단위당 요금 (예: 10분당 1000원)',
    billing_unit_minutes  INT      DEFAULT 30 COMMENT '추가 과금 단위 (예: 30분당)',
    help_discount_rate    INT      DEFAULT 50 COMMENT '장애인 할인 비율 (%)',
    compact_discount_rate INT      DEFAULT 30 COMMENT '경차 할인 비율 (%)',
    grace_period_minutes  INT      DEFAULT 10 COMMENT '회차인정시간 (분)',
    max_cap_amount        INT      DEFAULT 15000 COMMENT '하루 최대 비용(cap)',
    update_date           datetime default now() comment '요금 정책 변경 시간을 기록'
) COMMENT ='주차 요금 산정 정책';

-- member
create table if not exists admin
(
    `id`             varchar(20) primary key comment '아이디',
    `password`       varchar(100) not null comment '비밀번호',
    `name`           varchar(30) not null comment '사용자의 이름',
    `email`          varchar(50) not null unique comment '이메일 정보',
    `authorization`  enum ('user','master') default 'user' comment '권한정보',
    `authentication` boolean comment '로그인 가능 여부'
);

-- 로그인용 계정을 추가
insert into admin (id, password, name, email, authorization, authentication)
values ('1','1','실험자','testmail@test.com','master',true);


create table if not exists auth_token
(
#     토큰과 관련된 서비스가 필요할까?
#         아무래도 하나 있어야 할거 같은데...
#         회원가입, 비번 찾기, master등업 신청?
#
#     토큰, 유저아이디, 용도, 신청시간, 만료시간, 사용여부
    `token` char(36) primary key comment 'uuid를 받아서 사용.', #어차피 얘가 uuid임.
    `id`    varchar(20) not null comment '신청자의 아이디',
    `use`   enum ('signUp','findPw','upgrade') comment '회원가입용, 비번찾기용, 등급업',
    `register_time` datetime not null comment '신청시간',
    `expiry_time` datetime not null comment '만료시간',
    `is_can_use` boolean default true comment '사용 가능 여부',
    CONSTRAINT fk_admin_id FOREIGN KEY (id) REFERENCES admin (id)
);

create table if not exists pay_logs
(
    id               int auto_increment primary key comment '관리번호',
    pay_time         datetime not null comment '결제 시간',
    kind_of_discount enum ('normal','light','disabled','monthly') comment '차종 유형',
    pay_log          int comment '결제 금액'
) comment ='차량 1대 결제에 대한 기록';


-- 1. 오늘 데이터 (오늘 이용 차량 대수 및 매출 확인용)
INSERT INTO pay_logs (pay_time, kind_of_discount, pay_log) VALUES (NOW(), 'normal', 2000);
INSERT INTO pay_logs (pay_time, kind_of_discount, pay_log) VALUES (NOW(), 'light', 1400);
INSERT INTO pay_logs (pay_time, kind_of_discount, pay_log) VALUES (NOW(), 'disabled', 1000);
INSERT INTO pay_logs (pay_time, kind_of_discount, pay_log) VALUES (NOW(), 'monthly', 0);
INSERT INTO pay_logs (pay_time, kind_of_discount, pay_log) VALUES (DATE_SUB(NOW(), INTERVAL 1 HOUR), 'normal', 3000);

-- 2. 어제 데이터 (일별 통계 그래프/목록 확인용)
INSERT INTO pay_logs (pay_time, kind_of_discount, pay_log) VALUES (DATE_SUB(CURDATE(), INTERVAL 1 SECOND), 'normal', 5000);
INSERT INTO pay_logs (pay_time, kind_of_discount, pay_log) VALUES (DATE_SUB(CURDATE(), INTERVAL 12 HOUR), 'light', 2100);

-- 3. 지난주 데이터 (일별 추이 확인용)
INSERT INTO pay_logs (pay_time, kind_of_discount, pay_log) VALUES (DATE_SUB(NOW(), INTERVAL 2 DAY), 'disabled', 4000);
INSERT INTO pay_logs (pay_time, kind_of_discount, pay_log) VALUES (DATE_SUB(NOW(), INTERVAL 3 DAY), 'normal', 2000);

-- 4. 지난달 데이터 (월별 통계 확인용)
INSERT INTO pay_logs (pay_time, kind_of_discount, pay_log) VALUES (DATE_SUB(NOW(), INTERVAL 1 MONTH), 'normal', 15000);

-- 데이터 예시
INSERT INTO pay_logs (pay_time, kind_of_discount, pay_log) VALUES

-- 2025년 12월 데이터
-- 12월 1일
('2025-12-01 08:30:00', 'normal', 5000),
('2025-12-01 10:00:00', 'light', 3500),
('2025-12-01 11:45:00', 'monthly', 0),
('2025-12-01 13:30:00', 'disabled', 2500),
('2025-12-01 15:00:00', 'normal', 6000),

-- 12월 2일
('2025-12-02 09:00:00', 'normal', 5000),
('2025-12-02 10:30:00', 'light', 3800),
('2025-12-02 12:15:00', 'disabled', 3500),
('2025-12-02 14:00:00', 'normal', 7000),
('2025-12-02 16:30:00', 'monthly', 0),

-- 2026년 1월 데이터
-- 1월 28일
('2026-01-28 08:45:00', 'normal', 5000),
('2026-01-28 10:20:00', 'light', 3500),
('2026-01-28 11:00:00', 'normal', 6000),
('2026-01-28 13:30:00', 'disabled', 2500),
('2026-01-28 15:00:00', 'normal', 8000),
('2026-01-28 16:45:00', 'monthly', 0),

-- 1월 29일
('2026-01-29 09:00:00', 'normal', 5000),
('2026-01-29 10:30:00', 'light', 3500),
('2026-01-29 12:00:00', 'normal', 7000),
('2026-01-29 14:15:00', 'disabled', 3500),
('2026-01-29 16:00:00', 'normal', 6000),

-- 1월 30일
('2026-01-30 08:20:00', 'normal', 5000),
('2026-01-30 09:50:00', 'light', 4200),
('2026-01-30 11:30:00', 'monthly', 0),
('2026-01-30 13:45:00', 'normal', 8000),
('2026-01-30 15:30:00', 'disabled', 2500),
('2026-01-30 17:00:00', 'normal', 6000),

-- 1월 31일
('2026-01-31 08:00:00', 'normal', 5000),
('2026-01-31 10:00:00', 'light', 3500),
('2026-01-31 12:30:00', 'normal', 7000),
('2026-01-31 14:00:00', 'disabled', 3500),
('2026-01-31 16:30:00', 'monthly', 0),

-- 2026년 2월 데이터
-- 2월 1일
('2026-02-01 09:00:00', 'normal', 5000),
('2026-02-01 10:30:00', 'light', 3500),
('2026-02-01 11:30:00', 'disabled', 3000),
('2026-02-01 13:00:00', 'normal', 6000),
('2026-02-01 15:00:00', 'monthly', 0),
('2026-02-01 16:30:00', 'normal', 7000),

-- 2월 2일
('2026-02-02 08:30:00', 'normal', 5000),
('2026-02-02 10:00:00', 'light', 3800),
('2026-02-02 12:00:00', 'disabled', 2500),
('2026-02-02 14:30:00', 'normal', 8000),
('2026-02-02 16:00:00', 'light', 4200),

-- 2월 3일
('2026-02-03 09:15:00', 'normal', 6000),
('2026-02-03 11:00:00', 'monthly', 0),
('2026-02-03 13:30:00', 'disabled', 3500),
('2026-02-03 15:45:00', 'normal', 7000),

-- 2월 4일
('2026-02-04 08:00:00', 'normal', 5000),
('2026-02-04 09:30:00', 'light', 3500),
('2026-02-04 11:30:00', 'disabled', 2500),
('2026-02-04 13:00:00', 'normal', 6000),
('2026-02-04 15:30:00', 'monthly', 0),

-- 2월 5일
('2026-02-05 09:00:00', 'normal', 5000),
('2026-02-05 10:45:00', 'light', 4200),
('2026-02-05 12:30:00', 'normal', 7000),
('2026-02-05 14:15:00', 'disabled', 3500),
('2026-02-05 16:00:00', 'normal', 8000);

-- 전체 데이터 개수 확인
# SELECT COUNT(*) AS total_records FROM pay_logs;

-- 차종별 개수 확인
# SELECT
#     kind_of_discount,
#     COUNT(*) AS count,
#     ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM pay_logs), 2) AS percentage
# FROM pay_logs
# GROUP BY kind_of_discount;

-- 일별 매출 확인
# SELECT
#     DATE(pay_time) AS date,
#     SUM(pay_log) AS total_amount,
#     COUNT(*) AS total_count
# FROM pay_logs
# GROUP BY DATE(pay_time)
# ORDER BY date DESC
# LIMIT 10;

-- 월별 매출 확인
# SELECT
#     DATE_FORMAT(pay_time, '%Y-%m') AS month,
#     SUM(pay_log) AS total_amount,
#     COUNT(*) AS total_count
# FROM pay_logs
# GROUP BY DATE_FORMAT(pay_time, '%Y-%m')
# ORDER BY month DESC;

-- ========================================
-- 유용한 관리 쿼리
-- ========================================

-- 특정 날짜 데이터 삭제
-- DELETE FROM pay_logs WHERE DATE(pay_time) = '2026-01-15';

-- 특정 기간 데이터 삭제
-- DELETE FROM pay_logs WHERE pay_time BETWEEN '2026-01-01' AND '2026-01-31';

-- 전체 데이터 삭제 (주의!)
-- TRUNCATE TABLE pay_logs;

-- 테이블 구조 확인
-- DESC pay_logs;

-- 인덱스 확인
-- SHOW INDEX FROM pay_logs;






