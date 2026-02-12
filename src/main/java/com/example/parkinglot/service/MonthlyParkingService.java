package com.example.parkinglot.service;

import com.example.parkinglot.dao.MonthlyParkingDAO;
import com.example.parkinglot.dto.MonthlyParkingDTO;
import lombok.extern.log4j.Log4j2;

import java.sql.SQLException;
import java.util.List;

@Log4j2
public enum MonthlyParkingService {
    INSTANCE;

    private final MonthlyParkingDAO monthlyParkingDAO;
    private static final int PAGE_SIZE = 5; //한페이지당 표시 건수


    MonthlyParkingService( ) {
        this.monthlyParkingDAO = new MonthlyParkingDAO();
    }

    //회원 등록(차량 번호 중복 검사 포함)
    public boolean register(MonthlyParkingDTO monthlyParkingDTO) throws SQLException{
        log.info("회원 등록 요청 : {}", monthlyParkingDTO);

        if (monthlyParkingDAO.selectByPlateNumber(monthlyParkingDTO.getPlateNumber()) != null){
            log.info("차량 중복 번호 : {}", monthlyParkingDTO.getPlateNumber());
            return false;
        }
        monthlyParkingDAO.insert(monthlyParkingDTO);
        log.info("회원 등록 완료 : {}", monthlyParkingDTO.getPlateNumber());
        return true;
    }

    //페이징 처리된 회원 목록 조회
    public List<MonthlyParkingDTO> getPagedList(int page) throws SQLException{
        int offset = (page - 1) * PAGE_SIZE;
        return monthlyParkingDAO.selectWithPaging(offset, PAGE_SIZE);
    }
    //전체 회원 목록 조회
    public List<MonthlyParkingDTO> getAllList() throws SQLException{
        return monthlyParkingDAO.selectAll();
    }
    //전체 페이지 수 계산
    public int getTotalPages() throws SQLException{
        int totalCount = monthlyParkingDAO.getTotalCount();
        return (int) Math.ceil((double) totalCount / PAGE_SIZE);
    }
    //전체 회원 수
    public int getTotalCount() throws SQLException{
        return monthlyParkingDAO.getTotalCount();
    }
    //Id로 회원 조회
    public MonthlyParkingDTO getById(int id) throws SQLException{
        MonthlyParkingDTO monthlyParkingDTO = monthlyParkingDAO.selectById(id);
        log.info("회원 조회 (id={}) : {}", id, monthlyParkingDTO);
        return monthlyParkingDTO;
    }

    //차량 번호로 회원 조회
    public MonthlyParkingDTO getByPlateNumber(String plateNumber) throws SQLException{
        return  monthlyParkingDAO.selectByPlateNumber(plateNumber);
    }
    //회원 정보 수정
    public boolean update(MonthlyParkingDTO monthlyParkingDTO)throws SQLException{
        log.info("회원 수정 요청 : {}", monthlyParkingDTO);
        return monthlyParkingDAO.update(monthlyParkingDTO);
    }
    //회원 삭제
    public void delete(int id) throws SQLException{
        log.info("회원 삭제 요청 (id={}) : ", id);
        monthlyParkingDAO.delete(id);
    }
    //월정액 유효 회원 여부 확인
    public boolean isValidMember(String plateNumber) throws SQLException{
        return monthlyParkingDAO.isValidMember(plateNumber);
    }
    //페이지 사이즈
    public int getPageSize(){
        return PAGE_SIZE;
    }



}
