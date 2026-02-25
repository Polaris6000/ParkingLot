package com.example.parkinglot.dao;

import com.example.parkinglot.dto.FeePolicyDTO;
import com.example.parkinglot.util.ConnectionUtil;
import lombok.Cleanup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// 주차요금 정책 DAO 구현체
public class FeePolicyDAO {

    // 가장 최근 요금 정책 1건 조회
    public FeePolicyDTO selectCurrentPolicy() {
        FeePolicyDTO feePolicyDTO = null;
        String sql = "SELECT * FROM fee_policy ORDER BY update_date DESC LIMIT 1";

        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);

            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                feePolicyDTO = FeePolicyDTO.builder()
                        .id(resultSet.getInt("id"))
                        .baseFee(resultSet.getInt("base_fee"))
                        .basicUnitMinute(resultSet.getInt("basic_unit_minute"))
                        .unitFee(resultSet.getInt("unit_fee"))
                        .billingUnitMinutes(resultSet.getInt("billing_unit_minutes"))
                        .helpDiscountRate(resultSet.getInt("help_discount_rate"))
                        .compactDiscountRate(resultSet.getInt("compact_discount_rate"))
                        .gracePeriodMinutes(resultSet.getInt("grace_period_minutes"))
                        .maxCapAmount(resultSet.getInt("max_cap_amount"))
                        .monthlyPay(resultSet.getInt("monthly_pay"))
                        .updateDate(resultSet.getTimestamp("update_date").toLocalDateTime())
                        .build();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return feePolicyDTO;
    }

    // 요금 정책 신규 등록
    public void insert(FeePolicyDTO feePolicyDTO) {
        String sql = "INSERT INTO fee_policy (base_fee, basic_unit_minute, unit_fee, " +
                     "billing_unit_minutes, help_discount_rate, compact_discount_rate, " +
                     "grace_period_minutes, max_cap_amount, monthly_pay) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, feePolicyDTO.getBaseFee());
            preparedStatement.setInt(2, feePolicyDTO.getBasicUnitMinute());
            preparedStatement.setInt(3, feePolicyDTO.getUnitFee());
            preparedStatement.setInt(4, feePolicyDTO.getBillingUnitMinutes());
            preparedStatement.setInt(5, feePolicyDTO.getHelpDiscountRate());
            preparedStatement.setInt(6, feePolicyDTO.getCompactDiscountRate());
            preparedStatement.setInt(7, feePolicyDTO.getGracePeriodMinutes());
            preparedStatement.setInt(8, feePolicyDTO.getMaxCapAmount());
            preparedStatement.setInt(9, feePolicyDTO.getMonthlyPay());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 요금 정책 수정
    public void update(FeePolicyDTO feePolicyDTO) {
        String sql = "UPDATE fee_policy SET base_fee = ?, basic_unit_minute = ?, " +
                     "unit_fee = ?, billing_unit_minutes = ?, help_discount_rate = ?, " +
                     "compact_discount_rate = ?, grace_period_minutes = ?, max_cap_amount = ?, " +
                     "monthly_pay = ? " +
                     "WHERE id = ?";

        try {
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, feePolicyDTO.getBaseFee());
            preparedStatement.setInt(2, feePolicyDTO.getBasicUnitMinute());
            preparedStatement.setInt(3, feePolicyDTO.getUnitFee());
            preparedStatement.setInt(4, feePolicyDTO.getBillingUnitMinutes());
            preparedStatement.setInt(5, feePolicyDTO.getHelpDiscountRate());
            preparedStatement.setInt(6, feePolicyDTO.getCompactDiscountRate());
            preparedStatement.setInt(7, feePolicyDTO.getGracePeriodMinutes());
            preparedStatement.setInt(8, feePolicyDTO.getMaxCapAmount());
            preparedStatement.setInt(9, feePolicyDTO.getMonthlyPay());
            preparedStatement.setInt(10, feePolicyDTO.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}