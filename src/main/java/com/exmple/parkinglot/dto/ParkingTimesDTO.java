package com.exmple.parkinglot.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

//입출차 시간 기록
public class ParkingTimesDTO {
    private int id; //car_info의 id와 매칭
    private LocalDateTime entryTime; //입차시간
    private LocalDateTime exitTime; //출차시간
}
