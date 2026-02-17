package com.example.parkinglot.dao;

import com.example.parkinglot.dto.MonthlyParkingDTO;
import com.example.parkinglot.util.ConnectionUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//월주차 정기권 회원 DAO구현체
public class MonthlyParkingDAO {

    //월주차 회원 등록
    public void insert(MonthlyParkingDTO monthlyParkingDTO) throws SQLException {
        String sql = "INSERT INTO monthly_parking (plate_number, name, phone_number, begin_date, expiry_date) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, monthlyParkingDTO.getPlateNumber());
            preparedStatement.setString(2, monthlyParkingDTO.getName());
            preparedStatement.setString(3, monthlyParkingDTO.getPhoneNumber());
            preparedStatement.setDate(4, Date.valueOf(monthlyParkingDTO.getBeginDate()));
            preparedStatement.setDate(5, Date.valueOf(monthlyParkingDTO.getExpiryDate()));
            preparedStatement.executeUpdate();
        }
    }

    //id 로 회원 조회
    public MonthlyParkingDTO selectById(int id) throws SQLException {
        String sql = "SELECT * FROM monthly_parking WHERE id = ?";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapToDTO(resultSet);
            }
            return null;
        }
    }

    //차량번호로 회원 조회
    public MonthlyParkingDTO selectByPlateNumber(String plateNumber) throws SQLException {
        String sql = "SELECT * FROM monthly_parking WHERE plate_number = ?";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, plateNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapToDTO(resultSet);
            }
            return null;
        }
    }

    //전체 회원 목록
    public List<MonthlyParkingDTO> selectAll() throws SQLException {
        String sql = "SELECT * FROM monthly_parking ORDER BY expiry_date DESC";
        List<MonthlyParkingDTO> list = new ArrayList<>();

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                list.add(mapToDTO(resultSet));
            }
            return list;
        }
    }

    // 페이징 처리된 목록 조회 (정렬 조건 포함)
    // sortColumn, sortOrder 는 Service 에서 whitelist 검증 후 전달받으므로 SQL Injection 위험 없음
    public List<MonthlyParkingDTO> selectWithPaging(int offset, int limit, String sortColumn, String sortOrder) throws SQLException {
        String sql = "SELECT * FROM monthly_parking ORDER BY " + sortColumn + " " + sortOrder + " LIMIT ? OFFSET ?";
        List<MonthlyParkingDTO> list = new ArrayList<>();

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, limit);
            preparedStatement.setInt(2, offset);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                list.add(mapToDTO(resultSet));
            }
            return list;
        }
    }

    // 전체 회원 수 조회 (페이징 계산용)
    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM monthly_parking";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        }
    }

    // 검색 조건 + 정렬 조건 포함 페이징 목록 조회
    // keyword 가 null 이거나 빈 문자열이면 전체 조회와 동일하게 동작
    // sortColumn, sortOrder 는 Service 에서 whitelist 검증 후 전달받으므로 SQL Injection 위험 없음
    public List<MonthlyParkingDTO> selectWithPagingAndSearch(int offset, int limit, String keyword, String sortColumn, String sortOrder) throws SQLException {
        String like = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        String sql = "SELECT * FROM monthly_parking " +
                "WHERE plate_number LIKE ? OR name LIKE ? OR phone_number LIKE ? " +
                "ORDER BY " + sortColumn + " " + sortOrder + " LIMIT ? OFFSET ?";
        List<MonthlyParkingDTO> list = new ArrayList<>();

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, like);
            preparedStatement.setString(2, like);
            preparedStatement.setString(3, like);
            preparedStatement.setInt(4, limit);
            preparedStatement.setInt(5, offset);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                list.add(mapToDTO(resultSet));
            }
            return list;
        }
    }

    // 검색 조건 포함 전체 건수 조회 (페이징 계산용)
    public int getTotalCountBySearch(String keyword) throws SQLException {
        String like = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        String sql = "SELECT COUNT(*) FROM monthly_parking " +
                "WHERE plate_number LIKE ? OR name LIKE ? OR phone_number LIKE ?";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, like);
            preparedStatement.setString(2, like);
            preparedStatement.setString(3, like);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        }
    }

    //월정액 회원 유효성 확인
    public boolean isValidMember(String plateNumber) throws SQLException {
        String sql = "SELECT expiry_date FROM monthly_parking WHERE plate_number = ?";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, plateNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                LocalDate expireDate = resultSet.getDate("expiry_date").toLocalDate();
                return !expireDate.isBefore(LocalDate.now());
            }
            return false;
        }
    }

    //회원 정보 수정
    public boolean update(MonthlyParkingDTO monthlyParkingDTO) throws SQLException {
        String sql = "UPDATE monthly_parking SET name = ?, phone_number = ?, " +
                "begin_date = ?, expiry_date = ? WHERE id = ?";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, monthlyParkingDTO.getName());
            preparedStatement.setString(2, monthlyParkingDTO.getPhoneNumber());
            preparedStatement.setDate(3, Date.valueOf(monthlyParkingDTO.getBeginDate()));
            preparedStatement.setDate(4, Date.valueOf(monthlyParkingDTO.getExpiryDate()));
            preparedStatement.setInt(5, monthlyParkingDTO.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    //회원 삭제
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM monthly_parking WHERE id = ?";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    //매핑 헬퍼 메서드
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
}