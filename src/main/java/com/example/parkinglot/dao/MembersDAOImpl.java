package com.exmple.parkinglot.dao;

import com.exmple.parkinglot.dto.MembersDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//월주차 정기권 회원 DAO구현체
public class MembersDAOImpl implements MembersDAO {
    @Override
    public void insert(MembersDTO membersDTO) throws SQLException {
        String sql = "INSERT INTO members (plate_number, name, phone_number, begin_date, expiry_date) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, membersDTO.getPlateNumber());
            preparedStatement.setString(2, membersDTO.getName());
            preparedStatement.setString(3, membersDTO.getPhoneNumber());
            preparedStatement.setString(4, String.valueOf(Date.valueOf(membersDTO.getBeginDate())));
            preparedStatement.setString(5, String.valueOf(Date.valueOf(membersDTO.getExpiryDate())));
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public MembersDTO selectByPlateNumber(String plateNumber) throws SQLException {
        String sql = "SELECT * FROM members WHERE plate_number = ?";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, plateNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return MembersDTO.builder()
                        .id(resultSet.getInt("id"))
                        .plateNumber(resultSet.getString("plate_number"))
                        .name(resultSet.getString("name"))
                        .phoneNumber(resultSet.getString("phone_number"))
                        .beginDate(resultSet.getDate("begin_date").toLocalDate())
                        .expiryDate(resultSet.getDate("expiry_date").toLocalDate())
                        .build();
            }
            return null;
        }
    }

    @Override
    public List<MembersDTO> selectAll() throws SQLException {
        String sql = "SELECT * FROM members ORDER BY expiry_date DESC";
        List<MembersDTO> list = new ArrayList<>();

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            {

                while (resultSet.next()) {

                    list.add(MembersDTO.builder()
                            .id(resultSet.getInt("id"))
                            .plateNumber(resultSet.getString("plate_number"))
                            .name(resultSet.getString("name"))
                            .phoneNumber(resultSet.getString("phone_number"))
                            .beginDate(resultSet.getDate("begin_date").toLocalDate())
                            .expiryDate(resultSet.getDate("expiry_date").toLocalDate())
                            .build());
                }

            }
            return list;
        }
    }

    @Override
    public boolean isValidMember(String plateNumber) throws SQLException {
        String sql = "";

        try(Connection connection = ConnectionUtil.INSTANCE.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setString(1, plateNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                LocalDate expireDate = resultSet.getDate("expiry_date").toLocalDate();
                return !expireDate.isBefore(LocalDate.now());
            }
            return false;
        }
    }

    @Override
    public boolean update(MembersDTO membersDTO) throws SQLException {
        String sql = "UPDATE members SET name = ?, phone_number = ?, " +
                "begin_date = ?, expiry_date = ? WHERE id = ?";

        try(Connection connection = ConnectionUtil.INSTANCE.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setString(1, membersDTO.getPlateNumber());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                LocalDate expiryDate = resultSet.getDate("expiry_date").toLocalDate();
                return  !expiryDate.isBefore(LocalDate.now());
            }
            return false;
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM members WHERE id = ?";

        try(Connection connection = ConnectionUtil.INSTANCE.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }

    }
}
