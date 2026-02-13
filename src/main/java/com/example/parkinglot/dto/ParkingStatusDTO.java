package com.example.parkinglot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParkingStatusDTO {
    private Integer id; // car_info의 id
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
}