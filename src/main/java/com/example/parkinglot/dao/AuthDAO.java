package com.example.parkinglot.dao;

import com.example.parkinglot.vo.AdminVO;
import com.example.parkinglot.vo.AuthVO;
import lombok.Cleanup;

import java.sql.*;
import java.time.LocalDateTime;

public class AuthDAO {

    //admin 테이블에서 uuid값으로 해당 대상의 값을 가져오는 메서드
    public AuthVO selectOne(String uuid) {
        AuthVO authVO = null;
        String sql = "select * from auth_token where token = ?";

        try {
            //connection 연결해서 아이디 값 찾아오기
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, uuid);

            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                authVO = AuthVO.builder()
                        .token(uuid)
                        .id(resultSet.getString("id"))
                        .use(resultSet.getString("use"))
                        .registerTime(resultSet.getTimestamp("register_time").toLocalDateTime())
                        .expiryTime(resultSet.getObject("expiry_time", LocalDateTime.class))
                        .isCanUse(resultSet.getBoolean("is_can_use"))
                        .build();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return authVO;
    }

    //회원가입을 위해서 사용하는 메서드
    public void insert(AuthVO authVO) {
        //만료 시간을 지정
        final int expiry_time = 5;//분단위를 기록
        String sql = "insert into auth_token (`token`, id, `use`, register_time, expiry_time) " +
                "values (?,?,?,?,?) ";

        try {
            //connection 연결해서 아이디 값 찾아오기
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, authVO.getToken());
            preparedStatement.setString(2, authVO.getId());
            preparedStatement.setString(3, authVO.getUse());
            //시간까지 넣어야 하므로 timestamp를 사용하기.
            //8버전 이상이면 그냥 setObject를 쓰면 된다네요.
            preparedStatement.setObject(4, LocalDateTime.now());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now().plusMinutes(expiry_time)));

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //사용한 토큰을 사용 처리
    public void updateUse(String uuid){
        String sql = "update auth_token set is_can_use = ? where token = ? ";

        try {
            //connection 연결해서 아이디 값 찾아오기
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setBoolean(1, false);
            preparedStatement.setString(2, uuid);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
