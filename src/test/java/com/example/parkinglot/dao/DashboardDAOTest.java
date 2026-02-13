package com.example.parkinglot.dao;

import com.example.parkinglot.vo.DashboardStatsVO;
import com.example.parkinglot.vo.ParkingSpotVO;
import com.example.parkinglot.util.ConnectionUtil;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
class DashboardDAOTest {
private DashboardDAO dao;

    @BeforeEach
    void setUp() {
        dao = new DashboardDAO();
    }

    @Test
    @DisplayName("통계 데이터 조회 테스트: 현재 주차 수와 오늘 방문자 수가 0 이상이어야 함")
    void testGetStats() throws Exception {
        // given
        try (Connection conn = ConnectionUtil.INSTANCE.getConnection()) {
            // when
            DashboardStatsVO stats = dao.getStats(conn);

            // then
            log.info("조회된 통계: " + stats);
            assertNotNull(stats);
            assertTrue(stats.getTotalSpots() >= 20, "기본 주차 면수는 20이어야 함");
            assertTrue(stats.getCurrentParkedCount() >= 0);
            assertTrue(stats.getTodayVisitorCount() >= 0);
        }
    }

    @Test
    @DisplayName("전체 주차 구역 조회 테스트: 결과 리스트가 비어있지 않아야 함")
    void testGetAllParkingSpots() throws Exception {
        // given
        try (Connection conn = ConnectionUtil.INSTANCE.getConnection()) {
            // when
            List<ParkingSpotVO> spots = dao.getAllParkingSpots(conn);

            // then
            log.info("조회된 주차 구역 수: " + spots.size());
            assertNotNull(spots);
            assertFalse(spots.isEmpty(), "주차 구역 리스트가 비어있으면 안 됨");

            // 데이터가 있다면 첫 번째 요소 확인
            if (!spots.isEmpty()) {
                log.info("첫 번째 구역 정보: " + spots.get(0));
                assertNotNull(spots.get(0).getSpotNumber());
            }
        }
    }
}