package com.exmple.parkinglot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarInfoDTO {
    private Integer id;
    private String plateNumber;
    private String parkingSpot;
}