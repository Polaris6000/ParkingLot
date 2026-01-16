/* 1. 차량 정보: 현재 주차장에 등록된 차량의 기본 정보 */
CREATE TABLE car_info (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK: 시스템 관리 번호',
    plate_number VARCHAR(20) NOT NULL COMMENT '차량번호 (예: 111가1111)',
    parking_spot VARCHAR(10) COMMENT '주차 위치 (예: A01)'
) COMMENT='차량 기본 정보 테이블';

/* 2. 월주차 등록: 정기권 이용 차량 관리 */
CREATE TABLE members (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK: 월주차 등록 번호',
    plate_number VARCHAR(20) UNIQUE NOT NULL COMMENT '차량번호',
    name VARCHAR(20) NOT NULL COMMENT '회원 이름',
    phone_number CHAR(13) NOT NULL COMMENT '연락처',
    begin_date DATE NOT NULL COMMENT '시작날짜',
    expiry_date DATE NOT NULL COMMENT '만료날짜'
) COMMENT='월주차 정기권 등록 명단';

/* 3. 입출차시간: 차량의 입고 및 출고 기록 */
CREATE TABLE parking_times (
    id INT PRIMARY KEY COMMENT 'PK: car_info의 id와 매칭',
    entry_time DATETIME NOT NULL COMMENT '입차시간',
    exit_time DATETIME COMMENT '출차시간',
    CONSTRAINT fk_car_times FOREIGN KEY (id) REFERENCES car_info(id)
) COMMENT='입출차 시간 기록';

/* 4. 할인정보: 차량별 할인 대상 여부 (순화된 표현 반영) */
CREATE TABLE discount_info (
    id INT PRIMARY KEY COMMENT 'PK: car_info의 id와 매칭',
    is_disability_discount CHAR(1) DEFAULT 'N' COMMENT '장애인 할인 대상 ("Y"|"N")',
    is_compact_car CHAR(1) DEFAULT 'N' COMMENT '경차 여부 ("Y"|"N")',
    CONSTRAINT fk_car_discount FOREIGN KEY (id) REFERENCES car_info(id)
) COMMENT='차량별 할인 대상 정보';

/* 5. 비용 정보: 요금 계산을 위한 정책 설정 */
CREATE TABLE fee_policy (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK: 관리 번호',
    base_fee INT DEFAULT 2000 COMMENT '기본 요금',
    basic_unit_minute INT DEFAULT 60 COMMENT '최초 1시간(60분)',
    unit_fee INT DEFAULT 1000 COMMENT '단위당 요금 (예: 10분당 1000원)',
    billing_unit_minutes INT DEFAULT 30 COMMENT '추가 과금 단위 (예: 30분당)',
    help_discount_rate INT DEFAULT 50 COMMENT '장애인 할인 비율 (%)',
    compact_discount_rate INT DEFAULT 30 COMMENT '경차 할인 비율 (%)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '정책 등록 날짜',
    grace_period_minutes INT DEFAULT 10 COMMENT '회차인정시간 (분)',
    max_cap_amount INT DEFAULT 15000 COMMENT '최대 비용(cap)'
) COMMENT='주차 요금 산정 정책';


-- 아래는 유사 시 사용 ------------------------------------

-- 강제로 테이블 지우기 전에 사용
SET FOREIGN_KEY_CHECKS = 0;

-- 테이블 삭제
DROP TABLE IF EXISTS car_info;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS parking_times;
DROP TABLE IF EXISTS discount_info;
DROP TABLE IF EXISTS fee_policy;

-- 테이블을 지웠으면 원상복구
SET FOREIGN_KEY_CHECKS = 1;