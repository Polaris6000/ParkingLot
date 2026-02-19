package com.example.parkinglot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 통계 조회 요청 데이터를 담는 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsDTO {
    private String searchType;  // 검색 타입 (daily, monthly, type)
    private String startDate;   // 시작 날짜
    private String endDate;     // 종료 날짜
    private String yearMonth;   // 년월 (월별 조회용)

    // Getters and Setters
    public String getSearchType() {
        return searchType;
    }
    
    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }
    
    public String getStartDate() {
        return startDate;
    }
    
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    
    public String getEndDate() {
        return endDate;
    }
    
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    
    public String getYearMonth() {
        return yearMonth;
    }
    
    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }
    
    @Override
    public String toString() {
        return "StatisticsDTO{" +
                "searchType='" + searchType + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", yearMonth='" + yearMonth + '\'' +
                '}';
    }
}
