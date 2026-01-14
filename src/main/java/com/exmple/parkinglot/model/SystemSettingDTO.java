package com.exmple.parkinglot.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettingDTO {
    private String settingKey;          // 설정 키 (예: BASE_RATE_PER_10MIN)
    private String settingValue;        // 설정 값
    private String settingDescription;  // 설명
}