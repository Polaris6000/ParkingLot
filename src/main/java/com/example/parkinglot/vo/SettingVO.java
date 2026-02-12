package com.example.parkinglot.vo;

import java.time.LocalDateTime;

/**
 * 요금 정책 Value Object (데이터베이스 조회 결과 매핑용)
 * DB에서 조회한 데이터를 담는 객체
 */
public class SettingVO {
    private int id;
    private int baseFee;
    private int basicUnitMinute;
    private int unitFee;
    private int billingUnitMinutes;
    private int helpDiscountRate;
    private int compactDiscountRate;
    private int gracePeriodMinutes;
    private int maxCapAmount;
    private LocalDateTime updateDate;

    // 기본 생성자
    public SettingVO() {
    }

    // 전체 생성자
    public SettingVO(int id, int baseFee, int basicUnitMinute, int unitFee,
                     int billingUnitMinutes, int helpDiscountRate, int compactDiscountRate,
                     int gracePeriodMinutes, int maxCapAmount, LocalDateTime updateDate) {
        this.id = id;
        this.baseFee = baseFee;
        this.basicUnitMinute = basicUnitMinute;
        this.unitFee = unitFee;
        this.billingUnitMinutes = billingUnitMinutes;
        this.helpDiscountRate = helpDiscountRate;
        this.compactDiscountRate = compactDiscountRate;
        this.gracePeriodMinutes = gracePeriodMinutes;
        this.maxCapAmount = maxCapAmount;
        this.updateDate = updateDate;
    }

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
        return "SettingVO{" +
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
