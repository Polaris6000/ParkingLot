package com.example.parkinglot.dao;

import com.example.parkinglot.dto.FeePolicyDTO;

import java.sql.SQLException;

public interface FeePolicyDAO {
    //현재 적용중인 요금 정책 조회
    FeePolicyDTO selectCurrentPolicy() throws SQLException;

    //요금 정책 등록
    void insert(FeePolicyDTO feePolicyDTO) throws SQLException;

    //요금 정책 수정
    void updateDate(FeePolicyDTO feePolicyDTO) throws  SQLException;
}
