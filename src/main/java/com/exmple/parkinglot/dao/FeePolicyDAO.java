package com.exmple.parkinglot.dao;

import com.exmple.parkinglot.dto.FeePolicyDTO;

import java.sql.SQLException;

//주차 요금 정책 데이터 접근 인터페이스
public interface FeePolicyDAO {

    //현재 적용중인 요금 정책 조회
    FeePolicyDTO selectCurrentPolicy() throws SQLException;

    //요금 정책 등록
    void insert(FeePolicyDTO feePolicyDTO) throws SQLException;

    //요금 정책 수정
    void update(FeePolicyDTO feePolicyDTO) throws  SQLException;
}
