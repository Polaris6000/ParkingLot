package com.example.parkinglot.vo;


import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminVO {
    private String id; //아이디
    private String password; //비밀번호
    private String name; //이름
    private String email; //2차 인증을 위한 이메일
    private String authorization; //관리 권한
    private boolean authentication; //인증 여부
}
