package com.exmple.parkinglot;

import com.exmple.parkinglot.model.DBConnection;
import com.exmple.parkinglot.model.ParkingService;
import com.exmple.parkinglot.model.ParkingServiceImpl;
import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Log4j2
public class FeeTest {
    public static void main(String[] args) {
        ParkingService parkingService = new ParkingServiceImpl();

        log.info("========== [주차 요금 정책 정밀 검증 시작] ==========");

        // case 1. 경차 + 80분 주차 (1시간 20분)
        // 계산: 기본(2,000) + 추가 30분 1회(1,000) = 3,000원 -> 경차 30% 할인 = 2,100원 예상
        runFeeTest(parkingService, "A-01", "33다 3333", "경차", 80, "경차 80분 주차 테스트");

        // case 2. 일반차량 + 150분 주차 (2시간 30분)
        // 계산: 기본(2,000) + 추가 30분 3회(3,000) = 5,000원 예상
        runFeeTest(parkingService, "A-02", "55마 5555", "일반승용차", 150, "일반차 150분 주차 테스트");

        // case 3. 월정액 회원 + 5시간 주차
        // 예상: 무조건 0원
        runFeeTest(parkingService, "A-03", "11가 1111", "일반승용차", 300, "월정액 회원 무료 테스트");

        // case 4. 일반차량 + 24시간 주차 (일일 최대 요금)
        // 계산: 원금은 48,000원 이상이지만 15,000원 캡(Cap) 적용 예상
        runFeeTest(parkingService, "A-04", "77사 7777", "일반승용차", 1440, "일일 최대 요금 테스트");

        log.info("========== [모든 요금 테스트 종료] ==========");
    }

    private static void runFeeTest(ParkingService parkingService, String spotId, String carNumber, String carType, int pastMinutes, String testName) {
        log.info("------------------------------------------------");
        log.info(">>> [{}] 실행", testName);

        // 1. 입차 처리
        parkingService.processEntry(spotId, carNumber, carType);

        // 2. DB 시간 조작 (방금 입차한 기록의 시간을 과거로 돌림)
        int recordId = manipulateEntryTime(carNumber, pastMinutes);

        // 3. 출차 처리 및 결과 확인
        if (recordId > 0) {
            int finalFee = parkingService.processExit(recordId);
            log.info("=> 결과: {}분 주차 시 최종 정산 금액 = {}원", pastMinutes, finalFee);
        } else {
            log.error("=> 테스트 실패: 기록을 찾을 수 없음");
        }
    }

    // 테스트를 위해 DB의 입차 시간을 강제로 과거로 변경하는 메서드
    private static int manipulateEntryTime(String carNumber, int minutesAgo) {
        String updateSql = "UPDATE parking_records SET entry_time = DATE_SUB(NOW(), INTERVAL ? MINUTE) WHERE car_number = ? AND exit_time IS NULL";
        String selectSql = "SELECT record_id FROM parking_records WHERE car_number = ? AND exit_time IS NULL ORDER BY record_id DESC LIMIT 1";

        int recordId = 0;
        try {
            @Cleanup Connection connection = DBConnection.getConnection();
            // 시간 조작
            @Cleanup PreparedStatement updatePstmt = connection.prepareStatement(updateSql);
            updatePstmt.setInt(1, minutesAgo);
            updatePstmt.setString(2, carNumber);
            updatePstmt.executeUpdate();

            // 조작된 기록의 ID 가져오기
            @Cleanup PreparedStatement selectPstmt = connection.prepareStatement(selectSql);
            selectPstmt.setString(1, carNumber);
            @Cleanup ResultSet resultSet = selectPstmt.executeQuery();
            if (resultSet.next()) {
                recordId = resultSet.getInt("record_id");
            }
        } catch (Exception e) {
            log.error("시간 조작 중 오류 발생: {}", e.getMessage());
        }
        return recordId;
    }
}