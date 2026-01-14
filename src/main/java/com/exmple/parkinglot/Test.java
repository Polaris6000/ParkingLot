package com.exmple.parkinglot;

import com.exmple.parkinglot.model.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Test {
    public static void main(String[] args) {
        // 모든 DAO 객체 생성
        ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAOImpl();
        ParkingRecordDAO parkingRecordDAO = new ParkingRecordDAOImpl();
        MemberDAO memberDAO = new MemberDAOImpl();
        SystemSettingDAO systemSettingDAO = new SystemSettingDAOImpl();

        log.info("========== [전체 시스템 통합 테스트 시작] ==========");

        // 1. 시스템 설정 조회 테스트 (요금 정보)
        log.info("1. 시스템 설정(기본 요금)을 조회합니다.");
        String baseRate = systemSettingDAO.getSetting("BASE_RATE_PER_10MIN");
        if (baseRate != null) {
            log.info("=> 조회 성공! 현재 10분당 요금: {}원", baseRate);
        } else {
            log.error("=> 조회 실패: 설정값을 찾을 수 없습니다.");
        }

        // 2. 회원 조회 테스트
        // 먼저 DB에 테스트용 회원을 넣고 테스트
        // 예: INSERT INTO members (member_id, member_name, car_number) VALUES ('user01', '홍길동', '123가 4567');
        log.info("2. 차량 번호 '123가 4567'의 회원 여부를 확인합니다.");
        MemberDTO memberDTO = memberDAO.getMemberByCarNumber("123가 4567");
        if (memberDTO != null) {
            log.info("=> 회원 확인 성공! 이름: {}, 아이디: {}", memberDTO.getMemberName(), memberDTO.getMemberId());
        } else {
            log.warn("=> 회원이 아닙니다. (일반 차량)");
        }

        // 3. 입차 및 자리 배정 테스트 (기존 로직)
        log.info("3. 입차 시나리오 실행 (A-01 구역)");
        String testSpotId = "A-01";
        String testCarNumber = "123가 4567";

        // 1) 기록 생성
        int generatedRecordId = parkingRecordDAO.insertRecord(testSpotId, testCarNumber, "경차", (memberDTO != null ? "Y" : "N"));

        if (generatedRecordId > 0) {
            // 2) 자리 상태 업데이트
            int updateResult = parkingSpotDAO.updateSpotStatus(testSpotId, 1, generatedRecordId);
            if (updateResult > 0) {
                log.info("=> [성공] 입차 기록 생성 및 {} 구역 배정 완료", testSpotId);
            }
        }

        log.info("========== [전체 시스템 통합 테스트 종료] ==========");
    }
}