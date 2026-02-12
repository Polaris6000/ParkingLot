package com.example.parkinglot.dao;

import com.example.parkinglot.dto.MonthlyParkingDTO;
import com.example.parkinglot.util.ConnectionUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//월주차 정기권 회원 DAO구현체
public class MonthlyParkingDAO {
    public void insert(MonthlyParkingDTO monthlyParkingDTO) throws SQLException {
        String sql = "INSERT INTO monthly_parking (plate_number, name, phone_number, begin_date, expiry_date) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, monthlyParkingDTO.getPlateNumber());
            preparedStatement.setString(2, monthlyParkingDTO.getName());
            preparedStatement.setString(3, monthlyParkingDTO.getPhoneNumber());
            preparedStatement.setString(4, String.valueOf(Date.valueOf(monthlyParkingDTO.getBeginDate())));
            preparedStatement.setString(5, String.valueOf(Date.valueOf(monthlyParkingDTO.getExpiryDate())));
            preparedStatement.executeUpdate();
        }
    }

    public MonthlyParkingDTO selectByPlateNumber(String plateNumber) throws SQLException {
        String sql = "SELECT * FROM monthly_parking WHERE plate_number = ?";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, plateNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return MonthlyParkingDTO.builder()
                        .id(resultSet.getInt("id"))
                        .plateNumber(resultSet.getString("plate_number"))
                        .name(resultSet.getString("name"))
                        .phoneNumber(resultSet.getString("phone_number"))
                        .beginDate(resultSet.getDate("begin_date").toLocalDate())
                        .expiryDate(resultSet.getDate("expiry_date").toLocalDate())
                        .build();
            }
            return null;
        }
    }

    public List<MonthlyParkingDTO> selectAll() throws SQLException {
        String sql = "SELECT * FROM monthly_parking ORDER BY expiry_date DESC";
        List<MonthlyParkingDTO> list = new ArrayList<>();

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            {

                while (resultSet.next()) {

                    list.add(MonthlyParkingDTO.builder()
                            .id(resultSet.getInt("id"))
                            .plateNumber(resultSet.getString("plate_number"))
                            .name(resultSet.getString("name"))
                            .phoneNumber(resultSet.getString("phone_number"))
                            .beginDate(resultSet.getDate("begin_date").toLocalDate())
                            .expiryDate(resultSet.getDate("expiry_date").toLocalDate())
                            .build());
                }

            }
            return list;
        }
    }

    public boolean isValidMember(String plateNumber) throws SQLException {
        String sql = "SELECT expiry_date FROM monthly_parking WHERE plate_number = ?";

        try(Connection connection = ConnectionUtil.INSTANCE.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setString(1, plateNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                LocalDate expireDate = resultSet.getDate("expiry_date").toLocalDate();
                return expireDate.isAfter(LocalDate.now());
            }
            return false;
        }
    }

    public boolean update(MonthlyParkingDTO monthlyParkingDTO) throws SQLException {
        String sql = "UPDATE monthly_parking SET name = ?, phone_number = ?, " +
                "begin_date = ?, expiry_date = ? WHERE id = ?";

        try(Connection connection = ConnectionUtil.INSTANCE.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setString(1, monthlyParkingDTO.getPlateNumber());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                LocalDate expiryDate = resultSet.getDate("expiry_date").toLocalDate();
                return  !expiryDate.isBefore(LocalDate.now());
            }
            return false;
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM monthly_parking WHERE id = ?";

        try(Connection connection = ConnectionUtil.INSTANCE.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }

    }
}
