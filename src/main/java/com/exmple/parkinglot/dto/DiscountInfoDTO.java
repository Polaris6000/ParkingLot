package com.exmple.parkinglot.dto;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor

//차량별 할인정보
public class DiscountInfoDTO {
    private int id; //car_info의 id와 매칭
    private String isDisabilityDiscount; //장애인 할인 대상 (y/n)
    private String isCompactCar; //경차여부 (y/n)

}
