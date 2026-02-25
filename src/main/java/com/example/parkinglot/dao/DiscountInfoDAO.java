package com.example.parkinglot.dao;

import com.example.parkinglot.util.ConnectionUtil;
import com.example.parkinglot.vo.DiscountInfoVO;
import lombok.Cleanup;

import java.sql.*;

// 차량별 할인 정보 DAO 구현체
public class DiscountInfoDAO {

    // 할인 정보 등록
    public void insert(DiscountInfoVO discountInfoVO) {
        String sql = "INSERT INTO discount_info (id, kind) VALUES (?, ?)";

        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, discountInfoVO.getId());
            preparedStatement.setString(2, discountInfoVO.getKind());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // id로 할인 정보 조회
    public DiscountInfoVO selectById(int id) {
        DiscountInfoVO discountInfoVO = null;
        String sql = "SELECT * FROM discount_info WHERE id = ?";

        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);

            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                discountInfoVO = DiscountInfoVO.builder()
                        .id(resultSet.getInt("id"))
                        .kind(resultSet.getString("kind"))
                        .build();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return discountInfoVO;
    }
}