package com.exmple.parkinglot.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
//차량 기본 정보
public class CarInfoDTO {
    private int id; //시스템 관리 번호
    private String plateNumber; //차량번호
    private String parkingSpot; //주차위치


}
