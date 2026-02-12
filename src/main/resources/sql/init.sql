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
    update_date            datetime default now() comment '할인 정책 변경 시간을 기록', #? 이거 이상한데?
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

create table if not exists pay_logs
(
    id               int auto_increment primary key comment '관리번호',
    pay_time         datetime not null comment '결제 시간',
    kind_of_discount enum ('normal','light','disabled','monthly') comment '차종 유형',
    pay_log          int comment '결제 금액'
) comment ='차량 1대 결제에 대한 기록';
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
drop table auth_token;