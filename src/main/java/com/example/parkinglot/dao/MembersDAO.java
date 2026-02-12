package com.example.parkinglot.dao;

import com.example.parkinglot.dto.MembersDTO;

import java.sql.SQLException;
import java.util.List;

public interface MembersDAO {
    //월주차 회원 등록
    void insert(MembersDTO membersDTO) throws SQLException;

    //차량번호로 조회
    MembersDTO selectByPlateNumber(String plateNumber) throws SQLException;

    //모든 회원 조회
    List<MembersDTO> selectAll() throws SQLException;

    //유효한 회원인지 확인
    boolean isValidMember(String plateNumber) throws  SQLException;

    //회원정보 수정
    boolean update(MembersDTO membersDTO) throws SQLException;

    //회원 삭제
    void delete(int id) throws SQLException;
}
