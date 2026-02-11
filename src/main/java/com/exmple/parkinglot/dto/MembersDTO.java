package com.exmple.parkinglot.dto;


import lombok.*;

import java.time.LocalDate;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

//월주차 정기권 회원정보
public class MembersDTO {
    private int id; //월주차 등록 번호
    private String plateNumber; //차량 번호
    private String name; //회원이름
    private String phoneNumber; //연락처
    private LocalDate beginDate; //시작날짜
    private LocalDate expiryDate; //만료날짜
}
