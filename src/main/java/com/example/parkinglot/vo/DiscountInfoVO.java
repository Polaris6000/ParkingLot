package com.example.parkinglot.vo;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountInfoVO {
    Integer id; //차량의 할인 정보
    boolean disabilityDiscount; // 장애인 할인 여부
    boolean compactCar; // 경차 할인 여부
}
