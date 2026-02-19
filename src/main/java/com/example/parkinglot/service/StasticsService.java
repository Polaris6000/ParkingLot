package com.example.parkinglot.service;

import com.example.parkinglot.dao.*;
import com.example.parkinglot.util.ConnectionUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


// 통계 관련 비즈니스 로직 처리 서비스

public class StasticsService {


    // 일일 매출 통계

    public int getDailySales(LocalDate date) throws SQLException {
        String sql = "SELECT SUM(CASE " +
                "  WHEN m.plate_number IS NOT NULL THEN 0 " +
                "  ELSE (계산로직) " +
                "END) as total " +
                "FROM car_info c " +
                "JOIN parking_times pt ON c.id = pt.id " +
                "LEFT JOIN monthly_parking m ON c.plate_number = m.plate_number " +
                "WHERE DATE(pt.exit_time) = ?";

        try (Connection conn = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        }
    }


     //현재 주차 차량 수

    public int getCurrentParkingCount() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM parking_times WHERE exit_time IS NULL";

        try (Connection conn = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        }
    }


     // 일일 입출차 현황

    public Map<String, Integer> getDailyStats(LocalDate date) throws SQLException {
        Map<String, Integer> stats = new HashMap<>();

        // 입차 수
        String entrySql = "SELECT COUNT(*) as count FROM parking_times WHERE DATE(entry_time) = ?";
        try (Connection conn = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(entrySql)) {

            pstmt.setDate(1, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                stats.put("entryCount", rs.getInt("count"));
            }
        }

        // 출차 수
        String exitSql = "SELECT COUNT(*) as count FROM parking_times WHERE DATE(exit_time) = ?";
        try (Connection conn = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(exitSql)) {

            pstmt.setDate(1, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                stats.put("exitCount", rs.getInt("count"));
            }
        }

        return stats;
    }


    //월주차 회원 수

    public int getActiveMemberCount() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM monthly_parking WHERE expiry_date >= CURDATE()";

        try (Connection conn = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        }
    }
}