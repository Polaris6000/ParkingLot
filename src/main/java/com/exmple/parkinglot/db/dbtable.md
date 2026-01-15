/* 1. 차량 정보: 현재 주차장에 등록된 차량의 기본 정보 */
CREATE TABLE car_info (
    id INT AUTO_INCREMENT PRIMARY KEY,      -- PK: 시스템 관리 번호
    plate_number VARCHAR(20) NOT NULL,      -- 차량번호 (예: 111가1111)
    parking_spot VARCHAR(10)                -- 주차 위치 (예: A01)
);

/* 2. 월주차 등록: 정기권 이용 차량 관리 */
CREATE TABLE monthly_parking (
    id INT AUTO_INCREMENT PRIMARY KEY,      -- PK: 월주차 등록 번호
    plate_number VARCHAR(20) NOT NULL,      -- 차량번호
    expiry_date DATE NOT NULL               -- 만료날짜
);

/* 3. 입출차시간: 차량의 입고 및 출고 기록 */
CREATE TABLE parking_times (
    id INT PRIMARY KEY,                     -- PK: car_info의 id와 매칭
    entry_time DATETIME NOT NULL,           -- 입차시간
    exit_time DATETIME,                     -- 출차시간
    CONSTRAINT fk_car_times FOREIGN KEY (id) REFERENCES car_info(id)
);

/* 4. 할인정보: 차량별 할인 대상 여부 */
CREATE TABLE discount_info (
    id INT PRIMARY KEY,                     -- PK: car_info의 id와 매칭
    is_need_help CHAR(1) DEFAULT 'N',       -- 장애인 할인 대상 ("Y"|"N")
    is_compact_car CHAR(1) DEFAULT 'N',     -- 경차 여부 ("Y"|"N")
    CONSTRAINT fk_car_discount FOREIGN KEY (id) REFERENCES car_info(id)
);

/* 5. 비용 정보: 요금 계산을 위한 정책 설정 */
CREATE TABLE fee_policy (
    id INT AUTO_INCREMENT PRIMARY KEY,      -- PK: 관리 번호
    unit_fee INT DEFAULT 1000,              -- 단위당 요금 (예: 10분당 1000원)
    billing_unit_minutes INT DEFAULT 10,    -- 추가 과금 단위 (예: 10분당)
    help_discount_rate INT DEFAULT 50,      -- 장애인 할인 비율
    compact_discount_rate INT DEFAULT 30,   -- 경차 할인 비율
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP, -- 등록 날짜
    grace_period_minutes INT DEFAULT 10,    -- 회차인정시간
    max_cap_amount INT DEFAULT 15000        -- 최대 비용(cap)
);

-- 테스트용 쿼리 -----------------------------------------


-- 아래는 유사 시 사용 ------------------------------------

-- 강제로 테이블 지우기 전에 사용
SET FOREIGN_KEY_CHECKS = 0;

-- 테이블 삭제
DROP TABLE IF EXISTS car_info;
DROP TABLE IF EXISTS monthly_parking;
DROP TABLE IF EXISTS parking_times;
DROP TABLE IF EXISTS discount_info;
DROP TABLE IF EXISTS fee_policy;

-- 테이블을 지웠으면 원상복구
SET FOREIGN_KEY_CHECKS = 1;