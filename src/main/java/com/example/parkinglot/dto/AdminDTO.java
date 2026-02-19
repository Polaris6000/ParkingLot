package com.example.parkinglot.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {
    String id; //아이디
    String password; //비밀번호
    String name; //이름
    String email; //2차 인증을 위한 이메일
    String authorization; //관리 권한
    boolean authentication; //인증 여부
}
