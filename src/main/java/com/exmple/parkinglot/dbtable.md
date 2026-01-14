-- 1. 시스템 설정 테이블 (요금 및 정책 관리)
CREATE TABLE system_settings (
    setting_key VARCHAR(50) PRIMARY KEY,    -- 설정 키
    setting_value VARCHAR(100) NOT NULL,    -- 설정 값
    setting_description VARCHAR(255)        -- 설정 설명
);

-- 요금 정책 기초 데이터 삽입
INSERT INTO system_settings (setting_key, setting_value, setting_description) VALUES
('FREE_TIME_LIMIT', '10', '회차 무료 시간(분)'),
('BASE_FEE_1HOUR', '2000', '최초 1시간 기본 요금(원)'),
('EXTRA_FEE_30MIN', '1000', '1시간 초과 후 30분당 추가 요금(원)'),
('DAY_MAX_FEE', '15000', '24시간 기준 일일 최대 요금(원)');

-- 2. 회원 관리 테이블 (월정액 등)
CREATE TABLE members (
    member_id VARCHAR(20) PRIMARY KEY,      -- 회원 아이디
    member_name VARCHAR(50) NOT NULL,       -- 회원 이름
    phone_number VARCHAR(20),               -- 전화번호
    car_number VARCHAR(20) UNIQUE,          -- 등록 차량 번호 (할인 판단 기준)
    registration_date DATETIME DEFAULT NOW() -- 가입일
);

-- 기초 데이터 삽입 (회원 정보)
INSERT INTO members (member_id, member_name, phone_number, car_number)
VALUES ('test01', '홍길동', '010-1234-5678', '123가 4567');

-- 3. 입출차 이력 테이블
CREATE TABLE parking_records (
    record_id INT AUTO_INCREMENT PRIMARY KEY, -- 기록 고유 번호
    spot_id VARCHAR(10) NOT NULL,              -- 주차 구역 ID
    car_number VARCHAR(20) NOT NULL,           -- 차량 번호
    car_type VARCHAR(20),                      -- 센서 인식 차종 (경차, 장애인, 일반 등)
    is_member CHAR(1) DEFAULT 'N',             -- 월정액 여부 (Y/N)
    entry_time DATETIME DEFAULT NOW(),         -- 입차 시간
    exit_time DATETIME,                        -- 출차 시간
    total_fee INT DEFAULT 0,                   -- 최종 정산 금액 (할인 적용 후)
    applied_discount INT DEFAULT 0             -- 적용된 할인 금액
);

-- 4. 주차 구역 상태 테이블
CREATE TABLE parking_spots (
    spot_id VARCHAR(5) PRIMARY KEY,            -- 구역 번호 (예: A-01)
    is_occupied TINYINT(1) DEFAULT 0,          -- 점유 여부 (0:비음, 1:주차중)
    current_record_id INT,                     -- 현재 주차 기록 외래키
    FOREIGN KEY (current_record_id) REFERENCES parking_records(record_id)
);

-- 주차 구역 초기 데이터 (A-01 ~ A-20 생성)
INSERT INTO parking_spots (spot_id)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 20
)
SELECT CONCAT('A-', LPAD(n, 2, '0')) FROM seq;

-- 테스트용 쿼리 -----------------------------------------

-- 기존 데이터 초기화
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE members;
TRUNCATE TABLE parking_records;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. 월정액 회원 (요금 무조건 0원 대상)
INSERT INTO members (member_id, member_name, phone_number, car_number) VALUES
('user01', '김월정', '010-1111-1111', '11가 1111'),
('user02', '이멤버', '010-2222-2222', '22나 2222');


-- 아래는 유사 시 사용 ------------------------------------

-- 강제로 테이블 지우기 전에 사용
SET FOREIGN_KEY_CHECKS = 0;

-- 테이블 삭제
DROP TABLE IF EXISTS parking_records;
DROP TABLE IF EXISTS parking_spots;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS system_settings;

-- 테이블을 지웠으면 원상복구
SET FOREIGN_KEY_CHECKS = 1;