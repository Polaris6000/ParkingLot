package com.example.parkinglot.dao;

import com.example.parkinglot.util.ConnectionUtil;
import com.example.parkinglot.vo.StatisticsVO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 통계 데이터 접근 객체
 */
public class StatisticsDAO {

    /**
     * 일별 매출 통계 조회
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 일별 통계 리스트
     */
    public List<StatisticsVO> getDailySales(String startDate, String endDate) throws SQLException {
        List<StatisticsVO> list = new ArrayList<>();
        
        String sql = "SELECT DATE(pay_time) AS date, " +
                     "       SUM(pay_log) AS total_amount, " +
                     "       COUNT(*) AS total_count " +
                     "FROM pay_logs " +
                     "WHERE DATE(pay_time) BETWEEN ? AND ? " +
                     "GROUP BY DATE(pay_time) " +
                     "ORDER BY date DESC";
        
        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, startDate);
            preparedStatement.setString(2, endDate);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String date = resultSet.getString("date");
                    int totalAmount = resultSet.getInt("total_amount");
                    int totalCount = resultSet.getInt("total_count");
                    
                    list.add(new StatisticsVO(date, totalAmount, totalCount));
                }
            }
        }
        
        return list;
    }
    
    /**
     * 월별 매출 통계 조회
     * @param yearMonth 년월 (YYYY-MM 형식, null이면 전체)
     * @return 월별 통계 리스트
     */
    public List<StatisticsVO> getMonthlySales(String yearMonth) throws SQLException {
        List<StatisticsVO> list = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DATE_FORMAT(pay_time, '%Y-%m') AS date, ");
        sql.append("       SUM(pay_log) AS total_amount, ");
        sql.append("       COUNT(*) AS total_count ");
        sql.append("FROM pay_logs ");
        
        if (yearMonth != null && !yearMonth.isEmpty()) {
            sql.append("WHERE DATE_FORMAT(pay_time, '%Y-%m') = ? ");
        }
        
        sql.append("GROUP BY DATE_FORMAT(pay_time, '%Y-%m') ");
        sql.append("ORDER BY date DESC");
        
        try (   Connection connection = ConnectionUtil.INSTANCE.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {
            if (yearMonth != null && !yearMonth.isEmpty()) {
                preparedStatement.setString(1, yearMonth);
            }
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String date = resultSet.getString("date");
                    int totalAmount = resultSet.getInt("total_amount");
                    int totalCount = resultSet.getInt("total_count");
                    
                    list.add(new StatisticsVO(date, totalAmount, totalCount));
                }
            }
        }
        
        return list;
    }
    
    /**
     * 차종별 이용 비율 통계 조회
     * kind_of_discount: 'normal'(일반), 'light'(경차), 'disabled'(장애인), 'monthly'(월정액)
     * @return 차종별 통계 리스트
     */
    public List<StatisticsVO> getTypeStatistics() throws SQLException {
        List<StatisticsVO> list = new ArrayList<>();
        
        String sql = "SELECT kind_of_discount, " +
                     "       COUNT(*) AS type_count, " +
                     "       ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM pay_logs WHERE kind_of_discount IS NOT NULL), 2) AS type_percentage " +
                     "FROM pay_logs " +
                     "WHERE kind_of_discount IS NOT NULL " +
                     "GROUP BY kind_of_discount " +
                     "ORDER BY type_count DESC";
        
        try (   Connection connection = ConnectionUtil.INSTANCE.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()) {
            
            while (resultSet.next()) {
                String kindOfDiscount = resultSet.getString("kind_of_discount");
                int typeCount = resultSet.getInt("type_count");
                double typePercentage = resultSet.getDouble("type_percentage");
                
                list.add(new StatisticsVO(kindOfDiscount, typeCount, typePercentage));
            }
        }
        
        return list;
    }
    
    /**
     * 차종 코드를 한글명으로 변환
     * @param code 차종 코드 (normal, light, disabled, monthly)
     * @return 한글 차종명
     */
    public static String getKindOfDiscountName(String code) {
        if (code == null)
            return "미분류";
        
        switch (code) {
            case "normal":
                return "일반";
            case "light":
                return "경차";
            case "disabled":
                return "장애인";
            case "monthly":
                return "월정액";
            default:
                return code;
        }
    }
    
    /**
     * 오늘 매출 요약 정보 조회
     * @return 오늘 통계 정보
     */
    public StatisticsVO getTodaySummary() throws SQLException {
        String sql = "SELECT SUM(pay_log) AS total_amount, " +
                     "       COUNT(*) AS total_count " +
                     "FROM pay_logs " +
                     "WHERE DATE(pay_time) = CURDATE()";
        
        try (   Connection connection = ConnectionUtil.INSTANCE.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()) {
            
            if (resultSet.next()) {
                int totalAmount = resultSet.getInt("total_amount");
                int totalCount = resultSet.getInt("total_count");
                String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
                
                return new StatisticsVO(today, totalAmount, totalCount);
            }
        }
        
        return null;
    }
    
    /**
     * 이번 달 매출 요약 정보 조회
     * @return 이번 달 통계 정보
     */
    public StatisticsVO getMonthSummary() throws SQLException {
        String sql = "SELECT SUM(pay_log) AS total_amount, " +
                     "       COUNT(*) AS total_count " +
                     "FROM pay_logs " +
                     "WHERE DATE_FORMAT(pay_time, '%Y-%m') = DATE_FORMAT(CURDATE(), '%Y-%m')";
        
        try (   Connection connection = ConnectionUtil.INSTANCE.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            
            if (resultSet.next()) {
                int totalAmount = resultSet.getInt("total_amount");
                int totalCount = resultSet.getInt("total_count");
                String month = new java.text.SimpleDateFormat("yyyy-MM").format(new java.util.Date());
                
                return new StatisticsVO(month, totalAmount, totalCount);
            }
        }
        
        return null;
    }
}
