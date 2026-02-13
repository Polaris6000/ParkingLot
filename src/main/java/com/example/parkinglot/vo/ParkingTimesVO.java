package com.example.parkinglot.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// parking_times 테이블과 매핑되는 VO 클래스

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingTimesVO {

    private Integer id;                     // PK: car_info의 id와 매칭
    private LocalDateTime entryTime;        // 입차시간
    private LocalDateTime exitTime;         // 출차시간 (NULL이면 현재 주차중)
}