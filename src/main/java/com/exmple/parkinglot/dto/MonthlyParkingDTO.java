package com.exmple.parkinglot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyParkingDTO {
    private Integer id;
    private String plateNumber;
    private String name;
    private String phoneNumber;
    private LocalDate beginDate;
    private LocalDate expiryDate;
}