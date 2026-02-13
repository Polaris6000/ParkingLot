package com.example.parkinglot.dao;

import com.example.parkinglot.dto.CarInfoDTO;
import com.example.parkinglot.util.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//차량 기본 정보 DAO 구현체
public class CarInfoDAO {


    public int insert(CarInfoDTO carInfoDTO) throws SQLException {
        String sql = "INSERT INTO car_info (plate_number, parking_spot) VALUES (?, ?)";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, carInfoDTO.getPlateNumber());
            preparedStatement.setString(2, carInfoDTO.getParkingSpot());
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            throw new SQLException("차량 등록 실패");
        }

    }

    public CarInfoDTO selectByPlateNumber(String plateNumber) throws SQLException {
        String sql = "SELECT * FROM car_info WHERE plate_number = ?";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, plateNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return CarInfoDTO.builder()
                        .id(resultSet.getInt("id"))
                        .plateNumber(resultSet.getString("plate_number"))
                        .parkingSpot(resultSet.getString("parking_spot"))
                        .build();
            }
            return null;
        }
    }

    public CarInfoDTO selectById(int id) throws SQLException {
        String sql = "SELECT * FROM car_info WHERE id = ?";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return CarInfoDTO.builder()
                        .id(resultSet.getInt("id"))
                        .plateNumber(resultSet.getString("plate_number"))
                        .parkingSpot(resultSet.getString("parking_spot"))
                        .build();
            }
            return null;
        }
    }

    public List<CarInfoDTO> selectAll() throws SQLException {
        String sql = "SELECT * FROM car_info ORDER BY id DESC";
        List<CarInfoDTO> list = new ArrayList<>();

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                list.add(CarInfoDTO.builder()
                        .id(resultSet.getInt("id"))
                        .plateNumber(resultSet.getString("plate_number"))
                        .parkingSpot(resultSet.getString("parking_spot"))
                        .build());
            }
        }
        return list;
    }

    public void updateParkingSpot(int id, String parkingSpot) throws SQLException {
        String sql = "UPDATE car_info SET parking_spot = ? WHERE id = ?";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, parkingSpot);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM car_info WHERE id = ?";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }
}
