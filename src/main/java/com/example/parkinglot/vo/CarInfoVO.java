package com.example.parkinglot.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// car_info 테이블과 매핑되는 VO 클래스

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarInfoVO {

    private Integer id;             // PK: 시스템 관리 번호
    private String plateNumber;     // 차량번호 (예: 111가1111)
    private String parkingSpot;     // 주차 위치 (예: A-01) NULL이면 출차된 상태
}