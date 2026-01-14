package com.exmple.parkinglot.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private String memberId;        // 회원 아이디
    private String memberName;      // 회원 이름
    private String phoneNumber;     // 전화번호
    private String carNumber;       // 등록된 차량 번호
    private String registrationDate; // 가입일
}