package com.exmple.parkinglot.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FeePolicyVO {

    private int baseFee;                // '기본 요금'
    private int basicUnitMinute;        // '최초 1시간(60분)'
    private int unitFee;                // '단위당 요금 (예: 10분당 1000원)'
    private int billingUnitMinutes;     // '추가 과금 단위 (예: 30분당)'
    private int helpDiscountRate;       // '장애인 할인 비율 (%)'
    private int compactDiscountRate;    // '경차 할인 비율 (%)'
    private int gracePeriodMinutes;     // '회차인정시간 (분)'
    private int maxCapAmount;           // '하루 최대 비용(cap)'
    LocalDateTime updateDate;           // '요금 정책 변경 시간을 기록'

}
