package com.example.parkinglot.service;

import com.example.parkinglot.dao.StatisticsDAO;
import com.example.parkinglot.dto.StatisticsDTO;
import com.example.parkinglot.vo.StatisticsVO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 통계 비즈니스 로직 처리 서비스
 */
public class StatisticsService {
    
    /**
     * 통계 데이터 조회
     * @param conn DB 연결 객체
     * @param dto 검색 조건
     * @return 통계 데이터 맵
     */
    public Map<String, Object> getStatistics(Connection conn, StatisticsDTO dto) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        StatisticsDAO dao = new StatisticsDAO(conn);
        
        try {
            // 오늘 매출 요약
            StatisticsVO todaySummary = dao.getTodaySummary();
            result.put("todaySummary", todaySummary);
            
            // 이번 달 매출 요약
            StatisticsVO monthSummary = dao.getMonthSummary();
            result.put("monthSummary", monthSummary);
            
            // 차종별 통계 (항상 조회)
            List<StatisticsVO> typeStats = dao.getTypeStatistics();
            result.put("typeStats", typeStats);
            
            // 검색 타입에 따른 추가 조회
            if (dto != null && dto.getSearchType() != null) {
                switch (dto.getSearchType()) {
                    case "daily":
                        // 일별 매출 조회
                        if (dto.getStartDate() != null && dto.getEndDate() != null) {
                            List<StatisticsVO> dailyStats = dao.getDailySales(
                                dto.getStartDate(), 
                                dto.getEndDate()
                            );
                            result.put("dailyStats", dailyStats);
                            result.put("searchType", "daily");
                        }
                        break;
                        
                    case "monthly":
                        // 월별 매출 조회
                        List<StatisticsVO> monthlyStats = dao.getMonthlySales(dto.getYearMonth());
                        result.put("monthlyStats", monthlyStats);
                        result.put("searchType", "monthly");
                        break;
                        
                    default:
                        break;
                }
            }
            
            result.put("success", true);
            
        } catch (SQLException e) {
            result.put("success", false);
            result.put("message", "통계 조회 중 오류가 발생했습니다: " + e.getMessage());
            throw e;
        }
        
        return result;
    }
    
    /**
     * 일별 매출 통계 조회
     * @param conn DB 연결 객체
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 일별 통계 리스트
     */
    public List<StatisticsVO> getDailySales(Connection conn, String startDate, String endDate) 
            throws SQLException {
        StatisticsDAO dao = new StatisticsDAO(conn);
        return dao.getDailySales(startDate, endDate);
    }
    
    /**
     * 월별 매출 통계 조회
     * @param conn DB 연결 객체
     * @param yearMonth 년월
     * @return 월별 통계 리스트
     */
    public List<StatisticsVO> getMonthlySales(Connection conn, String yearMonth) 
            throws SQLException {
        StatisticsDAO dao = new StatisticsDAO(conn);
        return dao.getMonthlySales(yearMonth);
    }
    
    /**
     * 차종별 통계 조회
     * @param conn DB 연결 객체
     * @return 차종별 통계 리스트
     */
    public List<StatisticsVO> getTypeStatistics(Connection conn) throws SQLException {
        StatisticsDAO dao = new StatisticsDAO(conn);
        return dao.getTypeStatistics();
    }
    
    /**
     * 대시보드 요약 정보 조회
     * @param conn DB 연결 객체
     * @return 요약 정보 맵
     */
    public Map<String, Object> getDashboardSummary(Connection conn) throws SQLException {
        Map<String, Object> summary = new HashMap<>();
        StatisticsDAO dao = new StatisticsDAO(conn);
        
        try {
            // 오늘 매출
            StatisticsVO today = dao.getTodaySummary();
            summary.put("todayAmount", today != null ? today.getTotalAmount() : 0);
            summary.put("todayCount", today != null ? today.getTotalCount() : 0);
            
            // 이번 달 매출
            StatisticsVO month = dao.getMonthSummary();
            summary.put("monthAmount", month != null ? month.getTotalAmount() : 0);
            summary.put("monthCount", month != null ? month.getTotalCount() : 0);
            
            summary.put("success", true);
            
        } catch (SQLException e) {
            summary.put("success", false);
            summary.put("message", "대시보드 정보 조회 중 오류가 발생했습니다.");
            throw e;
        }
        
        return summary;
    }
}
