package com.example.parkinglot.service;

import com.example.parkinglot.dao.*;
import com.example.parkinglot.dto.CarInfoDTO;
import com.example.parkinglot.dto.DiscountInfoDTO;
import com.example.parkinglot.dto.FeePolicyDTO;
import com.example.parkinglot.dto.ParkingTimesDTO;

import java.time.Duration;
import java.time.LocalDateTime;

//차량 정보에 대해서 처리하는 서비스
public class ParkingService {

    private final CarInfoDAO carInfoDAO = new CarInfoDAOImpl();
    private final ParkingTimesDAO parkingTimesDAO = new ParkingTimesDAOImpl();
    private final MonthlyParkingDAO monthlyParkingDAO = new MonthlyParkingDAOImpl();
    private final DiscountInfoDAO discountInfoDAO = new DiscountInfoDAOImpl();
    private final FeePolicyDAO feePolicyDAO = new FeePolicyDAOImpl();

    //차량 입차 처리
    public int entryVehicle(String plateNumber, String parkingSpot) throws Exception {
        // 차량 정보 등록
        CarInfoDTO carInfoDTO = CarInfoDTO.builder()
                .plateNumber(plateNumber)
                .parkingSpot(parkingSpot)
                .build();

        int carId = carInfoDAO.insert(carInfoDTO);

        //입차시간 기록
        ParkingTimesDTO parkingTimesDTO = ParkingTimesDTO.builder()
                .id(carId)
                .entryTime(LocalDateTime.now())
                .build();

        parkingTimesDAO.insertEntry(parkingTimesDTO);

        //할인정보 기본값 등록
        DiscountInfoDTO discountInfoDTO = DiscountInfoDTO.builder()
                .id(carId)
                .isDisabilityDiscount("N")
                .isCompactCar("N")
                .build();

        discountInfoDAO.insert(discountInfoDTO);

        return carId;
    }

    //차량 출차 및 요금 계산
    public int exitVehicle(String plateNumber) throws Exception {
        //차량 정보 조회
        CarInfoDTO carInfoDTO = carInfoDAO.selectByPlateNumber(plateNumber);
        if (carInfoDTO == null) {
            throw new Exception("등록되지 않은 차량입니다.");
        }
        //월주차 회원 확인
        if (monthlyParkingDAO.isValidMember(plateNumber)) {
            parkingTimesDAO.updateExit(carInfoDTO.getId());
            return 0; //월주차 회원은 무료
        }
        //출차 시간 업데이트
        parkingTimesDAO.updateExit(carInfoDTO.getId());

        //주차 시간 및 요금 계산
        ParkingTimesDTO parkingTimesDTO = parkingTimesDAO.selectById(carInfoDTO.getId());
        int fee = calculateFee(carInfoDTO.getId(), parkingTimesDTO);

        return fee;
    }

    private int calculateFee(int carId, ParkingTimesDTO parkingTimesDTO) throws Exception {
        //요금 정책 조회
        FeePolicyDTO feePolicyDTO = feePolicyDAO.selectCurrentPolicy();
        if (feePolicyDTO == null) {
            throw new Exception("요금 정책이 설정되지 않았습니다.");
        }
        //주차 시간 계산
        long minutes = Duration.between(parkingTimesDTO.getEntryTime(), parkingTimesDTO.getExitTime()).toMinutes();

        //회차 인정 시간 확인
        if (minutes <= feePolicyDTO.getGracePeriodMinutes()) {
            return 0;
        }
        //기본 요금 적용
        int fee = feePolicyDTO.getBaseFee();

        //추가 요금 계산
        if (minutes > feePolicyDTO.getBasicUnitMinute()) {
            long additionalMinutes = minutes - feePolicyDTO.getBasicUnitMinute();
            long additionalUnits = (long) Math.ceil((double) additionalMinutes / feePolicyDTO.getBillingUnitMinutes());
            fee += (int) (additionalUnits * feePolicyDTO.getUnitFee());
        }
        //할인 적용
        DiscountInfoDTO discountInfo = discountInfoDAO.selectId(carId);
        if (discountInfo != null) {
            if ("Y".equals(discountInfo.getIsDisabilityDiscount())) {
                fee = fee * (100 - feePolicyDTO.getHelpDiscountRate()) / 100;
            } else if ("Y".equals(discountInfo.getIsCompactCar())) {
                fee = fee * (100 - feePolicyDTO.getCompactDiscountRate()) / 100;
            }
        }
        //최대 요금 제한
        if (fee > feePolicyDTO.getMaxCapAmount()) {
            fee = feePolicyDTO.getMaxCapAmount();
        }
        return fee;
    }

    //할인 정보 설정
    public void setDiscount(int carId, boolean isDisability, boolean isCompact) throws Exception {
        DiscountInfoDTO discountInfo = DiscountInfoDTO.builder()
                .id(carId)
                .isDisabilityDiscount(isDisability ? "Y" : "N")
                .isCompactCar(isCompact ? "Y" : "N")
                .build();

        discountInfoDAO.update(discountInfo);

    }
}
