package com.exmple.parkinglot.service;

import com.exmple.parkinglot.dao.TestDataDAO;
import com.exmple.parkinglot.util.ConnectionUtil;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;

@Log4j2
public enum TestDataService {
    INSTANCE;

    private TestDataDAO dao;

    TestDataService() {
        dao = new TestDataDAO();
    }

    // 대량 입차 생성
    public int bulkInsertParking(int count) throws Exception {
        try (Connection conn = ConnectionUtil.INSTANCE.getConnection()) {
            return dao.bulkInsertParking(conn, count);
        } catch (Exception e) {
            log.error("대량 입차 생성 중 오류 발생", e);
            throw e;
        }
    }

    // 대량 출차 처리
    public int bulkExitParking(int count) throws Exception {
        try (Connection conn = ConnectionUtil.INSTANCE.getConnection()) {
            return dao.bulkExitParking(conn, count);
        } catch (Exception e) {
            log.error("대량 출차 처리 중 오류 발생", e);
            throw e;
        }
    }

    // 월정액 회원 대량 등록
    public int bulkInsertMonthlyMembers(int count) throws Exception {
        try (Connection conn = ConnectionUtil.INSTANCE.getConnection()) {
            return dao.bulkInsertMonthlyMembers(conn, count);
        } catch (Exception e) {
            log.error("월정액 회원 등록 중 오류 발생", e);
            throw e;
        }
    }

    // 요금 정책 대량 등록
    public int bulkInsertFeePolicies(int count) throws Exception {
        try (Connection conn = ConnectionUtil.INSTANCE.getConnection()) {
            return dao.bulkInsertFeePolicies(conn, count);
        } catch (Exception e) {
            log.error("요금 정책 등록 중 오류 발생", e);
            throw e;
        }
    }

    // 랜덤 삭제
    public int randomDeleteData(String tableName, int count) throws Exception {
        try (Connection conn = ConnectionUtil.INSTANCE.getConnection()) {
            return dao.randomDeleteData(conn, tableName, count);
        } catch (Exception e) {
            log.error("랜덤 삭제 중 오류 발생", e);
            throw e;
        }
    }

    // 전체 주차 데이터 초기화
    public void clearAllParkingData() throws Exception {
        try (Connection conn = ConnectionUtil.INSTANCE.getConnection()) {
            dao.clearAllParkingData(conn);
        } catch (Exception e) {
            log.error("데이터 초기화 중 오류 발생", e);
            throw e;
        }
    }

    // 통계 조회
    public String getStatistics() throws Exception {
        try (Connection conn = ConnectionUtil.INSTANCE.getConnection()) {
            return dao.getStatistics(conn);
        } catch (Exception e) {
            log.error("통계 조회 중 오류 발생", e);
            throw e;
        }
    }
}