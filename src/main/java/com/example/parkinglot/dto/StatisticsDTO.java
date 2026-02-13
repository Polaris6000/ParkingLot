package com.example.parkinglot.dto;

/**
 * 통계 조회 요청 데이터를 담는 DTO
 */
public class StatisticsDTO {
    private String searchType;  // 검색 타입 (daily, monthly, type)
    private String startDate;   // 시작 날짜
    private String endDate;     // 종료 날짜
    private String yearMonth;   // 년월 (월별 조회용)
    
    // 기본 생성자
    public StatisticsDTO() {}
    
    // 전체 파라미터 생성자
    public StatisticsDTO(String searchType, String startDate, String endDate, String yearMonth) {
        this.searchType = searchType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.yearMonth = yearMonth;
    }
    
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
