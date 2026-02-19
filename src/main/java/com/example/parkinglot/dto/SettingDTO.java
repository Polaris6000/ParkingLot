package com.example.parkinglot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 요금 정책 데이터 전송 객체 (Data Transfer Object)
 * 클라이언트와 서버 간 데이터 전송에 사용
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingDTO {
    private int id;
    private int baseFee;              // 기본 요금
    private int basicUnitMinute;      // 최초 1시간(60분)
    private int unitFee;              // 단위당 요금
    private int billingUnitMinutes;   // 추가 과금 단위
    private int helpDiscountRate;     // 장애인 할인 비율 (%)
    private int compactDiscountRate;  // 경차 할인 비율 (%)
    private int gracePeriodMinutes;   // 회차인정시간 (분)
    private int maxCapAmount;         // 하루 최대 비용(cap)
    private LocalDateTime updateDate; // 요금 정책 변경 시간

    // Getter & Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBaseFee() {
        return baseFee;
    }

    public void setBaseFee(int baseFee) {
        this.baseFee = baseFee;
    }

    public int getBasicUnitMinute() {
        return basicUnitMinute;
    }

    public void setBasicUnitMinute(int basicUnitMinute) {
        this.basicUnitMinute = basicUnitMinute;
    }

    public int getUnitFee() {
        return unitFee;
    }

    public void setUnitFee(int unitFee) {
        this.unitFee = unitFee;
    }

    public int getBillingUnitMinutes() {
        return billingUnitMinutes;
    }

    public void setBillingUnitMinutes(int billingUnitMinutes) {
        this.billingUnitMinutes = billingUnitMinutes;
    }

    public int getHelpDiscountRate() {
        return helpDiscountRate;
    }

    public void setHelpDiscountRate(int helpDiscountRate) {
        this.helpDiscountRate = helpDiscountRate;
    }

    public int getCompactDiscountRate() {
        return compactDiscountRate;
    }

    public void setCompactDiscountRate(int compactDiscountRate) {
        this.compactDiscountRate = compactDiscountRate;
    }

    public int getGracePeriodMinutes() {
        return gracePeriodMinutes;
    }

    public void setGracePeriodMinutes(int gracePeriodMinutes) {
        this.gracePeriodMinutes = gracePeriodMinutes;
    }

    public int getMaxCapAmount() {
        return maxCapAmount;
    }

    public void setMaxCapAmount(int maxCapAmount) {
        this.maxCapAmount = maxCapAmount;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return "SettingDTO{" +
                "id=" + id +
                ", baseFee=" + baseFee +
                ", basicUnitMinute=" + basicUnitMinute +
                ", unitFee=" + unitFee +
                ", billingUnitMinutes=" + billingUnitMinutes +
                ", helpDiscountRate=" + helpDiscountRate +
                ", compactDiscountRate=" + compactDiscountRate +
                ", gracePeriodMinutes=" + gracePeriodMinutes +
                ", maxCapAmount=" + maxCapAmount +
                ", updateDate=" + updateDate +
                '}';
    }
}
