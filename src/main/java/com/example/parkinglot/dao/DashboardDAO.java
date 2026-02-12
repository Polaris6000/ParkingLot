package com.example.parkinglot.dao;

import com.example.parkinglot.vo.DashboardStatsVO;
import com.example.parkinglot.vo.ParkingSpotVO;
import lombok.Cleanup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 대시보드 화면에 필요한 데이터를 조회하는 DAO
 */
public class DashboardDAO {

    /**
     * 대시보드 통계 정보 조회 (현재 주차 수, 오늘 방문자 수)
     */
    public DashboardStatsVO getStats(Connection conn) throws Exception {

        // 현재 주차중인 차량 수 조회 (parking_spot이 NULL이 아닌 것)
        String sql1 = "SELECT COUNT(*) FROM car_info WHERE parking_spot IS NOT NULL";

        // 오늘 입차한 차량 수 조회
        String sql2 = "SELECT COUNT(*) FROM parking_times WHERE DATE(entry_time) = ?";

        int currentParked = 0;
        int todayVisitors = 0;

        // 현재 주차 수 조회
        @Cleanup PreparedStatement pstmt1 = conn.prepareStatement(sql1);
        @Cleanup ResultSet rs1 = pstmt1.executeQuery();
        if (rs1.next()) {
            currentParked = rs1.getInt(1);
        }

        // 오늘 방문자 수 조회
        @Cleanup PreparedStatement pstmt2 = conn.prepareStatement(sql2);
        pstmt2.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
        @Cleanup ResultSet rs2 = pstmt2.executeQuery();
        if (rs2.next()) {
            todayVisitors = rs2.getInt(1);
        }

        return DashboardStatsVO.builder()
                .currentParkedCount(currentParked)
                .totalSpots(20)  // 전체 주차 면수는 20으로 고정
                .todayVisitorCount(todayVisitors)
                .build();
    }

    /**
     * 전체 주차 구역(A01 ~ A20) 상태 조회
     * DB에 저장된 형식: A01, A02, ... A20 (하이픈 없음)
     */
    public List<ParkingSpotVO> getAllParkingSpots(Connection conn) throws Exception {
        List<ParkingSpotVO> spots = new ArrayList<>();

        // 현재 주차중인 차량 정보 조회
        String sql = "SELECT c.id, c.plate_number, c.parking_spot, p.entry_time " +
                "FROM car_info c " +
                "LEFT JOIN parking_times p ON c.id = p.id " +
                "WHERE c.parking_spot IS NOT NULL";

        @Cleanup PreparedStatement pstmt = conn.prepareStatement(sql);
        @Cleanup ResultSet rs = pstmt.executeQuery();

        // 점유된 주차 구역 목록
        List<ParkingSpotVO> occupiedSpots = new ArrayList<>();
        while (rs.next()) {
            occupiedSpots.add(ParkingSpotVO.builder()
                    .spotNumber(rs.getString("parking_spot"))
                    .occupied(true)
                    .carId(rs.getInt("id"))
                    .plateNumber(rs.getString("plate_number"))
                    .entryTime(rs.getTimestamp("entry_time") != null
                            ? rs.getTimestamp("entry_time").toLocalDateTime()
                            : null)
                    .build());
        }

        // 전체 20개 구역 생성 (A01 ~ A20 형식, 하이픈 없음)
        for (int i = 1; i <= 20; i++) {
            String spotNumber = String.format("A%02d", i);  // A01, A02, ...

            // 해당 구역이 점유되어 있는지 확인
            ParkingSpotVO occupiedSpot = null;
            for (ParkingSpotVO spot : occupiedSpots) {
                if (spot.getSpotNumber().trim().equals(spotNumber)) {
                    occupiedSpot = spot;
                    break;
                }
            }

            // 점유된 구역이면 해당 정보, 빈 구역이면 기본 정보 추가
            if (occupiedSpot != null) {
                spots.add(occupiedSpot);
            } else {
                spots.add(ParkingSpotVO.builder()
                        .spotNumber(spotNumber)
                        .occupied(false)
                        .build());
            }
        }
        return spots;
    }

    /**
     * 차량번호로 주차 위치 검색 (부분 일치 지원)
     */
    public ParkingSpotVO searchByPlateNumber(Connection conn, String searchKeyword) throws Exception {

        String sql = "SELECT c.id, c.plate_number, c.parking_spot, p.entry_time " +
                "FROM car_info c " +
                "LEFT JOIN parking_times p ON c.id = p.id " +
                "WHERE c.parking_spot IS NOT NULL " +
                "AND c.plate_number LIKE ?";

        @Cleanup PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, "%" + searchKeyword + "%");
        @Cleanup ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return ParkingSpotVO.builder()
                    .spotNumber(rs.getString("parking_spot"))
                    .occupied(true)
                    .carId(rs.getInt("id"))
                    .plateNumber(rs.getString("plate_number"))
                    .entryTime(rs.getTimestamp("entry_time") != null
                            ? rs.getTimestamp("entry_time").toLocalDateTime()
                            : null)
                    .build();
        }

        return null; // 검색 결과 없음
    }
}