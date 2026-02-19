package com.example.parkinglot.dao;

import com.example.parkinglot.util.ConnectionUtil;
import com.example.parkinglot.vo.ParkingTimesVO;
import lombok.Cleanup;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//입출차 시간 기록 DAO 구현체
public class ParkingTimesDAO {

    public void insertEntry(int id) {
        String sql = "insert into parking_times (id, entry_time) " +
                "values (?, ?) ";

        try {
            //connection 연결해서 아이디 값 찾아오기
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));


            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateExit(int id, LocalDateTime exitTime) {
        String sql = "update parking_times set exit_time = ? where id = ?";

        try {
            //connection 연결해서 아이디 값 찾아오기
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(exitTime));
            preparedStatement.setInt(2, id);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public ParkingTimesVO selectById(int id) {
        ParkingTimesVO parkingTimesVO = null;
        String sql = "select * from parking_times where id = ?";

        try {
            //connection 연결해서 아이디 값 찾아오기
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);

            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                parkingTimesVO = ParkingTimesVO.builder()
                        .id(resultSet.getInt("id"))
                        .entryTime(resultSet.getTimestamp("entry_time").toLocalDateTime())
                        .exitTime(resultSet.getTimestamp("exit_time").toLocalDateTime())
                        .build();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return parkingTimesVO;
    }

    public List<ParkingTimesVO> selectCurrentParking() {
        List<ParkingTimesVO> parkingTimesVOList = new ArrayList<>();
        String sql = "select * from parking_times where exit_time IS NULL";

        try {
            //connection 연결해서 아이디 값 찾아오기
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);

            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ParkingTimesVO parkingTimesVO = ParkingTimesVO.builder()
                        .id(resultSet.getInt("id"))
                        .entryTime(resultSet.getTimestamp("entry_time").toLocalDateTime())
                        .build();
                parkingTimesVOList.add(parkingTimesVO);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return parkingTimesVOList;
    }

    public List<ParkingTimesVO> selectTodayVisitor() {
        List<ParkingTimesVO> parkingTimesVOList = new ArrayList<>();
        String sql = "select * from parking_times where entry_time >= ? and exit_time < ?";

        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime startOfTomorrow = startOfToday.plusDays(1);

        try {
            //connection 연결해서 아이디 값 찾아오기
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(startOfToday));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(startOfTomorrow));

            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ParkingTimesVO parkingTimesVO = ParkingTimesVO.builder()
                        .id(resultSet.getInt("id"))
                        .entryTime(resultSet.getTimestamp("entry_time").toLocalDateTime())
                        .exitTime(resultSet.getTimestamp("exit_time").toLocalDateTime())
                        .build();
                parkingTimesVOList.add(parkingTimesVO);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return parkingTimesVOList;
    }
}
