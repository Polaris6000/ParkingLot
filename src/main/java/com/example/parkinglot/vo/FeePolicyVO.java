package com.example.parkinglot.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FeePolicyVO {
    private int id; // 관리번호
    private int baseFee; // 기본요금
    private int basicUnitMinute; // 최초 1시간(60분)
    private int unitFee; // 단위당 요금
    private int billingUnitMinutes; // 추과 과금 단위
    private int helpDiscountRate; // 장애인 할인비율
    private int compactDiscountRate; // 경차 할인비율
    private int gracePeriodMinutes; // 회차 인정 시간
    private int maxCapAmount; // 최대비용(cap)
    private Integer monthlyPay; // 월주차 비용
    private LocalDateTime updateDate; // 정책 등록 날짜

}
