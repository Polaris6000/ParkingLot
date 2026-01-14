package com.exmple.parkinglot.model;

import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ParkingSpotDAOImpl implements ParkingSpotDAO {

    @Override
    public List<ParkingSpotDTO> getAllSpots() {
        List<ParkingSpotDTO> parkingSpotDTOList = new ArrayList<>();
        // 주차 구역과 차량 번호를 조인해서 가져오는 쿼리
        String sql = "SELECT s.spot_id, s.is_occupied, r.car_number " +
                              "FROM parking_spots s " +
                              "LEFT JOIN parking_records r ON s.current_record_id = r.record_id " +
                              "ORDER BY s.spot_id ASC";

        try {
            @Cleanup Connection connection = DBConnection.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ParkingSpotDTO parkingSpotDTO = ParkingSpotDTO.builder()
                        .spotId(resultSet.getString("spot_id"))
                        .isOccupied(resultSet.getInt("is_occupied"))
                        .carNumber(resultSet.getString("car_number"))
                        .build();
                parkingSpotDTOList.add(parkingSpotDTO);
            }
        } catch (SQLException sqlException) {
            log.error("전체 구역 조회 중 오류: {}", sqlException.getMessage());
        }
        return parkingSpotDTOList;
    }

    @Override
    public int updateSpotStatus(String spotId, int isOccupied, Integer currentRecordId) {
        // 주차 구역의 상태와 현재 물고 있는 기록 ID를 업데이트
        String sql = "UPDATE parking_spots SET is_occupied = ?, current_record_id = ? WHERE spot_id = ?";
        try {
            @Cleanup Connection connection = DBConnection.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, isOccupied);

            if (currentRecordId == null) {
                preparedStatement.setNull(2, Types.INTEGER);
            } else {
                preparedStatement.setInt(2, currentRecordId);
            }

            preparedStatement.setString(3, spotId);
            return preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            log.error("구역 상태 업데이트 중 오류: {}", sqlException.getMessage());
            return 0;
        }
    }
}