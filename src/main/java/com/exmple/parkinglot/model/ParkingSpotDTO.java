package com.exmple.parkinglot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSpotDTO { // 주차 구역 정보
    private String spotId;              // 주차 구역 번호
    private int isOccupied;             // 주차 구역 상태
    private Integer currentRecordId;    // NULL일 수 있으니 Integer
    private String carNumber;           // 조인해서 가져올 차량번호
}
