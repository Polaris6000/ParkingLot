package com.example.parkinglot.dto;

import com.example.parkinglot.dao.DiscountInfoDAO;
import com.example.parkinglot.vo.CarInfoVO;
import com.example.parkinglot.vo.DiscountInfoVO;
import com.example.parkinglot.vo.ParkingTimesVO;
import com.example.parkinglot.vo.PayLogsVO;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingCarDTO {
    private Integer id; //car_info의 id와 매칭
    private String plateNumber; //차량번호
    private String parkingSpot; //주차위치
    private String kindOfDiscount; //할인 종류
    private LocalDateTime entryTime; //입차시간
    private LocalDateTime exitTime; //출차시간
    private Integer cost; //비용 받기


    public void setData(CarInfoVO carInfoVO, DiscountInfoVO discountInfoVO, ParkingTimesVO parkingTimesVO, boolean isMember) {
        this.id = carInfoVO.getId();
        this.plateNumber = carInfoVO.getPlateNumber();
        this.parkingSpot = carInfoVO.getParkingSpot();

        if (isMember) {
            kindOfDiscount = "monthly";
        } else if (discountInfoVO.isDisabilityDiscount()) {
            kindOfDiscount = "disabled";
        } else if (discountInfoVO.isCompactCar()) {
            kindOfDiscount = "light";
        } else {
            kindOfDiscount = "normal";
        }


        this.entryTime = parkingTimesVO.getEntryTime();
        this.exitTime = parkingTimesVO.getExitTime();
    }

    public CarInfoVO toCarInfoVO() {
        return CarInfoVO.builder()
                .id(id)
                .plateNumber(plateNumber)
                .parkingSpot(parkingSpot)
                .build();
    }

    public DiscountInfoVO toDiscountInfoVO() {
        DiscountInfoVO discountInfoVO = null;

        switch (kindOfDiscount){
            case "disabled" ->{
                discountInfoVO = DiscountInfoVO.builder()
                        .id(id)
                        .disabilityDiscount(true)
                        .compactCar(false)
                        .build();
            }
            case "light" ->{
                discountInfoVO = DiscountInfoVO.builder()
                        .id(id)
                        .disabilityDiscount(false)
                        .compactCar(true)
                        .build();
            }

            default -> {
                discountInfoVO = DiscountInfoVO.builder()
                        .id(id)
                        .disabilityDiscount(false)
                        .compactCar(false)
                        .build();
            }
        }
         return discountInfoVO;
    }

    public ParkingTimesVO toparkingTimesVO() {
        return ParkingTimesVO.builder()
                .id(id)
                .entryTime(entryTime)
                .exitTime(exitTime)
                .build();
    }

    public PayLogsVO toPayLogsVO() {
        return PayLogsVO.builder()
                .id(id)
                .payTime(exitTime)
                .payLog(cost)
                .kindOfDiscount(kindOfDiscount)
                .build();
    }
}
