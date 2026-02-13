package com.example.parkinglot.dao;

import com.example.parkinglot.util.ConnectionUtil;
import lombok.Cleanup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 테스트용 가짜 데이터를 생성하고 관리하는 DAO 클래스
 */
public class TestDataDAO {

    private static final Random random = new Random();
    // 가짜 이름 생성을 위한 성/이름 배열
    private static final String[] LAST_NAMES = {"김", "이", "박", "최", "정", "강", "조", "윤", "장", "임"};
    private static final String[] FIRST_NAMES = {"민준", "서연", "지훈", "수빈", "예준", "하은", "도윤", "시우", "주원", "지우"};

    // 랜덤 차량번호 생성 (예: 12가3456)
    private String generatePlateNumber() {
        int num1 = 10 + random.nextInt(90);
        String[] regions = {"가", "나", "다", "라", "마", "바", "사"};
        String region = regions[random.nextInt(regions.length)];
        int num2 = 1000 + random.nextInt(9000);
        return num1 + region + num2;
    }

    // 랜덤 전화번호 생성
    private String generatePhoneNumber() {
        return String.format("010-%04d-%04d", random.nextInt(10000), random.nextInt(10000));
    }

    // 랜덤 한국인 성명 생성
    private String generateName() {
        return LAST_NAMES[random.nextInt(LAST_NAMES.length)] + FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
    }

    /**
     * 현재 주차되어 있는 구역 목록을 조회 (중복 주차 방지용)
     */
    private List<String> getUsedParkingSpots() {
        List<String> spots = new ArrayList<>();
        String sql = "SELECT parking_spot FROM car_info WHERE parking_spot IS NOT NULL";
        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                spots.add(resultSet.getString("parking_spot"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return spots;
    }

    /**
     * 대량 주차 데이터 입력 (입차 시뮬레이션)
     * @param count 생성할 데이터 개수 (최대 20개)
     */
    public int bulkInsertParking(int count) {
        if (count > 20) count = 20; // A01~A20 구역 제한
        List<String> usedSpots = getUsedParkingSpots();
        List<String> availableSpots = new ArrayList<>();

        // 빈 자리 찾기 (A01 ~ A20)
        for (int i = 1; i <= 20; i++) {
            String spot = String.format("A%02d", i);
            if (!usedSpots.contains(spot)) {
                availableSpots.add(spot);
            }
        }

        if (availableSpots.isEmpty()) return 0;
        Collections.shuffle(availableSpots); // 무작위 자리에 주차하기 위해 섞음
        count = Math.min(count, availableSpots.size());

        int insertedCount = 0;
        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            for (int i = 0; i < count; i++) {
                String plateNumber = generatePlateNumber();
                String parkingSpot = availableSpots.get(i);
                LocalDateTime entryTime = LocalDateTime.now().minusHours(random.nextInt(12)); // 최대 12시간 전 입차

                // 1. 차량 기본 정보 입력
                String sql1 = "INSERT INTO car_info (plate_number, parking_spot) VALUES (?, ?)";
                @Cleanup PreparedStatement pstmt1 = connection.prepareStatement(sql1, PreparedStatement.RETURN_GENERATED_KEYS);
                pstmt1.setString(1, plateNumber);
                pstmt1.setString(2, parkingSpot);
                pstmt1.executeUpdate();

                // 2. 자동 생성된 car_id 가져오기
                @Cleanup ResultSet rs = pstmt1.getGeneratedKeys();
                int carId = 0;
                if (rs.next()) {
                    carId = rs.getInt(1);
                }

                // 3. 입차 시간 정보 입력 (exit_time은 NULL)
                String sql2 = "INSERT INTO parking_times (id, entry_time, exit_time) VALUES (?, ?, NULL)";
                @Cleanup PreparedStatement pstmt2 = connection.prepareStatement(sql2);
                pstmt2.setInt(1, carId);
                pstmt2.setObject(2, entryTime);
                pstmt2.executeUpdate();

                // 4. 할인 대상 여부 랜덤 입력 (장애인 20%, 경차 30%)
                boolean isDisability = random.nextInt(10) < 2;
                boolean isCompact = random.nextInt(10) < 3;
                String sql3 = "INSERT INTO discount_info (id, is_disability_discount, is_compact_car) VALUES (?, ?, ?)";
                @Cleanup PreparedStatement pstmt3 = connection.prepareStatement(sql3);
                pstmt3.setInt(1, carId);
                pstmt3.setBoolean(2, isDisability);
                pstmt3.setBoolean(3, isCompact);
                pstmt3.executeUpdate();

                insertedCount++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return insertedCount;
    }

    /**
     * 대량 출차 처리 (출차 시뮬레이션)
     * @param count 출차시킬 차량 수
     */
    public int bulkExitParking(int count) {
        List<Integer> carIds = new ArrayList<>();
        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            // 랜덤으로 현재 주차중인 차량 ID 선택
            String selectSql = "SELECT id FROM car_info WHERE parking_spot IS NOT NULL ORDER BY RAND() LIMIT ?";
            @Cleanup PreparedStatement pstmt = connection.prepareStatement(selectSql);
            pstmt.setInt(1, count);
            @Cleanup ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                carIds.add(rs.getInt("id"));
            }
            if (carIds.isEmpty()) return 0;

            // 출차 시간 업데이트 및 주차 구역 비우기 (Batch 처리로 효율성 증대)
            String updateTimeSql = "UPDATE parking_times SET exit_time = ? WHERE id = ? AND exit_time IS NULL";
            String updateSpotSql = "UPDATE car_info SET parking_spot = NULL WHERE id = ?";

            @Cleanup PreparedStatement pstmtTime = connection.prepareStatement(updateTimeSql);
            @Cleanup PreparedStatement pstmtSpot = connection.prepareStatement(updateSpotSql);

            LocalDateTime now = LocalDateTime.now();
            for (int carId : carIds) {
                pstmtTime.setObject(1, now);
                pstmtTime.setInt(2, carId);
                pstmtTime.addBatch();

                pstmtSpot.setInt(1, carId);
                pstmtSpot.addBatch();
            }
            pstmtTime.executeBatch();
            pstmtSpot.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return carIds.size();
    }

    /**
     * 월정액 회원 대량 등록
     */
    public int bulkInsertMonthlyMembers(int count) {
        int insertedCount = 0;
        String sql = "INSERT INTO monthly_parking (plate_number, name, phone_number, begin_date, expiry_date) VALUES (?, ?, ?, ?, ?)";
        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            for (int i = 0; i < count; i++) {
                try {
                    @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1, generatePlateNumber());
                    preparedStatement.setString(2, generateName());
                    preparedStatement.setString(3, generatePhoneNumber());
                    preparedStatement.setDate(4, java.sql.Date.valueOf(LocalDate.now())); // 오늘부터
                    preparedStatement.setDate(5, java.sql.Date.valueOf(LocalDate.now().plusMonths(1))); // 한달 뒤까지
                    preparedStatement.executeUpdate();
                    insertedCount++;
                } catch (SQLException e) {
                    // 중복 차량 번호 발생 시 무시하고 다음 진행
                    continue;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return insertedCount;
    }

    /**
     * 랜덤하게 데이터 삭제 (청소용)
     */
    public int randomDeleteData(String tableName, int count) {
        String sql = "";
        switch (tableName) {
            case "monthly_parking":
                sql = "DELETE FROM monthly_parking ORDER BY RAND() LIMIT ?";
                break;
            case "fee_policy":
                // ID가 1인 기본 정책은 삭제되지 않도록 보호
                sql = "DELETE FROM fee_policy WHERE id > 1 ORDER BY RAND() LIMIT ?";
                break;
            default:
                return 0;
        }
        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, count);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 모든 주차 관련 실시간 데이터 초기화 (주의: 테이블 전체 비움)
     */
    public void clearAllParkingData() {
        String sql1 = "DELETE FROM discount_info";
        String sql2 = "DELETE FROM parking_times";
        String sql3 = "DELETE FROM car_info";
        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            // 외래키 참조 관계를 고려하여 자식 테이블부터 삭제
            @Cleanup PreparedStatement pstmt1 = connection.prepareStatement(sql1);
            pstmt1.executeUpdate();
            @Cleanup PreparedStatement pstmt2 = connection.prepareStatement(sql2);
            pstmt2.executeUpdate();
            @Cleanup PreparedStatement pstmt3 = connection.prepareStatement(sql3);
            pstmt3.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 현재 DB의 데이터 현황 요약 문자열 반환
     */
    public String getStatistics() {
        StringBuilder sb = new StringBuilder();
        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();

            // 1. 현재 주차 중인 차량
            @Cleanup PreparedStatement pstmt1 = connection.prepareStatement("SELECT COUNT(*) FROM car_info WHERE parking_spot IS NOT NULL");
            @Cleanup ResultSet rs1 = pstmt1.executeQuery();
            if (rs1.next()) {
                sb.append("현재 주차중: ").append(rs1.getInt(1)).append("대 | ");
            }

            // 2. 전체 입차 기록 수
            @Cleanup PreparedStatement pstmt2 = connection.prepareStatement("SELECT COUNT(*) FROM car_info");
            @Cleanup ResultSet rs2 = pstmt2.executeQuery();
            if (rs2.next()) {
                sb.append("전체 기록: ").append(rs2.getInt(1)).append("건 | ");
            }

            // 3. 월정액 회원 수
            @Cleanup PreparedStatement pstmt3 = connection.prepareStatement("SELECT COUNT(*) FROM monthly_parking");
            @Cleanup ResultSet rs3 = pstmt3.executeQuery();
            if (rs3.next()) {
                sb.append("월정액 회원: ").append(rs3.getInt(1)).append("명 | ");
            }

            // 4. 요금 정책 수
            @Cleanup PreparedStatement pstmt4 = connection.prepareStatement("SELECT COUNT(*) FROM fee_policy");
            @Cleanup ResultSet rs4 = pstmt4.executeQuery();
            if (rs4.next()) {
                sb.append("요금 정책: ").append(rs4.getInt(1)).append("건");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return sb.length() > 0 ? sb.toString() : "데이터가 없습니다.";
    }
}