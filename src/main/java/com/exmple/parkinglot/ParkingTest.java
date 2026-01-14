package com.exmple.parkinglot;

import com.exmple.parkinglot.model.ParkingService;
import com.exmple.parkinglot.model.ParkingServiceImpl;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ParkingTest {

    public static void main(String[] args) {
        // 실제 서비스 객체 생성 (DAO 구현체들이 내부에서 자동 연결됨)
        ParkingService parkingService = new ParkingServiceImpl();

        log.info("========== [주차 시스템 통합 테스트 시작] ==========");

        // [테스트 1] 일반 차량 입차 및 출차 테스트
        testGeneralVehicleFlow(parkingService);

        // [테스트 2] 경차 할인 및 DB 설정 연동 테스트
        testDiscountAndSettingFlow(parkingService);

        log.info("========== [모든 통합 테스트 종료] ==========");
    }

    private static void testGeneralVehicleFlow(ParkingService parkingService) {
        log.info(">>> 1. 일반 차량 입차 테스트 실행 (차량번호: 12가 3456)");

        // 입차 시 record_id를 반환받음
        int recordId = parkingService.processEntry("A-01", "12가 3456", "일반");

        if (recordId > 0) {
            log.info("   [성공] 입차 완료! 기록 ID: {}", recordId);

            // 즉시 출차 테스트 (10분 이내라 0원 나와야 함 - DB 설정 FREE_TIME_LIMIT 기반)
            log.info(">>> 2. 일반 차량 즉시 출차 테스트 실행");
            int fee = parkingService.processExit(recordId);
            log.info("   [결과] 최종 결제 금액: {}원", fee);
        } else {
            log.error("   [실패] 입차 처리에 실패했습니다.");
        }
        log.info("--------------------------------------------------");
    }

    private static void testDiscountAndSettingFlow(ParkingService parkingService) {
        log.info(">>> 3. 경차 할인 적용 테스트 실행 (차량번호: 99나 9999)");

        // 입차
        int recordId = parkingService.processEntry("B-05", "99나 9999", "경차");

        if (recordId > 0) {
            log.info("   [성공] 경차 입차 완료! 기록 ID: {}", recordId);

            // [주의] 현재 시간 기반이라 실제 주차 시간이 0분이므로 요금은 0원 나옴.
            // 만약 요금 계산 로직을 정밀하게 테스트하려면 DB에서 entry_time을 강제로 2시간 전으로 UPDATE하고 돌려야 함.
            int fee = parkingService.processExit(recordId);
            log.info("   [결과] (현재 시간 기준) 경차 최종 결제 금액: {}원", fee);
            log.info("   [설명] DB의 'system_settings' 값을 기준으로 계산되고, 경차 30% 할인이 적용됨.");
        }
        log.info("--------------------------------------------------");
    }
}