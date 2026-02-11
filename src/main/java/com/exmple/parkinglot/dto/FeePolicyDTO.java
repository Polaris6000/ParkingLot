package com.exmple.parkinglot.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

//주차요금 산정 정책
public class FeePolicyDTO {
    private int id; //관리번호
    private int baseFee; //기본요금
    private int basicUnitMinute; //최초 1시간(60분)
    private int unitFee; //단위당 요금
    private int billingUnitMinutes; //추과 과금 단위
    private int helpDiscountRate; //장애인 할인비율
    private int compactDiscountRate; //경차 할인비율
    private LocalDateTime createdAt; //정책 등록 날짜
    private int gracePeriodMinutes; //회차 인정 시간
    private int maxCapAmount; //최대비용(cap)
}
