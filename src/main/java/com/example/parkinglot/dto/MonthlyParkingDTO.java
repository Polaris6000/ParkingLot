package com.example.parkinglot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyParkingDTO {
    private Integer id;
    private String plateNumber;
    private String name;
    private String phoneNumber;
    private LocalDate beginDate;
    private LocalDate expiryDate;
    private String status; // DB에서 CASE WHEN으로 계산되어 들어옴 (active/expired/scheduled)
}