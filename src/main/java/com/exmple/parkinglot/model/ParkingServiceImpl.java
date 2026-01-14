package com.exmple.parkinglot.model;

import lombok.extern.log4j.Log4j2;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Log4j2
public class ParkingServiceImpl implements ParkingService {

    // 데이터베이스 접근을 위한 DAO 객체들 (구현체 연결)
    private final ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAOImpl();
    private final ParkingRecordDAO parkingRecordDAO = new ParkingRecordDAOImpl();
    private final MemberDAO memberDAO = new MemberDAOImpl();
    private final SystemSettingDAO systemSettingDAO = new SystemSettingDAOImpl();

    @Override
    public int processEntry(String spotId, String carNumber, String carType) {
        // 1. 차량 번호로 회원 테이블을 조회하여 회원 여부 판단
        MemberDTO memberDTO = memberDAO.getMemberByCarNumber(carNumber);
        String isMember = (memberDTO != null) ? "Y" : "N";

        // 2. 주차 기록 테이블에 새로운 데이터 삽입 (입차 시점의 정보 보관)
        int generatedRecordId = parkingRecordDAO.insertRecord(spotId, carNumber, carType, isMember);

        if (generatedRecordId > 0) {
            // 3. 주차 구역의 상태를 '주차중(1)'으로 변경하고 현재 기록 ID를 연결
            int updateResult = parkingSpotDAO.updateSpotStatus(spotId, 1, generatedRecordId);
            if (updateResult > 0) {
                log.info("[Service] 입차 프로세스 완료: 차량번호={}, 구역={}", carNumber, spotId);
                return generatedRecordId;
            }
        }
        return 0;
    }

    @Override
    public int processExit(int recordId) {
        // 1. 전달받은 recordId로 기존 입차 정보를 불러옴
        ParkingRecordDTO parkingRecordDTO = parkingRecordDAO.getRecord(recordId);
        if (parkingRecordDTO == null) {
            log.error("[Service] 출차 데이터 조회 실패: recordId={}", recordId);
            return -1;
        }

        // 2. DB의 시스템 설정값을 기반으로 요금 계산 진행
        int originalTotalFee = calculateOriginalFeeFromDatabase(parkingRecordDTO.getEntryTime());

        // 3. 차종 및 회원 혜택에 따른 할인 적용
        int finalSettledFee = applyDiscountPolicy(originalTotalFee, parkingRecordDTO);

        // 4. 최종 할인된 금액(차액) 계산
        int appliedDiscountAmount = originalTotalFee - finalSettledFee;

        // 5. DB 업데이트: 출차 시간 기록 및 최종 정산 금액 저장
        parkingRecordDAO.updateExitRecord(recordId, finalSettledFee, appliedDiscountAmount);

        // 6. 주차 구역 상태를 '빈차위(0)'로 초기화
        parkingSpotDAO.updateSpotStatus(parkingRecordDTO.getSpotId(), 0, null);

        log.info("[Service] 출차 프로세스 완료: 최종 요금={} (할인액={})", finalSettledFee, appliedDiscountAmount);
        return finalSettledFee;
    }

    // [로직 1] DB 설정값을 활용한 순수 주차 요금 산정
    private int calculateOriginalFeeFromDatabase(String entryTimeStr) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime entryTime = LocalDateTime.parse(entryTimeStr, dateTimeFormatter);
        LocalDateTime currentTime = LocalDateTime.now();

        // 총 주차 시간(분) 계산
        long durationMinutes = Duration.between(entryTime, currentTime).toMinutes();

        // [방어 코드] DB에서 설정값을 읽어오되, null일 경우를 대비해 기본값(Default) 설정
        int freeTimeLimit = getSafeSetting("FREE_TIME_LIMIT", 10);
        int baseFee1Hour = getSafeSetting("BASE_FEE_1HOUR", 2000);
        int extraFee30Min = getSafeSetting("EXTRA_FEE_30MIN", 1000);
        int dayMaxFee = getSafeSetting("DAY_MAX_FEE", 15000);

        // 정책 1: 회차 무료 시간 이내
        if (durationMinutes <= freeTimeLimit) return 0;

        // 정책 2: 기본 시간(1시간) 이내
        if (durationMinutes <= 60) return baseFee1Hour;

        // 정책 3: 추가 시간 요금 (30분 단위 올림)
        long extraDurationMinutes = durationMinutes - 60;
        int additionalUnits = (int) Math.ceil(extraDurationMinutes / 30.0);
        int calculatedFee = baseFee1Hour + (additionalUnits * extraFee30Min);

        // 정책 4: 일일 최대 요금 제한
        return Math.min(calculatedFee, dayMaxFee);
    }

    // DB 설정값 조회 시 발생할 수 있는 에러를 방지하는 헬퍼 메서드
    private int getSafeSetting(String key, int defaultValue) {
        String settingValue = systemSettingDAO.getSetting(key);
        try {
            return (settingValue != null) ? Integer.parseInt(settingValue) : defaultValue;
        } catch (NumberFormatException e) {
            log.warn("[Service] 설정값({}) 형변환 실패. 기본값({})을 사용합니다.", key, defaultValue);
            return defaultValue;
        }
    }

    // [로직 2] 할인 정책 적용 (회원 면제 -> 경차/장애인 중 높은 할인율 적용)
    private int applyDiscountPolicy(int originalTotalFee, ParkingRecordDTO parkingRecordDTO) {
        if (originalTotalFee <= 0) return 0;

        // 회원일 경우 100% 면제
        if ("Y".equals(parkingRecordDTO.getIsMember())) return 0;

        double highestDiscountRate = 0.0;

        // 경차 할인 30%
        if ("경차".equals(parkingRecordDTO.getCarType())) {
            highestDiscountRate = 0.3;
        }

        // 장애인 할인 50% (경차보다 높으면 덮어쓰기)
        if ("장애인".equals(parkingRecordDTO.getCarType())) {
            highestDiscountRate = Math.max(highestDiscountRate, 0.5);
        }

        return (int) (originalTotalFee * (1 - highestDiscountRate));
    }
}