package com.example.parkinglot.vo;

import lombok.*;

/**
 * 통계 데이터를 담는 Value Object
 * pay_logs 테이블: id, pay_time(datetime), kind_of_discount(enum), pay_log(int)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatisticsVO {
    private String date;           // 날짜 (일별/월별)
    private int totalAmount;       // 총 결제 금액 (pay_log 합계)
    private int totalCount;        // 오늘 이용차량 대수
    private String kindOfDiscount; // 차종 유형 (normal, light, disabled, monthly)
    private int typeCount;         // 차종별 카운트
    private double typePercentage; // 차종별 비율

    // 일별/월별 매출용 생성자
    public StatisticsVO(String date, int totalAmount, int totalCount) {
        this.date = date;
        this.totalAmount = totalAmount;
        this.totalCount = totalCount;
    }
    
    // 차종별 통계용 생성자
    public StatisticsVO(String kindOfDiscount, int typeCount, double typePercentage) {
        this.kindOfDiscount = kindOfDiscount;
        this.typeCount = typeCount;
        this.typePercentage = typePercentage;
    }
    
    // Getters and Setters
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
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
    
    public String getKindOfDiscount() {
        return kindOfDiscount;
    }
    
    public void setKindOfDiscount(String kindOfDiscount) {
        this.kindOfDiscount = kindOfDiscount;
    }
    
    public int getTypeCount() {
        return typeCount;
    }
    
    public void setTypeCount(int typeCount) {
        this.typeCount = typeCount;
    }
    
    public double getTypePercentage() {
        return typePercentage;
    }
    
    public void setTypePercentage(double typePercentage) {
        this.typePercentage = typePercentage;
    }
    
    @Override
    public String toString() {
        return "StatisticsVO{" +
                "date='" + date + '\'' +
                ", totalAmount=" + totalAmount +
                ", totalCount=" + totalCount +
                ", kindOfDiscount='" + kindOfDiscount + '\'' +
                ", typeCount=" + typeCount +
                ", typePercentage=" + typePercentage +
                '}';
    }
}
