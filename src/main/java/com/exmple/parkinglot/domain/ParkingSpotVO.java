package com.exmple.parkinglot.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 주차 구역 정보 VO
 * - 주차 구역 번호 (A01 ~ A20)
 * - 점유 여부
 * - 차량 정보 (점유 시)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSpotVO {

    private String spotNumber;       // 주차 구역 번호 (A01, A02, ...)
    private boolean occupied;        // 점유 여부
    private Integer carId;           // 차량 ID (car_info.id)
    private String plateNumber;      // 차량 번호
    private LocalDateTime entryTime; // 입차 시간
}