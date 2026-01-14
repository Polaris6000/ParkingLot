package com.exmple.parkinglot.model;

public interface ParkingService {
    // 1. 입차 처리 (기록 생성 + 구역 업데이트)
    int processEntry(String spotId, String carNumber, String carType);

    // 2. 출차 처리 (요금 계산 + 기록 업데이트 + 구역 비우기)
    int processExit(int recordId);
}