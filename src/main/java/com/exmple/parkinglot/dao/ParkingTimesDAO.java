package com.exmple.parkinglot.dao;

import com.exmple.parkinglot.dto.ParkingTimesDTO;

import java.sql.SQLException;
import java.util.List;

//입출차 시간 기록 데이터 접근 인터페이스
public interface ParkingTimesDAO {

    //입차 시간 기록
    void insertEntry(ParkingTimesDTO parkingTimesDTO) throws SQLException;

    //출차시간 업데이트
    void updateExit(int id)throws SQLException;

    //id로 조회
    ParkingTimesDTO selectById(int id) throws SQLException;

    //현재 주차중인 차량 조회
    List<ParkingTimesDTO> selectCurrentParking() throws SQLException;
}
