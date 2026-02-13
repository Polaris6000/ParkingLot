package com.example.parkinglot.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

//월주차 정기권 회원정보
public class MonthlyParkingDTO {
    private int id; //월주차 등록 번호
    private String plateNumber; //차량 번호
    private String name; //회원이름
    private String phoneNumber; //연락처
    private LocalDate beginDate; //시작날짜
    private LocalDate expiryDate; //만료날짜
}