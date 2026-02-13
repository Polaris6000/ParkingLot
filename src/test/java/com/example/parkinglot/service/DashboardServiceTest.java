package com.example.parkinglot.service;

import com.example.parkinglot.vo.DashboardStatsVO;
import com.example.parkinglot.vo.ParkingSpotVO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
class DashboardServiceTest {

    private DashboardService service = DashboardService.INSTANCE;

    @Test
    @DisplayName("서비스 통계 조회 테스트: DAO로부터 받은 데이터가 VO에 잘 담겨야 함")
    void testGetStats() throws Exception {
        // when
        DashboardStatsVO stats = service.getStats();

        // then
        log.info("Service Stats: " + stats);
        assertNotNull(stats);
        // 기본값 20이 잘 들어있는지, 데이터가 null이 아닌지 확인
        assertEquals(20, stats.getTotalSpots());
    }

    @Test
    @DisplayName("서비스 주차 구역 전체 조회 테스트: 리스트 크기가 20이어야 함")
    void testGetAllParkingSpots() throws Exception {
        // when
        List<ParkingSpotVO> spots = service.getAllParkingSpots();

        // then
        log.info("Service Parking Spots Count: " + spots.size());
        assertNotNull(spots);
        // 주차 구역은 항상 20개가 보장되어야 하므로 size 체크가 중요
        assertEquals(20, spots.size(), "주차 구역은 반드시 20개여야 합니다.");
    }
}