package com.example.parkinglot.dao;

import com.example.parkinglot.util.ConnectionUtil;
import com.example.parkinglot.vo.CarInfoVO;
import lombok.Cleanup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

//차량 기본 정보 DAO 구현체
public class CarInfoDAO {

    public void insert(CarInfoVO carInfoVO){
        String sql = "insert into car_info (plate_number, parking_spot) " +
                "VALUES (?,?) ";

        try {
            //connection 연결해서 아이디 값 찾아오기
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, carInfoVO.getPlateNumber());
            preparedStatement.setString(2, carInfoVO.getParkingSpot());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CarInfoVO selectByPlateNumber(String plateNumber){
        CarInfoVO carInfoVO = null;
        String sql = "select * from car_info where plate_number = ? order by id desc limit 1";

        try {
            //connection 연결해서 아이디 값 찾아오기
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, plateNumber);

            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                carInfoVO = CarInfoVO.builder()
                        .id(resultSet.getInt("id"))
                        .plateNumber(resultSet.getString("plate_number"))
                        .parkingSpot(resultSet.getString("parking_spot"))
                        .build();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return carInfoVO;
    }

    public CarInfoVO selectById(int id) {
        CarInfoVO carInfoVO = null;
        String sql = "select * from car_info where id = ?";

        try {
            //connection 연결해서 아이디 값 찾아오기
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);

            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                carInfoVO = CarInfoVO.builder()
                        .id(resultSet.getInt("id"))
                        .plateNumber(resultSet.getString("plate_number"))
                        .parkingSpot(resultSet.getString("parking_spot"))
                        .build();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return carInfoVO;
    }
}
