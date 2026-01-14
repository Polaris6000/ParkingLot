package com.exmple.parkinglot.model;

public interface SystemSettingDAO {
    // 특정 설정 키값을 던지면 해당 값을 가져옴 (요금 계산용)
    String getSetting(String settingKey);
}