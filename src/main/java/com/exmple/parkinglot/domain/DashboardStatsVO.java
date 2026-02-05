package com.exmple.parkinglot.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 대시보드 통계 정보 VO
 * - 현재 주차 중인 차량 수
 * - 전체 주차 면수
 * - 오늘 방문자 수
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsVO {

    private int currentParkedCount;  // 현재 주차 중인 차량 수
    private int totalSpots;          // 전체 주차 면수 (20)
    private int todayVisitorCount;   // 오늘 입차한 차량 수
}