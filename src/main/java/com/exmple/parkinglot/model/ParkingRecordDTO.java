package com.exmple.parkinglot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingRecordDTO {
    private int recordId;              // 기록 고유 번호
    private String spotId;             // 주차 구역 번호
    private String carNumber;          // 차량 번호
    private String carType;            // 차종 (경차, 장애인, 일반 등) - 추가됨!
    private String isMember;           // 월정액 회원 여부 (Y/N) - 추가됨!
    private String entryTime;          // 입차 시간
    private String exitTime;           // 출차 시간
    private int totalFee;              // 최종 정산 요금
    private int appliedDiscountAmount; // 적용된 할인 금액 (필요시 사용)
}