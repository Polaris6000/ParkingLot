/* 1. 차량 정보: 현재 주차장에 등록된 차량의 기본 정보 */
CREATE TABLE car_info (

/* 2. 월주차 등록: 정기권 이용 차량 관리 */
CREATE TABLE monthly_parking (

/* 3. 입출차시간: 차량의 입고 및 출고 기록 */
CREATE TABLE parking_times (
    CONSTRAINT fk_car_times FOREIGN KEY (id) REFERENCES car_info(id)

CREATE TABLE discount_info (
    CONSTRAINT fk_car_discount FOREIGN KEY (id) REFERENCES car_info(id)

/* 5. 비용 정보: 요금 계산을 위한 정책 설정 */
CREATE TABLE fee_policy (


SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS car_info;

SET FOREIGN_KEY_CHECKS = 1;