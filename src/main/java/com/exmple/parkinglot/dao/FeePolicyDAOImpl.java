package com.exmple.parkinglot.dao;

import com.exmple.parkinglot.dto.FeePolicyDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//주차요금 정책 DAO구현체
public class FeePolicyDAOImpl implements FeePolicyDAO {
    @Override
    public FeePolicyDTO selectCurrentPolicy() throws SQLException {
        String sql = "SELECT * FROM fee_policy ORDER BY update_date DESC LIMIT 1";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            ResultSet resultSet = preparedStatement.executeQuery();{

                if (resultSet.next()) {
                    return FeePolicyDTO.builder()
                            .id(resultSet.getInt("id"))
                            .baseFee(resultSet.getInt("base_fee"))
                            .basicUnitMinute(resultSet.getInt("basic_unit_minute"))
                            .unitFee(resultSet.getInt("unit_fee"))
                            .billingUnitMinutes(resultSet.getInt("billing_unit_minutes"))
                            .helpDiscountRate(resultSet.getInt("help_discount_rate"))
                            .compactDiscountRate(resultSet.getInt("compact_discount_rate"))
                            .updateDate(resultSet.getTimestamp("update_date").toLocalDateTime())
                            .gracePeriodMinutes(resultSet.getInt("grace_period_minutes"))
                            .maxCapAmount(resultSet.getInt("max_cap_amount"))
                            .build();
                }
                return null;
            }
        }
    }

    @Override
    public void insert(FeePolicyDTO feePolicyDTO) throws SQLException {
        String sql = "INSERT INTO fee_policy (base_fee, basic_unit_minute, unit_fee, " +
                "billing_unit_minutes, help_discount_rate, compact_discount_rate, " +
                "grace_period_minutes, max_cap_amount) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = ConnectionUtil.INSTANCE.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, feePolicyDTO.getBaseFee());
            preparedStatement.setInt(2, feePolicyDTO.getBasicUnitMinute());
            preparedStatement.setInt(3, feePolicyDTO.getUnitFee());
            preparedStatement.setInt(4, feePolicyDTO.getBillingUnitMinutes());
            preparedStatement.setInt(5, feePolicyDTO.getHelpDiscountRate());
            preparedStatement.setInt(6, feePolicyDTO.getCompactDiscountRate());
            preparedStatement.setInt(7, feePolicyDTO.getGracePeriodMinutes());
            preparedStatement.setInt(8, feePolicyDTO.getMaxCapAmount());
            preparedStatement.executeUpdate();

        }
    }

    @Override
    public void updateDate(FeePolicyDTO feePolicyDTO) throws SQLException {
        String sql = "UPDATE fee_policy SET base_fee = ?, basic_unit_minute = ?, " +
                "unit_fee = ?, billing_unit_minutes = ?, help_discount_rate = ?, " +
                "compact_discount_rate = ?, grace_period_minutes = ?, max_cap_amount = ? " +
                "WHERE id = ?";

        try(Connection connection = ConnectionUtil.INSTANCE.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, feePolicyDTO.getBaseFee());
            preparedStatement.setInt(2, feePolicyDTO.getBasicUnitMinute());
            preparedStatement.setInt(3, feePolicyDTO.getUnitFee());
            preparedStatement.setInt(4, feePolicyDTO.getBillingUnitMinutes());
            preparedStatement.setInt(5, feePolicyDTO.getHelpDiscountRate());
            preparedStatement.setInt(6, feePolicyDTO.getCompactDiscountRate());
            preparedStatement.setInt(7, feePolicyDTO.getGracePeriodMinutes());
            preparedStatement.setInt(8, feePolicyDTO.getMaxCapAmount());
            preparedStatement.setInt(9, feePolicyDTO.getId());
            preparedStatement.executeUpdate();

        }

    }
}
