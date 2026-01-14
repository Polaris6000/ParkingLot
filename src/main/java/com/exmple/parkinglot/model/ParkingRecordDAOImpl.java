package com.exmple.parkinglot.model;

import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;
import java.sql.*;

@Log4j2
public class ParkingRecordDAOImpl implements ParkingRecordDAO {

    @Override
    public int insertRecord(String spotId, String carNumber, String carType, String isMember) {
        String sqlStatement = "INSERT INTO parking_records (spot_id, car_number, car_type, is_member, entry_time) VALUES (?, ?, ?, ?, NOW())";
        try {
            @Cleanup Connection connection = DBConnection.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, spotId);
            preparedStatement.setString(2, carNumber);
            preparedStatement.setString(3, carType);
            preparedStatement.setString(4, isMember);
            preparedStatement.executeUpdate();

            @Cleanup ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getInt(1); // 생성된 record_id 반환
            }
        } catch (SQLException sqlException) {
            log.error("입차 기록 생성 중 오류 발생: {}", sqlException.getMessage());
        }
        return 0;
    }

    @Override
    public int updateExitRecord(int recordId, int totalFee, int appliedDiscountAmount) {
        // 출차 시간(exit_time)은 DB의 NOW()를 사용하고, 요금과 할인금액을 동시에 업데이트
        String sqlStatement = "UPDATE parking_records SET exit_time = NOW(), total_fee = ?, applied_discount = ? WHERE record_id = ?";
        try {
            @Cleanup Connection connection = DBConnection.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, totalFee);
            preparedStatement.setInt(2, appliedDiscountAmount);
            preparedStatement.setInt(3, recordId);

            return preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            log.error("출차 기록 업데이트 중 오류 발생: {}", sqlException.getMessage());
            return 0;
        }
    }

    @Override
    public ParkingRecordDTO getRecord(int recordId) {
        String sqlStatement = "SELECT * FROM parking_records WHERE record_id = ?";
        try {
            @Cleanup Connection connection = DBConnection.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, recordId);
            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return ParkingRecordDTO.builder()
                        .recordId(resultSet.getInt("record_id"))
                        .spotId(resultSet.getString("spot_id"))
                        .carNumber(resultSet.getString("car_number"))
                        .carType(resultSet.getString("car_type"))
                        .isMember(resultSet.getString("is_member"))
                        .entryTime(resultSet.getString("entry_time"))
                        .exitTime(resultSet.getString("exit_time"))
                        .totalFee(resultSet.getInt("total_fee"))
                        .appliedDiscountAmount(resultSet.getInt("applied_discount"))
                        .build();
            }
        } catch (SQLException sqlException) {
            log.error("기록 상세 조회 중 오류 발생: {}", sqlException.getMessage());
        }
        return null;
    }
}