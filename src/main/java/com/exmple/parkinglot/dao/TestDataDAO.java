package com.exmple.parkinglot.dao;

import lombok.Cleanup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

        // 실제 생성 가능한 수량 조정
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

            // 3) discount_info 테이블에 입력 (랜덤 할인 정보)
            boolean isDisability = random.nextInt(10) < 2; // 20% 확률
            boolean isCompact = random.nextInt(10) < 3; // 30% 확률

            String sql3 = "INSERT INTO discount_info (id, is_disability_discount, is_compact_car) VALUES (?, ?, ?)";
            @Cleanup PreparedStatement pstmt3 = conn.prepareStatement(sql3);
            pstmt3.setInt(1, carId);
            pstmt3.setBoolean(2, isDisability);
            pstmt3.setBoolean(3, isCompact);
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
        // 현재 주차중인 차량 ID 조회
        String sql1 = "SELECT id FROM car_info WHERE parking_spot IS NOT NULL LIMIT ?";
        @Cleanup PreparedStatement pstmt1 = conn.prepareStatement(sql1);
        pstmt1.setInt(1, count);
        @Cleanup ResultSet rs = pstmt1.executeQuery();

        List<Integer> carIds = new ArrayList<>();
        while (rs.next()) {
            carIds.add(rs.getInt("id"));
        }

        int exitedCount = 0;

        for (int carId : carIds) {
            // parking_times 테이블의 exit_time 업데이트
            String sql2 = "UPDATE parking_times SET exit_time = ? WHERE id = ? AND exit_time IS NULL";
            @Cleanup PreparedStatement pstmt2 = conn.prepareStatement(sql2);
            pstmt2.setObject(1, LocalDateTime.now());
            pstmt2.setInt(2, carId);
            pstmt2.executeUpdate();

            // car_info 테이블의 parking_spot을 NULL로 설정
            String sql3 = "UPDATE car_info SET parking_spot = NULL WHERE id = ?";
            @Cleanup PreparedStatement pstmt3 = conn.prepareStatement(sql3);
            pstmt3.setInt(1, carId);
            pstmt3.executeUpdate();

            exitedCount++;
        }

        return exitedCount;
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
            pstmt.setInt(1, 2000 + random.nextInt(1000) * 500); // 2000-6500
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

        // 현재 주차중인 차량 수
        String sql1 = "SELECT COUNT(*) as cnt FROM car_info WHERE parking_spot IS NOT NULL";
        @Cleanup PreparedStatement pstmt1 = conn.prepareStatement(sql1);
        @Cleanup ResultSet rs1 = pstmt1.executeQuery();
        if (rs1.next()) {
            sb.append("현재 주차중: ").append(rs1.getInt("cnt")).append("대\n");
        }

        // 전체 차량 기록 수
        String sql2 = "SELECT COUNT(*) as cnt FROM car_info";
        @Cleanup PreparedStatement pstmt2 = conn.prepareStatement(sql2);
        @Cleanup ResultSet rs2 = pstmt2.executeQuery();
        if (rs2.next()) {
            sb.append("전체 차량 기록: ").append(rs2.getInt("cnt")).append("건\n");
        }

        // 월정액 회원 수
        String sql3 = "SELECT COUNT(*) as cnt FROM monthly_parking";
        @Cleanup PreparedStatement pstmt3 = conn.prepareStatement(sql3);
        @Cleanup ResultSet rs3 = pstmt3.executeQuery();
        if (rs3.next()) {
            sb.append("월정액 회원: ").append(rs3.getInt("cnt")).append("명\n");
        }

        // 요금 정책 수
        String sql4 = "SELECT COUNT(*) as cnt FROM fee_policy";
        @Cleanup PreparedStatement pstmt4 = conn.prepareStatement(sql4);
        @Cleanup ResultSet rs4 = pstmt4.executeQuery();
        if (rs4.next()) {
            sb.append("요금 정책: ").append(rs4.getInt("cnt")).append("건");
        }

        return sb.toString();
    }
}