package com.example.parkinglot.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthDTO {
    private String token;
    private String id;
    private String use;
    private LocalDateTime registerTime;
    private LocalDateTime expiryTime;
    private boolean isCanUse;
}

