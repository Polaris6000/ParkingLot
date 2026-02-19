package com.example.parkinglot.vo;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class MonthlyParkingVO {
    private int id; //월주차 등록 번호
    private String plateNumber; //차량 번호
    private String name; //회원이름
    private String phoneNumber; //연락처
    private LocalDate beginDate; //시작날짜
    private LocalDate expiryDate; //만료날짜

}
