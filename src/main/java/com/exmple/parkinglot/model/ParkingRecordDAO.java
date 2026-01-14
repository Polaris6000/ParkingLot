package com.exmple.parkinglot.model;

public interface ParkingRecordDAO {
    // 입차 기록 생성 (생성된 record_id를 반환)
    int insertRecord(String spotId, String carNumber, String carType, String isMember);

    // 출차 시 최종 요금과 할인된 금액, 시간을 업데이트
    int updateExitRecord(int recordId, int totalFee, int appliedDiscountAmount);

    // 단일 기록 조회
    ParkingRecordDTO getRecord(int recordId);
}