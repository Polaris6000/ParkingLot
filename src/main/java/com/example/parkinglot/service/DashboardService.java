package com.example.parkinglot.service;

import com.example.parkinglot.dao.*;
import com.example.parkinglot.dto.FeePolicyDTO;
import com.example.parkinglot.dto.ParkingCarDTO;
import com.example.parkinglot.vo.CarInfoVO;
import com.example.parkinglot.vo.DiscountInfoVO;
import com.example.parkinglot.vo.ParkingTimesVO;
import com.example.parkinglot.vo.PayLogsVO;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
// 대시보드 관련 비즈니스 로직 서비스
// 기존 클래스 기반 싱글톤 → enum 싱글톤으로 변경 (프로젝트 표준 통일)
public enum DashboardService {
    INSTANCE;

    private final CarInfoDAO carInfoDAO = new CarInfoDAO();
    private final DiscountInfoDAO discountInfoDAO = new DiscountInfoDAO();
    private final ParkingTimesDAO parkingTimesDAO = new ParkingTimesDAO();
    private final FeePolicyDAO feePolicyDAO = new FeePolicyDAO();
    private final MonthlyParkingDAO monthlyParkingDAO = new MonthlyParkingDAO();
    private final PayLogsDAO payLogsDAO = new PayLogsDAO();

    public int getCurrentParkingCount() {
        log.info("현재 주차중인 차량의 수를 가져오기");
        List<ParkingTimesVO> parkings = parkingTimesDAO.selectCurrentParking();
        return parkings.size();
    }

    public int getTodayVisitor() {
        log.info("오늘 방문한 차량수를 가져오기");
        List<ParkingTimesVO> parkings = parkingTimesDAO.selectTodayVisitor();
        return parkings.size();
    }

    public List<ParkingCarDTO> getCurrentParking() {
        log.info("현재 주차중인 차량에 대한 정보를 전부 확인");
        List<ParkingCarDTO> parkingCarDTOList = new ArrayList<>();

        log.info("1. 일단 정산이 안된 차량을 찾아");
        List<ParkingTimesVO> parkings = parkingTimesDAO.selectCurrentParking();

        for (ParkingTimesVO parkingTimesVO : parkings) {
            log.info("2. 해당 차량의 id로 각 차량의 정보를 검색해");
            CarInfoVO carInfoVO = carInfoDAO.selectById(parkingTimesVO.getId());

            // discount_info 구조 변경: boolean 2개 → kind enum 1개
            // selectById 결과가 null이면 setData에서 normal로 처리
            DiscountInfoVO discountInfoVO = discountInfoDAO.selectById(parkingTimesVO.getId());

            log.info("월정액 회원인지도 확인해.");
            boolean isMonthly = monthlyParkingDAO.isValidMember(carInfoVO.getPlateNumber());

            log.info("3. 그리고 만든 데이터를 parkingCarDTO에 넣어");
            ParkingCarDTO parkingCarDTO = new ParkingCarDTO();
            parkingCarDTO.setData(carInfoVO, discountInfoVO, parkingTimesVO, isMonthly);

            log.info("4. 리스트에 값을 넣고");
            parkingCarDTOList.add(parkingCarDTO);
        }

        log.info("5. 반환해");
        return parkingCarDTOList;
    }

    // 요금 정책 조회
    // setting/feepolicy 쪽으로 통합 예정
    public FeePolicyDTO getFeePolicy() {
        log.info("요금 정책 조회");
        return feePolicyDAO.selectCurrentPolicy();
    }

    public void addParkingLog(ParkingCarDTO parkingCarDTO) {
        log.info("입차 처리 시작");

        carInfoDAO.insert(parkingCarDTO.toCarInfoVO());

        // INSERT 후 auto increment로 생성된 id 조회
        int no = carInfoDAO.selectByPlateNumber(parkingCarDTO.getPlateNumber()).getId();
        parkingCarDTO.setId(no);

        // id 생성 후 discount_info, parking_times 각각 등록
        discountInfoDAO.insert(parkingCarDTO.toDiscountInfoVO());
        parkingTimesDAO.insertEntry(no);

        log.info("입차 처리 완료 (id={})", no);
    }

    public void payParkingLog(ParkingCarDTO parkingCarDTO) {
        log.info("비용 지불 서비스");
        log.info("주차 dto : {}", parkingCarDTO);

        log.info("출차 시간에 대한 등록");
        parkingTimesDAO.updateExit(parkingCarDTO.getId(), parkingCarDTO.getExitTime());

        log.info("결제 내역에 대해서 기록하기");
        PayLogsVO payLogsVO = parkingCarDTO.toPayLogsVO();
        payLogsDAO.insert(payLogsVO);

        log.info("출차 처리 완료 (id={})", parkingCarDTO.getId());
    }
}