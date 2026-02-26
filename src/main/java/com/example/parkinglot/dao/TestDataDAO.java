package com.example.parkinglot.dao;

import lombok.Cleanup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TestDataDAO {

    private static final Random random = new Random();

    // 한글 성씨 목록
    private static final String[] LAST_NAMES = {"김", "이", "박", "최", "정", "강", "조", "윤", "장", "임"};
    private static final String[] FIRST_NAMES = {"민준", "서연", "지훈", "수빈", "예준", "하은", "도윤", "시우", "주원", "지우"};

    // 차량번호 생성 (예: 12가3456)
    private String generatePlateNumber() {
        int num1 = 10 + random.nextInt(90); // 10-99
        String[] regions = {"가", "나", "다", "라", "마", "바", "사"};
        String region = regions[random.nextInt(regions.length)];
        int num2 = 1000 + random.nextInt(9000); // 1000-9999
        return num1 + region + num2;
    }

    // 전화번호 생성
    private String generatePhoneNumber() {
        return String.format("010-%04d-%04d",
                random.nextInt(10000),
                random.nextInt(10000));
    }

    // 이름 생성
    private String generateName() {
        return LAST_NAMES[random.nextInt(LAST_NAMES.length)] +
                FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
    }

    // 1. 대량 입차 데이터 생성 (최대 20개, 중복 위치 방지)
    public int bulkInsertParking(Connection conn, int count) throws Exception {
        if (count > 20) count = 20;

        // 현재 사용중인 주차 구역 조회
        List<String> usedSpots = getUsedParkingSpots(conn);

        // 사용 가능한 구역 목록 생성
        List<String> availableSpots = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            String spot = String.format("A%02d", i);
            if (!usedSpots.contains(spot)) {
                availableSpots.add(spot);
            }
        }

        if (availableSpots.isEmpty()) {
            return 0; // 더 이상 입차 불가
        }


        // [핵심 추가] 리스트의 순서를 무작위로 섞음
        Collections.shuffle(availableSpots);

        // 실제 생성 가능한 수량 조정
        // 이제 get(i)를 해도 랜덤한 자리가 뽑힘.
        count = Math.min(count, availableSpots.size());

        int insertedCount = 0;

        for (int i = 0; i < count; i++) {
            String plateNumber = generatePlateNumber();
            String parkingSpot = availableSpots.get(i);
            LocalDateTime entryTime = LocalDateTime.now().minusHours(random.nextInt(12)); // 0-12시간 전

            // 1) car_info 테이블에 입력
            String sql1 = "INSERT INTO car_info (plate_number, parking_spot) VALUES (?, ?)";
            @Cleanup PreparedStatement pstmt1 = conn.prepareStatement(sql1, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt1.setString(1, plateNumber);
            pstmt1.setString(2, parkingSpot);
            pstmt1.executeUpdate();

            // 생성된 ID 가져오기
            @Cleanup ResultSet rs = pstmt1.getGeneratedKeys();
            int carId = 0;
            if (rs.next()) {
                carId = rs.getInt(1);
            }

            // 2) parking_times 테이블에 입력
            String sql2 = "INSERT INTO parking_times (id, entry_time, exit_time) VALUES (?, ?, NULL)";
            @Cleanup PreparedStatement pstmt2 = conn.prepareStatement(sql2);
            pstmt2.setInt(1, carId);
            pstmt2.setObject(2, entryTime);
            pstmt2.executeUpdate();

            // 3) discount_info 테이블에 입력 (ENUM 기반 랜덤 할인 정보)
            String kind;
            int rand = random.nextInt(100); // 100분율로 계산하는 게 확률 조정하기 편함

            if (rand < 20) {
                kind = "disabled"; // 20% 확률
            } else if (rand < 50) {
                kind = "light";    // 30% 확률 (20~49)
            } else {
                kind = "normal";   // 나머지 50%는 일반
            }

            String sql3 = "INSERT INTO discount_info (id, kind) VALUES (?, ?)";
            @Cleanup PreparedStatement pstmt3 = conn.prepareStatement(sql3);
            pstmt3.setInt(1, carId);
            pstmt3.setString(2, kind);
            pstmt3.executeUpdate();

            insertedCount++;
        }

        return insertedCount;
    }

    // 사용중인 주차 구역 목록 조회
    private List<String> getUsedParkingSpots(Connection conn) throws Exception {
        List<String> spots = new ArrayList<>();
        String sql = "SELECT parking_spot FROM car_info WHERE parking_spot IS NOT NULL";
        @Cleanup PreparedStatement pstmt = conn.prepareStatement(sql);
        @Cleanup ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            spots.add(rs.getString("parking_spot"));
        }
        return spots;
    }

    // 2. 대량 출차 처리 (랜덤 또는 전체)
    public int bulkExitParking(Connection conn, int count) throws Exception {
        // 1. 랜덤하게 출차할 차량 ID 조회
        String selectSql = "SELECT id FROM car_info WHERE parking_spot IS NOT NULL ORDER BY RAND() LIMIT ?";
        List<Integer> carIds = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
            pstmt.setInt(1, count);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    carIds.add(rs.getInt("id"));
                }
            }
        }

        if (carIds.isEmpty()) return 0;

        // 2. 일괄 업데이트 (Batch Update) 준비
        String updateTimeSql = "UPDATE parking_times SET exit_time = ? WHERE id = ? AND exit_time IS NULL";
        String updateSpotSql = "UPDATE car_info SET parking_spot = NULL WHERE id = ?";

        try (PreparedStatement pstmtTime = conn.prepareStatement(updateTimeSql);
             PreparedStatement pstmtSpot = conn.prepareStatement(updateSpotSql)) {

            LocalDateTime now = LocalDateTime.now();

            for (int carId : carIds) {
                // 시간 업데이트 배치 추가
                pstmtTime.setObject(1, now);
                pstmtTime.setInt(2, carId);
                pstmtTime.addBatch();

                // 구역 비우기 배치 추가
                pstmtSpot.setInt(1, carId);
                pstmtSpot.addBatch();
            }

            // 한 번에 실행
            pstmtTime.executeBatch();
            pstmtSpot.executeBatch();
        }

        return carIds.size();
    }

    // 3. 월정액 회원 대량 등록
    public int bulkInsertMonthlyMembers(Connection conn, int count) throws Exception {
        int insertedCount = 0;

        String sql = "INSERT INTO monthly_parking (plate_number, name, phone_number, begin_date, expiry_date) " +
                "VALUES (?, ?, ?, ?, ?)";

        for (int i = 0; i < count; i++) {
            try {
                @Cleanup PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, generatePlateNumber());
                pstmt.setString(2, generateName());
                pstmt.setString(3, generatePhoneNumber());
                pstmt.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
                pstmt.setDate(5, java.sql.Date.valueOf(LocalDate.now().plusMonths(1)));
                pstmt.executeUpdate();
                insertedCount++;
            } catch (Exception e) {
                // 중복 차량번호 무시
                continue;
            }
        }

        return insertedCount;
    }

    // 4. 요금 정책 대량 등록 (히스토리)
    public int bulkInsertFeePolicies(Connection conn, int count) throws Exception {
        int insertedCount = 0;

        String sql = "INSERT INTO fee_policy (base_fee, basic_unit_minute, unit_fee, billing_unit_minutes, " +
                "help_discount_rate, compact_discount_rate, grace_period_minutes, max_cap_amount) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        for (int i = 0; i < count; i++) {
            @Cleanup PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, 2000 + random.nextInt(50) * 100); // 2000-6500
            pstmt.setInt(2, 60); // 기본 60분
            pstmt.setInt(3, 500 + random.nextInt(10) * 100); // 500-1500
            pstmt.setInt(4, 10 + random.nextInt(5) * 10); // 10-60
            pstmt.setInt(5, 30 + random.nextInt(5) * 10); // 30-70
            pstmt.setInt(6, 20 + random.nextInt(5) * 5); // 20-40
            pstmt.setInt(7, 5 + random.nextInt(4) * 5); // 5-20
            pstmt.setInt(8, 10000 + random.nextInt(5) * 5000); // 10000-30000
            pstmt.executeUpdate();
            insertedCount++;
        }

        return insertedCount;
    }

    // 5. 랜덤 데이터 삭제
    public int randomDeleteData(Connection conn, String tableName, int count) throws Exception {
        String sql = "";

        switch (tableName) {
            case "monthly_parking":
                sql = "DELETE FROM monthly_parking ORDER BY RAND() LIMIT ?";
                break;
            case "fee_policy":
                sql = "DELETE FROM fee_policy WHERE id > 1 ORDER BY RAND() LIMIT ?"; // id=1은 보존
                break;
            default:
                return 0;
        }

        @Cleanup PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, count);
        return pstmt.executeUpdate();
    }

    // 6. 전체 주차 데이터 초기화
    public void clearAllParkingData(Connection conn) throws Exception {
        String sql1 = "DELETE FROM discount_info";
        String sql2 = "DELETE FROM parking_times";
        String sql3 = "DELETE FROM car_info";

        @Cleanup PreparedStatement pstmt1 = conn.prepareStatement(sql1);
        pstmt1.executeUpdate();

        @Cleanup PreparedStatement pstmt2 = conn.prepareStatement(sql2);
        pstmt2.executeUpdate();

        @Cleanup PreparedStatement pstmt3 = conn.prepareStatement(sql3);
        pstmt3.executeUpdate();
    }

    // 통계 정보 조회
    public String getStatistics(Connection conn) throws Exception {
        StringBuilder sb = new StringBuilder();

        // 1. 현재 주차중인 차량 수
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM car_info WHERE parking_spot IS NOT NULL");
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) sb.append("현재 주차중: ").append(rs.getInt(1)).append("대 | ");
        }

        // 2. 전체 차량 기록 수
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM car_info");
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) sb.append("전체 기록: ").append(rs.getInt(1)).append("건 | ");
        }

        // 3. 월정액 회원 수
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM monthly_parking");
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) sb.append("월정액 회원: ").append(rs.getInt(1)).append("명 | ");
        }

        // 4. 요금 정책 수
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM fee_policy");
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) sb.append("요금 정책: ").append(rs.getInt(1)).append("건");
        }

        return sb.length() > 0 ? sb.toString() : "데이터가 없습니다.";
    }
}