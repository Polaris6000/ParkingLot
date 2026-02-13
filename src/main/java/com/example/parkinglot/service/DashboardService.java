package com.example.parkinglot.service;

import com.example.parkinglot.dao.DashboardDAO;
import com.example.parkinglot.vo.DashboardStatsVO;
import com.example.parkinglot.vo.ParkingSpotVO;
import com.example.parkinglot.util.ConnectionUtil;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.util.List;

@Log4j2
public enum DashboardService {
    INSTANCE;

    private final DashboardDAO dao;

    DashboardService() {
        dao = new DashboardDAO();
    }

    // 대시보드 통계 정보 조회
    public DashboardStatsVO getStats() throws Exception {
        try (Connection conn = ConnectionUtil.INSTANCE.getConnection()) {
            return dao.getStats(conn);
        } catch (Exception e) {
            log.error("대시보드 통계 조회 중 오류 발생", e);
            throw e;
        }
    }

    // 전체 주차 구역 상태 조회
    public List<ParkingSpotVO> getAllParkingSpots() throws Exception {
        try (Connection conn = ConnectionUtil.INSTANCE.getConnection()) {
            return dao.getAllParkingSpots(conn);
        } catch (Exception e) {
            log.error("주차 구역 조회 중 오류 발생", e);
            throw e;
        }
    }

    // 차량번호로 주차 위치 검색
    public ParkingSpotVO searchByPlateNumber(String searchKeyword) throws Exception {
        try (Connection conn = ConnectionUtil.INSTANCE.getConnection()) {
            return dao.searchByPlateNumber(conn, searchKeyword);
        } catch (Exception e) {
            log.error("차량번호 검색 중 오류 발생: {}", searchKeyword, e);
            throw e;
        }
    }
}