package com.example.parkinglot.service;

import com.example.parkinglot.dao.*;
import com.example.parkinglot.dto.FeePolicyDTO;
import com.example.parkinglot.dto.ParkingCarDTO;
import com.example.parkinglot.vo.CarInfoVO;
import com.example.parkinglot.vo.DiscountInfoVO;
import com.example.parkinglot.vo.ParkingTimesVO;
import com.example.parkinglot.vo.PayLogsVO;
import lombok.extern.log4j.Log4j2;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public enum DashboardService {
    INSTANCE;

    private final CarInfoDAO carInfoDAO = new CarInfoDAO();
    private final DiscountInfoDAO discountInfoDAO = new DiscountInfoDAO();
    private final ParkingTimesDAO parkingTimesDAO = new ParkingTimesDAO();
    private final FeePolicyDAO feePolicyDAO = new FeePolicyDAO();
    private final MonthlyParkingDAO monthlyParkingDAO = new MonthlyParkingDAO();
    private final PayLogsDAO payLogsDAO = new PayLogsDAO();

    DashboardService() {
    }


    public int getCurrentParkingCount() {
        log.info("현재 주차중인 차량의 수를 가져오기");
        int count = 0;
        List<ParkingTimesVO> parkings = parkingTimesDAO.selectCurrentParking();
        count = parkings.size();
        return count;
    }

    public int getTodayVisitor() {
        log.info("오늘 방문한 차량수를 가져오기");
        int count = 0;
        List<ParkingTimesVO> parkings = parkingTimesDAO.selectTodayVisitor();
        count = parkings.size();
        return count;
    }

    public List<ParkingCarDTO> getCurrentParking() {
        log.info("현재 주차중인 차량에 대한 정보를 전부 확인");
        List<ParkingCarDTO> parkingCarDTOList = new ArrayList<>();
        log.info("1. 일단 정산이 안된 차량을 찾아");
        List<ParkingTimesVO> parkings = parkingTimesDAO.selectCurrentParking();

        for (ParkingTimesVO parkingTimesVO : parkings){
            log.info("2. 해당 차량의 id로 각 차량의 정보를 검색해");
            CarInfoVO carInfoVO = carInfoDAO.selectById(parkingTimesVO.getId());
            DiscountInfoVO discountInfoVO = discountInfoDAO.selectById(parkingTimesVO.getId());

            log.info("월정액 회원인지도 확인해.");
            boolean isMonthly = monthlyParkingDAO.isValidMember(carInfoVO.getPlateNumber());

            log.info("3. 그리고 만든 데이터를 parkingCarDTO에 넣어");
            ParkingCarDTO parkingCarDTO = new ParkingCarDTO();
            parkingCarDTO.setData(carInfoVO,discountInfoVO,parkingTimesVO,isMonthly);


            log.info("4. 리스트에 값을 넣고");
            parkingCarDTOList.add(parkingCarDTO);
        }

        log.info("5. 반환해");
        return parkingCarDTOList;
    }


    //이건 임시로 작성한 데이터. setting이나 feepolicy쪽으로 합쳐야함.
    public FeePolicyDTO getFeePolicy() {
        FeePolicyDTO feePolicyDTO = null;
        try { //아오 아카
            feePolicyDTO = feePolicyDAO.selectCurrentPolicy();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return feePolicyDTO;
    }

    public void addParkingLog(ParkingCarDTO parkingCarDTO) {
        carInfoDAO.insert(parkingCarDTO.toCarInfoVO());
        //이제 차량정보를 가지고 no를 가져오는거다.
        int no = carInfoDAO.selectByPlateNumber(parkingCarDTO.getPlateNumber()).getId();
        parkingCarDTO.setId(no);

        //이제 no생겼잖아? 이걸로 여기저기 등록하면 됌.
        discountInfoDAO.insert(parkingCarDTO.toDiscountInfoVO());
        parkingTimesDAO.insertEntry(no);
    }

    public void payParkingLog(ParkingCarDTO parkingCarDTO) {
        log.info("비용 지불 서비스");
        log.info("주차 dto : {}", parkingCarDTO);

        log.info("출차 시간에 대한 등록");
        parkingTimesDAO.updateExit(parkingCarDTO.getId(), parkingCarDTO.getExitTime());
        //출차시간 업데이트 하세요. 이걸 받은 시간으로 업뎃하게 바꾸기.

        log.info("결제 내역에 대해서 기록하기");
        PayLogsVO payLogsVO = parkingCarDTO.toPayLogsVO();
        payLogsDAO.insert(payLogsVO);

    }
}