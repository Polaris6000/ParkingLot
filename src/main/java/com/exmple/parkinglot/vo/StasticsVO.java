package com.exmple.parkinglot.vo;

import java.time.LocalDateTime;

/**
 * 관리자 대시보드 통합 VO (Value Object)
 * 데이터베이스 테이블과 매핑되는 엔티티
 */
public class StasticsVO {
    // 요금 정책 관련
    private int policyId;
    private int baseFee;
    private int basicUnitMinute;
    private int unitFee;
    private int billingUnitMinutes;
    private int helpDiscountRate;
    private int compactDiscountRate;
    private int gracePeriodMinutes;
    private int maxCapAmount;
    private LocalDateTime policyUpdateDate;
    
    // 매출 통계 관련
    private String period;
    private int totalAmount;
    private int totalCount;
    
    // 차종별 통계
    private String vehicleType;
    private int typeCount;
    
    // 기본 생성자
    public StasticsVO() {}
    
    // Getters and Setters
    public int getPolicyId() {
        return policyId;
    }
    
    public void setPolicyId(int policyId) {
        this.policyId = policyId;
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
    
    public LocalDateTime getPolicyUpdateDate() {
        return policyUpdateDate;
    }
    
    public void setPolicyUpdateDate(LocalDateTime policyUpdateDate) {
        this.policyUpdateDate = policyUpdateDate;
    }
    
    public String getPeriod() {
        return period;
    }
    
    public void setPeriod(String period) {
        this.period = period;
    }
    
    public int getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public int getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    
    public String getVehicleType() {
        return vehicleType;
    }
    
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
    
    public int getTypeCount() {
        return typeCount;
    }
    
    public void setTypeCount(int typeCount) {
        this.typeCount = typeCount;
    }
}
