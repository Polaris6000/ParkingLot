package com.exmple.parkinglot.model;

import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;
import java.sql.*;

@Log4j2
public class SystemSettingDAOImpl implements SystemSettingDAO {
    // 설정 키를 던지면 설정된 값을 반환하는 기능 (예: 요금 정보)
    public String getSetting(String settingKey) {
        String sqlStatement = "SELECT setting_value FROM system_settings WHERE setting_key = ?";
        try {
            @Cleanup Connection connection = DBConnection.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, settingKey);
            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("setting_value");
            }
        } catch (SQLException sqlException) {
            log.error("설정값 조회 중 오류 발생: {}", sqlException.getMessage());
        }

        // 값이 없으면 '기본값'이라도 줘서 에러를 막아야 함!
        return switch (settingKey) {
            case "FREE_TIME_LIMIT" -> "10";
            case "BASE_FEE_1HOUR" -> "2000";
            case "EXTRA_FEE_30MIN" -> "1000";
            case "DAY_MAX_FEE" -> "15000";
            default -> "0";
        };
    }
}