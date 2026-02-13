package com.example.parkinglot.vo;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthVO {
    private String token;
    private String id;
    private String use;
    private LocalDateTime registerTime;
    private LocalDateTime expiryTime;
    private boolean isCanUse;
}
