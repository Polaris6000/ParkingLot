package com.exmple.parkinglot.model;

public interface MemberDAO {
    // 차량 번호로 회원 정보가 있는지 조회
    MemberDTO getMemberByCarNumber(String carNumber);
}