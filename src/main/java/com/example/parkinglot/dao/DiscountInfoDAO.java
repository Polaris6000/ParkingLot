package com.example.parkinglot.dao;

import com.example.parkinglot.util.ConnectionUtil;
import com.example.parkinglot.vo.DiscountInfoVO;
import lombok.Cleanup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//차량별 할인 정보 DAO구현체
public class DiscountInfoDAO{
    public void insert(DiscountInfoVO discountInfoVO){
        String sql = "insert into discount_info (id, is_disability_discount, is_compact_car) " +
                "values (?, ?, ?) ";

        try {
            //connection 연결해서 아이디 값 찾아오기
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,discountInfoVO.getId());
            preparedStatement.setBoolean(2, discountInfoVO.isDisabilityDiscount());
            preparedStatement.setBoolean(3, discountInfoVO.isCompactCar());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public DiscountInfoVO selectById(int id) {
        DiscountInfoVO discountInfoVO = null;
        String sql = "select * from discount_info where id = ?";

        try {
            //connection 연결해서 아이디 값 찾아오기
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);

            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                discountInfoVO = DiscountInfoVO.builder()
                        .id(resultSet.getInt("id"))
                        .disabilityDiscount(resultSet.getBoolean("is_disability_discount"))
                        .compactCar(resultSet.getBoolean("is_compact_car"))
                        .build();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return discountInfoVO;
    }

    public void update(DiscountInfoVO discountInfoVO){
        String sql = "update discount_info set is_disability_discount=?, is_compact_car = ? where id = ?";

        try {
            //connection 연결해서 아이디 값 찾아오기
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(3,discountInfoVO.getId());
            preparedStatement.setBoolean(1, discountInfoVO.isDisabilityDiscount());
            preparedStatement.setBoolean(2, discountInfoVO.isCompactCar());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
