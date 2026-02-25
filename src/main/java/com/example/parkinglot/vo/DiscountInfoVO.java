package com.example.parkinglot.vo;

import lombok.*;

// discount_info 테이블과 매핑되는 VO
// 기존 is_disability_discount, is_compact_car boolean → kind enum으로 통합 변경
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountInfoVO {
    private Integer id;
    private String kind; // normal / light / disabled / monthly / turn
}