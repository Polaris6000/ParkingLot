package com.exmple.parkinglot.model;

import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;
import java.sql.*;

@Log4j2
public class MemberDAOImpl implements MemberDAO {
    // 차량 번호로 회원 여부와 정보를 확인하는 기능
    public MemberDTO getMemberByCarNumber(String carNumber) {
        String sql = "SELECT * FROM members WHERE car_number = ?";

        try {
            @Cleanup Connection connection = DBConnection.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, carNumber);
            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return MemberDTO.builder()
                        .memberId(resultSet.getString("member_id"))
                        .memberName(resultSet.getString("member_name"))
                        .phoneNumber(resultSet.getString("phone_number"))
                        .carNumber(resultSet.getString("car_number"))
                        .build();
            }
        } catch (SQLException sqlException) {
            log.error("회원 정보 조회 중 오류 발생: {}", sqlException.getMessage());
        }
        return null; // 회원이 아니면 null 반환
    }
}