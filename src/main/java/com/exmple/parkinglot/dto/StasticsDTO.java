package com.exmple.parkinglot.dto;

import java.util.List;
import java.util.Map;

/**
 * 관리자 대시보드 통합 DTO (Data Transfer Object)
 * Controller와 Service 간 데이터 전송용
 */
public class StasticsDTO {
    // 요금 정책
    private int policyId;
    private int baseFee;
    private int basicUnitMinute;
    private int unitFee;
    private int billingUnitMinutes;
    private int helpDiscountRate;
    private int compactDiscountRate;
    private int gracePeriodMinutes;
    private int maxCapAmount;
    private String updateDate;
    
    // 대시보드 요약
    private int todayAmount;
    private int todayCount;
    private int monthAmount;
    private int monthCount;
    private int currentParking;
    
    // 매출 통계 리스트
    private List<SalesData> salesList;
    
    // 차종별 통계
    private Map<String, Integer> vehicleStats;
    
    // 내부 클래스 - 매출 데이터
    public static class SalesData {
        private String period;
        private int totalAmount;
        private int totalCount;
        private int averageAmount;
        
        public SalesData() {}
        
        public SalesData(String period, int totalAmount, int totalCount) {
            this.period = period;
            this.totalAmount = totalAmount;
            this.totalCount = totalCount;
            this.averageAmount = totalCount > 0 ? totalAmount / totalCount : 0;
        }
        
        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }
        
        public int getTotalAmount() { return totalAmount; }
        public void setTotalAmount(int totalAmount) { this.totalAmount = totalAmount; }
        
        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        
        public int getAverageAmount() { return averageAmount; }
        public void setAverageAmount(int averageAmount) { this.averageAmount = averageAmount; }
    }
    
    // 기본 생성자
    public StasticsDTO() {}
    
    // Getters and Setters
    public int getPolicyId() { return policyId; }
    public void setPolicyId(int policyId) { this.policyId = policyId; }
    
    public int getBaseFee() { return baseFee; }
    public void setBaseFee(int baseFee) { this.baseFee = baseFee; }
    
    public int getBasicUnitMinute() { return basicUnitMinute; }
    public void setBasicUnitMinute(int basicUnitMinute) { this.basicUnitMinute = basicUnitMinute; }
    
    public int getUnitFee() { return unitFee; }
    public void setUnitFee(int unitFee) { this.unitFee = unitFee; }
    
    public int getBillingUnitMinutes() { return billingUnitMinutes; }
    public void setBillingUnitMinutes(int billingUnitMinutes) { this.billingUnitMinutes = billingUnitMinutes; }
    
    public int getHelpDiscountRate() { return helpDiscountRate; }
    public void setHelpDiscountRate(int helpDiscountRate) { this.helpDiscountRate = helpDiscountRate; }
    
    public int getCompactDiscountRate() { return compactDiscountRate; }
    public void setCompactDiscountRate(int compactDiscountRate) { this.compactDiscountRate = compactDiscountRate; }
    
    public int getGracePeriodMinutes() { return gracePeriodMinutes; }
    public void setGracePeriodMinutes(int gracePeriodMinutes) { this.gracePeriodMinutes = gracePeriodMinutes; }
    
    public int getMaxCapAmount() { return maxCapAmount; }
    public void setMaxCapAmount(int maxCapAmount) { this.maxCapAmount = maxCapAmount; }
    
    public String getUpdateDate() { return updateDate; }
    public void setUpdateDate(String updateDate) { this.updateDate = updateDate; }
    
    public int getTodayAmount() { return todayAmount; }
    public void setTodayAmount(int todayAmount) { this.todayAmount = todayAmount; }
    
    public int getTodayCount() { return todayCount; }
    public void setTodayCount(int todayCount) { this.todayCount = todayCount; }
    
    public int getMonthAmount() { return monthAmount; }
    public void setMonthAmount(int monthAmount) { this.monthAmount = monthAmount; }
    
    public int getMonthCount() { return monthCount; }
    public void setMonthCount(int monthCount) { this.monthCount = monthCount; }
    
    public int getCurrentParking() { return currentParking; }
    public void setCurrentParking(int currentParking) { this.currentParking = currentParking; }
    
    public List<SalesData> getSalesList() { return salesList; }
    public void setSalesList(List<SalesData> salesList) { this.salesList = salesList; }
    
    public Map<String, Integer> getVehicleStats() { return vehicleStats; }
    public void setVehicleStats(Map<String, Integer> vehicleStats) { this.vehicleStats = vehicleStats; }
    
    // 계산 메서드
    public int getTodayAverage() {
        return todayCount > 0 ? todayAmount / todayCount : 0;
    }
    
    public int getMonthAverage() {
        return monthCount > 0 ? monthAmount / monthCount : 0;
    }
}
