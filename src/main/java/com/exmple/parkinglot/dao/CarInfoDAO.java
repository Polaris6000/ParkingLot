package com.exmple.parkinglot.dao;

import com.exmple.parkinglot.dto.CarInfoDTO;

import java.sql.SQLException;
import java.util.List;

//차량 기본 정보 데이터 접근 인터페이스
public interface CarInfoDAO {

    //차량 등록(입차)
    int insert(CarInfoDTO carInfoDTO) throws SQLException;

    //차량 번호로 조회
    CarInfoDTO selectByPlateNumber(String plateNumber) throws SQLException;

    //id로 조회
    CarInfoDTO selectById(int id) throws SQLException;

    //모든 차량 조회
    List<CarInfoDTO> selectAll() throws SQLException;

    //주차위치 업데이트
    void updateParkingSpot(int id, String parkingSpot) throws SQLException;

    //차량 삭제
    void delete(int id) throws SQLException;
}
