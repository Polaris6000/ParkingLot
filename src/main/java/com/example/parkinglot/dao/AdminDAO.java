package com.example.parkinglot.dao;

import com.example.parkinglot.vo.AdminVO;
import lombok.Cleanup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {
    //일단 connection을 가져와야 하나?
    //crud 하나씩 다 해야될거 아니가?

    //admin 테이블에서 id값으로 해당 대상의 값을 가져오는 메서드
    public AdminVO selectOneById(String id) {
        AdminVO adminVO = null;
        String sql = "select * from admin where id = ?";

        try {
            //connection 연결해서 아이디 값 찾아오기
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, id);

            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                adminVO = AdminVO.builder()
                        .id(id)
                        .name(resultSet.getString("name"))
                        .password(resultSet.getString("password"))
                        .email(resultSet.getString("email"))
                        .authorization((resultSet.getString("authorization")))
                        .authentication((resultSet.getBoolean("authentication")))
                        .build();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return adminVO;
    }

    //회원가입을 위해서 사용하는 메서드
    public void insert(AdminVO adminVO) {
        String sql = "insert into admin (id, password, name, email, authentication) " +
                "VALUES (?,?,?,?,?) ";

        try {
            //connection 연결해서 아이디 값 찾아오기
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, adminVO.getId());
            preparedStatement.setString(2, adminVO.getPassword());
            preparedStatement.setString(3, adminVO.getName());
            preparedStatement.setString(4, adminVO.getEmail());
            preparedStatement.setBoolean(5, false);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //회원 정보를 변경하기 위해 사용하는 메서드, 이름과 이메일은 바꿀 수 없음.
    public void update(AdminVO adminVO) {
        String sql = "update admin set password =? , authorization=?, authentication =? where id =?";

        try {
            //connection 연결해서 아이디 값 찾아오기
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, adminVO.getPassword());
//            preparedStatement.setString(2, adminVO.getName());
//            preparedStatement.setString(3, adminVO.getEmail());
            preparedStatement.setString(2, adminVO.getAuthorization());
            preparedStatement.setBoolean(3, adminVO.isAuthentication());
            preparedStatement.setString(4, adminVO.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //회원정보를 삭제? 흠.. 필요하면 그때 만들자.
    public void delete() {

    }

    //마스터 등급의 정보를 가져오는 내용
    public List<AdminVO> selectAllMaster(){
        List<AdminVO> adminVOList = new ArrayList<>();
        String sql = "select * from admin where authorization = ?";

        try {
            //connection 연결해서 아이디 값 찾아오기
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "master");

            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                AdminVO adminVO = AdminVO.builder()
                        .id(resultSet.getString("id"))
                        .name(resultSet.getString("name"))
                        .email(resultSet.getString("email"))
                        .authorization((resultSet.getString("authorization")))
                        .authentication(resultSet.getBoolean("authentication"))
                        .build();
                adminVOList.add(adminVO);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return adminVOList;
    }

    public AdminVO selectOneByEmail(String email) {
        AdminVO adminVO = null;
        String sql = "select * from admin where email = ?";

        try {
            //connection 연결해서 아이디 값 찾아오기
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, email);

            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                adminVO = AdminVO.builder()
                        .id(resultSet.getString("id"))
                        .name(resultSet.getString("name"))
                        .password(resultSet.getString("password"))
                        .email(resultSet.getString("email"))
                        .authorization((resultSet.getString("authorization")))
                        .authentication((resultSet.getBoolean("authentication")))
                        .build();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return adminVO;
    }
}
