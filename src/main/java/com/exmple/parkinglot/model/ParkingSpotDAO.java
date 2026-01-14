package com.exmple.parkinglot.model;

import java.util.List;

public interface ParkingSpotDAO {
    // 전체 주차 구역 상태 조회
    List<ParkingSpotDTO> getAllSpots();

    // 특정 구역의 상태(is_occupied)와 현재 기록 ID(current_record_id) 업데이트
    int updateSpotStatus(String spotId, int isOccupied, Integer currentRecordId);
}