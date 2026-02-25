package com.example.parkinglot.dao;

import com.example.parkinglot.dto.MonthlyParkingDTO;
import com.example.parkinglot.util.ConnectionUtil;
import lombok.Cleanup;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// 월주차 정기권 회원 DAO 구현체
public class MonthlyParkingDAO {

    // 목록/이력 조회 쿼리에서 공통으로 사용하는 status 계산식
    // DB 컬럼이 아닌 조회 시점 날짜 기준으로 계산
    private static final String STATUS_CASE =
            "CASE WHEN expiry_date < CURDATE() THEN 'expired' " +
            "     WHEN begin_date > CURDATE()  THEN 'scheduled' " +
            "     ELSE 'active' END AS status ";

    // 월주차 회원 등록
    public void insert(MonthlyParkingDTO dto) {
        String sql = "INSERT INTO monthly_parking (plate_number, name, phone_number, begin_date, expiry_date) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, dto.getPlateNumber());
            preparedStatement.setString(2, dto.getName());
            preparedStatement.setString(3, dto.getPhoneNumber());
            preparedStatement.setDate(4, Date.valueOf(dto.getBeginDate()));
            preparedStatement.setDate(5, Date.valueOf(dto.getExpiryDate()));
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // id로 단건 조회 (수정 폼 진입 시 사용)
    // status 계산 불필요
    public MonthlyParkingDTO selectById(int id) {
        MonthlyParkingDTO monthlyParkingDTO = null;
        String sql = "SELECT * FROM monthly_parking WHERE id = ?";

        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);

            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                monthlyParkingDTO = mapToDTO(resultSet);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return monthlyParkingDTO;
    }

    // 차량번호로 가장 최근 등록 1건 조회 (연장 시 다음 시작일 계산용)
    // status 계산 불필요 - 만료일 값만 사용
    public MonthlyParkingDTO selectLatestByPlate(String plateNumber) {
        MonthlyParkingDTO monthlyParkingDTO = null;
        String sql = "SELECT * FROM monthly_parking WHERE plate_number = ? " +
                     "ORDER BY expiry_date DESC LIMIT 1";

        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, plateNumber);

            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                monthlyParkingDTO = mapToDTO(resultSet);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return monthlyParkingDTO;
    }

    // 페이징 + 정렬 목록 조회
    // sortColumn, sortOrder는 Service에서 whitelist 검증 후 전달받으므로 SQL Injection 위험 없음
    public List<MonthlyParkingDTO> selectWithPaging(int offset, int limit,
            String sortColumn, String sortOrder) {
        List<MonthlyParkingDTO> list = new ArrayList<>();
        String sql = "SELECT *, " + STATUS_CASE +
                     "FROM monthly_parking " +
                     "ORDER BY " + sortColumn + " " + sortOrder + " LIMIT ? OFFSET ?";

        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, limit);
            preparedStatement.setInt(2, offset);

            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                list.add(mapToDTOWithStatus(resultSet));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    // 검색 + 정렬 + 페이징 목록 조회
    // keyword가 null이거나 빈 문자열이면 전체 조회와 동일하게 동작
    // sortColumn, sortOrder는 Service에서 whitelist 검증 후 전달받으므로 SQL Injection 위험 없음
    public List<MonthlyParkingDTO> selectWithPagingAndSearch(int offset, int limit,
            String keyword, String sortColumn, String sortOrder) {
        List<MonthlyParkingDTO> list = new ArrayList<>();
        String like = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        String sql = "SELECT *, " + STATUS_CASE +
                     "FROM monthly_parking " +
                     "WHERE plate_number LIKE ? OR name LIKE ? OR phone_number LIKE ? " +
                     "ORDER BY " + sortColumn + " " + sortOrder + " LIMIT ? OFFSET ?";

        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, like);
            preparedStatement.setString(2, like);
            preparedStatement.setString(3, like);
            preparedStatement.setInt(4, limit);
            preparedStatement.setInt(5, offset);

            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                list.add(mapToDTOWithStatus(resultSet));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    // 차량번호로 전체 이력 조회 (시작일 오름차순)
    // 누적 기록 확인 및 연장 이력 표시용
    public List<MonthlyParkingDTO> selectAllByPlate(String plateNumber) {
        List<MonthlyParkingDTO> list = new ArrayList<>();
        String sql = "SELECT *, " + STATUS_CASE +
                     "FROM monthly_parking WHERE plate_number = ? ORDER BY begin_date ASC";

        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, plateNumber);

            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                list.add(mapToDTOWithStatus(resultSet));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    // 전체 건수 (페이징 계산용)
    public int getTotalCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM monthly_parking";

        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);

            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return count;
    }

    // 검색 조건 포함 전체 건수 (페이징 계산용)
    public int getTotalCountBySearch(String keyword) {
        int count = 0;
        String like = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        String sql = "SELECT COUNT(*) FROM monthly_parking " +
                     "WHERE plate_number LIKE ? OR name LIKE ? OR phone_number LIKE ?";

        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, like);
            preparedStatement.setString(2, like);
            preparedStatement.setString(3, like);

            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return count;
    }

    // 날짜 겹침 검사 (등록/연장 시 중복 방지)
    // 겹치는 조건: 기존 begin <= 새 end AND 기존 expiry >= 새 start
    public int countOverlap(String plateNumber, LocalDate start, LocalDate end) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM monthly_parking " +
                     "WHERE plate_number = ? AND begin_date <= ? AND expiry_date >= ?";

        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, plateNumber);
            preparedStatement.setDate(2, Date.valueOf(end));
            preparedStatement.setDate(3, Date.valueOf(start));

            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return count;
    }

    // 월정액 유효 회원 여부 확인 (오늘 날짜가 유효 기간 내에 있는지)
    // DashboardService에서 입차 시 월정액 자동 판별에 사용
    public boolean isValidMember(String plateNumber) {
        boolean isValid = false;
        String sql = "SELECT COUNT(*) FROM monthly_parking " +
                     "WHERE plate_number = ? AND begin_date <= CURDATE() AND expiry_date >= CURDATE()";

        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, plateNumber);

            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                isValid = resultSet.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return isValid;
    }

    // 회원 정보 수정
    public boolean update(MonthlyParkingDTO dto) {
        boolean result = false;
        String sql = "UPDATE monthly_parking SET name = ?, phone_number = ?, " +
                     "begin_date = ?, expiry_date = ? WHERE id = ?";

        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, dto.getName());
            preparedStatement.setString(2, dto.getPhoneNumber());
            preparedStatement.setDate(3, Date.valueOf(dto.getBeginDate()));
            preparedStatement.setDate(4, Date.valueOf(dto.getExpiryDate()));
            preparedStatement.setInt(5, dto.getId());

            result = preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    // 회원 삭제
    public void delete(int id) {
        String sql = "DELETE FROM monthly_parking WHERE id = ?";

        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // SELECT * 쿼리용 (status 컬럼 없음)
    private MonthlyParkingDTO mapToDTO(ResultSet resultSet) throws SQLException {
        return MonthlyParkingDTO.builder()
                .id(resultSet.getInt("id"))
                .plateNumber(resultSet.getString("plate_number"))
                .name(resultSet.getString("name"))
                .phoneNumber(resultSet.getString("phone_number"))
                .beginDate(resultSet.getDate("begin_date").toLocalDate())
                .expiryDate(resultSet.getDate("expiry_date").toLocalDate())
                .build();
    }

    // STATUS_CASE 포함 쿼리용 (status 컬럼 있음)
    private MonthlyParkingDTO mapToDTOWithStatus(ResultSet resultSet) throws SQLException {
        return MonthlyParkingDTO.builder()
                .id(resultSet.getInt("id"))
                .plateNumber(resultSet.getString("plate_number"))
                .name(resultSet.getString("name"))
                .phoneNumber(resultSet.getString("phone_number"))
                .beginDate(resultSet.getDate("begin_date").toLocalDate())
                .expiryDate(resultSet.getDate("expiry_date").toLocalDate())
                .status(resultSet.getString("status"))
                .build();
    }
}