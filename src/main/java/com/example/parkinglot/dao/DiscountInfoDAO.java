package com.example.parkinglot.dao;

import com.example.parkinglot.dto.DiscountInfoDTO;
import com.example.parkinglot.util.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//차량별 할인 정보 DAO구현체
public class DiscountInfoDAO{
    public void insert(DiscountInfoDTO discountInfoDTO) throws SQLException {
        String sql = "INSERT INTO discount_info (id, is_disability_discount, is_compact_car) " +
                "VALUES (?, ?, ?)";

        try(Connection connection = ConnectionUtil.INSTANCE.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setInt(1, discountInfoDTO.getId());
            preparedStatement.setString(2, discountInfoDTO.getIsDisabilityDiscount());
            preparedStatement.setString(3,discountInfoDTO.getIsCompactCar());
            preparedStatement.executeUpdate();
        }
    }

    public DiscountInfoDTO selectId(int id) throws SQLException {
        String sql = "SELECT * FROM discount_info WHERE id = ?";

        try(Connection connection = ConnectionUtil.INSTANCE.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                return DiscountInfoDTO.builder()
                        .id(resultSet.getInt("id"))
                        .isDisabilityDiscount(resultSet.getString("is_disability_discount"))
                        .isCompactCar(resultSet.getString("is_compact_car"))
                        .build();
            }
            return null;
        }
    }

    public void update(DiscountInfoDTO discountInfoDTO) throws SQLException {
        String sql = "UPDATE discount_info SET is_disability_discount = ?, is_compact_car = ? " +
                "WHERE id = ?";

        try(Connection connection = ConnectionUtil.INSTANCE.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setString(1, discountInfoDTO.getIsDisabilityDiscount());
            preparedStatement.setString(2, discountInfoDTO.getIsCompactCar());
            preparedStatement.setInt(3, discountInfoDTO.getId());
            preparedStatement.executeUpdate();
        }
    }
}
