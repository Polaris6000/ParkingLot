package com.exmple.parkinglot.dao;

import com.exmple.parkinglot.dto.DiscountInfoDTO;

import java.sql.SQLException;

//차량별 할인 정보 데이터 접근 인터페이스
public interface DiscountInfoDAO {

    //할인정보 등록
    void insert(DiscountInfoDTO discountInfoDTO) throws SQLException;

    //id로 조회
    DiscountInfoDTO selectId(int id) throws SQLException;

    //할인 정보 수정
    void update(DiscountInfoDTO discountInfoDTO) throws SQLException;

}
