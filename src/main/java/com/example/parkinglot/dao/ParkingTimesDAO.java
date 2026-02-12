package com.example.parkinglot.dao;

import com.example.parkinglot.dto.ParkingTimesDTO;
import com.example.parkinglot.util.ConnectionUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//입출차 시간 기록 DAO 구현체
public class ParkingTimesDAO{

    public void insertEntry(ParkingTimesDTO parkingTimesDTO) throws SQLException {
        String sql = "INSERT INTO parking_times (id, entry_time) VALUES (?, ?)";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, parkingTimesDTO.getId());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(parkingTimesDTO.getEntryTime()));
            preparedStatement.executeUpdate();
        }
    }

    public void updateExit(int id) throws SQLException {
        String sql = "UPDATE parking_times SET exit_time = ? WHERE id = ?";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        }

    }

    public ParkingTimesDTO selectById(int id) throws SQLException {
        String sql = "SELECT * FROM parking_times WHERE id = ?";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return ParkingTimesDTO.builder()
                        .id(resultSet.getInt("id"))
                        .entryTime(resultSet.getTimestamp("entry_time").toLocalDateTime())
                        .exitTime(resultSet.getTimestamp("exit_time") != null ? resultSet.getTimestamp("exit_time").toLocalDateTime() : null)
                        .build();
            }
            return null;
        }
    }

    public List<ParkingTimesDTO> selectCurrentParking() throws SQLException {
        String sql = "SELECT * FROM parking_times WHERE exit_time IS NULL ORDER BY entry_time DESC";
        List<ParkingTimesDTO> list = new ArrayList<>();

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                list.add(ParkingTimesDTO.builder()
                        .id(resultSet.getInt("id"))
                        .entryTime(resultSet.getTimestamp("entry_time").toLocalDateTime())
                        .exitTime(null)
                        .build());
            }
        }
        return list;
    }
}
