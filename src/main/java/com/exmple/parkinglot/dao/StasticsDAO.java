package com.exmple.parkinglot.dao;

import com.exmple.parkinglot.vo.StasticsVO;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자 대시보드 DAO (Data Access Object)
 * 데이터베이스 접근 및 CRUD 작업
 */
public class StasticsDAO {
    private Connection conn;
    
    // DB 연결 정보
    private static final String URL = "jdbc:mariadb://localhost:3306/parking_lot";
    private static final String USER = "admin";
    private static final String PASSWORD = "8282";
    
    /**
     * DB 연결
     */
    private void connect() throws SQLException {
        if (conn == null || conn.isClosed()) {
            try {
                Class.forName("org.mariadb.jdbc.Driver");
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MariaDB 드라이버를 찾을 수 없습니다.", e);
            }
        }
    }
    
    /**
     * DB 연결 해제
     */
    private void disconnect() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 현재 요금 정책 조회
     */
    public StasticsVO getFeePolicy() throws SQLException {
        connect();
        StasticsVO vo = null;
        String sql = "SELECT * FROM fee_policy ORDER BY id DESC LIMIT 1";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                vo = new StasticsVO();
                vo.setPolicyId(rs.getInt("id"));
                vo.setBaseFee(rs.getInt("base_fee"));
                vo.setBasicUnitMinute(rs.getInt("basic_unit_minute"));
                vo.setUnitFee(rs.getInt("unit_fee"));
                vo.setBillingUnitMinutes(rs.getInt("billing_unit_minutes"));
                vo.setHelpDiscountRate(rs.getInt("help_discount_rate"));
                vo.setCompactDiscountRate(rs.getInt("compact_discount_rate"));
                vo.setGracePeriodMinutes(rs.getInt("grace_period_minutes"));
                vo.setMaxCapAmount(rs.getInt("max_cap_amount"));
                
                Timestamp timestamp = rs.getTimestamp("update_date");
                if (timestamp != null) {
                    vo.setPolicyUpdateDate(timestamp.toLocalDateTime());
                }
            }
        } finally {
            disconnect();
        }
        
        return vo;
    }
    
    /**
     * 요금 정책 수정
     */
    public int updateFeePolicy(StasticsVO vo) throws SQLException {
        connect();
        String sql = "UPDATE fee_policy SET " +
                     "base_fee = ?, basic_unit_minute = ?, unit_fee = ?, " +
                     "billing_unit_minutes = ?, help_discount_rate = ?, " +
                     "compact_discount_rate = ?, grace_period_minutes = ?, " +
                     "max_cap_amount = ?, update_date = NOW() WHERE id = ?";
        
        int result = 0;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, vo.getBaseFee());
            pstmt.setInt(2, vo.getBasicUnitMinute());
            pstmt.setInt(3, vo.getUnitFee());
            pstmt.setInt(4, vo.getBillingUnitMinutes());
            pstmt.setInt(5, vo.getHelpDiscountRate());
            pstmt.setInt(6, vo.getCompactDiscountRate());
            pstmt.setInt(7, vo.getGracePeriodMinutes());
            pstmt.setInt(8, vo.getMaxCapAmount());
            pstmt.setInt(9, vo.getPolicyId());
            
            result = pstmt.executeUpdate();
        } finally {
            disconnect();
        }
        
        return result;
    }
    
    /**
     * 일별 매출 통계 (최근 30일)
     */
    public List<StasticsVO> getDailySales() throws SQLException {
        connect();
        List<StasticsVO> list = new ArrayList<>();
        
        String sql = "SELECT DATE_FORMAT(pay_time, '%Y-%m-%d') as period, " +
                     "SUM(pay_log) as total_amount, COUNT(*) as total_count " +
                     "FROM pay_logs " +
                     "WHERE pay_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                     "GROUP BY DATE_FORMAT(pay_time, '%Y-%m-%d') " +
                     "ORDER BY period DESC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                StasticsVO vo = new StasticsVO();
                vo.setPeriod(rs.getString("period"));
                vo.setTotalAmount(rs.getInt("total_amount"));
                vo.setTotalCount(rs.getInt("total_count"));
                list.add(vo);
            }
        } finally {
            disconnect();
        }
        
        return list;
    }
    
    /**
     * 월별 매출 통계 (최근 12개월)
     */
    public List<StasticsVO> getMonthlySales() throws SQLException {
        connect();
        List<StasticsVO> list = new ArrayList<>();
        
        String sql = "SELECT DATE_FORMAT(pay_time, '%Y-%m') as period, " +
                     "SUM(pay_log) as total_amount, COUNT(*) as total_count " +
                     "FROM pay_logs " +
                     "WHERE pay_time >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH) " +
                     "GROUP BY DATE_FORMAT(pay_time, '%Y-%m') " +
                     "ORDER BY period DESC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                StasticsVO vo = new StasticsVO();
                vo.setPeriod(rs.getString("period"));
                vo.setTotalAmount(rs.getInt("total_amount"));
                vo.setTotalCount(rs.getInt("total_count"));
                list.add(vo);
            }
        } finally {
            disconnect();
        }
        
        return list;
    }
    
    /**
     * 차종별 통계
     */
    public Map<String, Integer> getVehicleStats() throws SQLException {
        connect();
        Map<String, Integer> stats = new HashMap<>();
        
        String sql = "SELECT kind_of_discount, COUNT(*) as count " +
                     "FROM pay_logs " +
                     "WHERE pay_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                     "GROUP BY kind_of_discount";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            // 초기화
            stats.put("normal", 0);
            stats.put("light", 0);
            stats.put("disabled", 0);
            stats.put("monthly", 0);
            
            while (rs.next()) {
                String type = rs.getString("kind_of_discount");
                int count = rs.getInt("count");
                if (type != null) {
                    stats.put(type, count);
                }
            }
        } finally {
            disconnect();
        }
        
        return stats;
    }
    
    /**
     * 대시보드 요약 통계
     */
    public Map<String, Object> getSummary() throws SQLException {
        connect();
        Map<String, Object> summary = new HashMap<>();
        
        try {
            // 오늘 매출
            String todaySql = "SELECT SUM(pay_log) as amount, COUNT(*) as count " +
                             "FROM pay_logs WHERE DATE(pay_time) = CURDATE()";
            try (PreparedStatement pstmt = conn.prepareStatement(todaySql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    summary.put("todayAmount", rs.getInt("amount"));
                    summary.put("todayCount", rs.getInt("count"));
                }
            }
            
            // 이번달 매출
            String monthSql = "SELECT SUM(pay_log) as amount, COUNT(*) as count " +
                             "FROM pay_logs WHERE DATE_FORMAT(pay_time, '%Y-%m') = DATE_FORMAT(CURDATE(), '%Y-%m')";
            try (PreparedStatement pstmt = conn.prepareStatement(monthSql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    summary.put("monthAmount", rs.getInt("amount"));
                    summary.put("monthCount", rs.getInt("count"));
                }
            }
            
            // 현재 주차 중인 차량
            String parkingSql = "SELECT COUNT(*) as count FROM parking_times WHERE exit_time IS NULL";
            try (PreparedStatement pstmt = conn.prepareStatement(parkingSql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    summary.put("currentParking", rs.getInt("count"));
                }
            }
            
        } finally {
            disconnect();
        }
        
        return summary;
    }
}
