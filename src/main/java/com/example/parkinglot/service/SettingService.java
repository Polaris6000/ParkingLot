package com.example.parkinglot.service;

import com.example.parkinglot.dao.SettingDAO;
import com.example.parkinglot.dto.SettingDTO;
import com.example.parkinglot.vo.SettingVO;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;

import java.sql.SQLException;
import java.util.List;

/**
 * 요금 정책 비즈니스 로직 처리 서비스
 * DAO와 Controller 사이의 중간 계층
 */
@Log4j2
public class SettingService {
    private final SettingDAO settingDAO;

    public SettingService() {
        this.settingDAO = new SettingDAO();
    }

    /**
     * 새로운 요금 정책 등록
     * @param dto 등록할 요금 정책 정보
     * @return 등록 성공 여부
     */
    public boolean registerFeePolicy(SettingDTO dto) {
        try {
            // 유효성 검증
            if (!validateFeePolicy(dto)) {
                log.error("Service - 유효성 검증 실패");
                return false;
            }

            // DAO를 통한 DB 저장
            boolean result = settingDAO.insertFeePolicy(dto);

            if (result) {
                log.info("Service - 요금 정책 등록 성공");
            } else {
                log.error("Service - 요금 정책 등록 실패");
            }

            return result;
        } catch (SQLException e) {
            log.error("Service - 요금 정책 등록 중 예외 발생: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 전체 요금 정책 목록 조회
     * @return 요금 정책 목록
     */
    public List<SettingVO> getAllFeePolicies() {
        try {
            List<SettingVO> list = settingDAO.selectAllFeePolicies();
            log.info("Service - 요금 정책 목록 조회 성공: " +
                             (list != null ? list.size() : 0) + "건");
            return list;
        } catch (SQLException e) {
            log.error("Service - 요금 정책 목록 조회 중 예외 발생: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 현재 적용 중인 요금 정책 조회 (최신 정책)
     * @return 현재 요금 정책
     */
    public SettingVO getCurrentFeePolicy() {
        try {
            SettingVO policy = settingDAO.selectLatestFeePolicy();
            if (policy != null) {
                log.info("Service - 현재 요금 정책 조회 성공: ID=" + policy.getId());
            } else {
                log.info("Service - 등록된 요금 정책이 없습니다.");
            }
            return policy;
        } catch (SQLException e) {
            log.error("Service - 현재 요금 정책 조회 중 예외 발생: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 특정 ID의 요금 정책 조회
     * @param id 요금 정책 ID
     * @return 해당 ID의 요금 정책
     */
    public SettingVO getFeePolicyById(int id) {
        try {
            SettingVO policy = settingDAO.selectFeePolicyById(id);
            if (policy != null) {
                log.info("Service - ID별 요금 정책 조회 성공: " + id);
            } else {
                log.info("Service - 해당 ID의 요금 정책이 없습니다: " + id);
            }
            return policy;
        } catch (SQLException e) {
            log.error("Service - ID별 요금 정책 조회 중 예외 발생: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 등록된 요금 정책 개수 조회
     * @return 요금 정책 개수
     */
    public int getFeePolicyCount() {
        try {
            int count = settingDAO.countFeePolicies();
            log.info("Service - 요금 정책 개수 조회 성공: " + count);
            return count;
        } catch (SQLException e) {
            log.error("Service - 요금 정책 개수 조회 중 예외 발생: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 요금 정책 유효성 검증
     * @param dto 검증할 요금 정책
     * @return 유효성 검증 결과
     */
    private boolean validateFeePolicy(SettingDTO dto) {
        // 기본 요금은 0 이상
        if (dto.getBaseFee() < 0) {
            log.error("유효성 검증 실패: 기본 요금은 0 이상이어야 합니다.");
            return false;
        }

        // 기본 시간은 1분 이상
        if (dto.getBasicUnitMinute() < 1) {
            log.error("유효성 검증 실패: 기본 시간은 1분 이상이어야 합니다.");
            return false;
        }

        // 추가 요금은 0 이상
        if (dto.getUnitFee() < 0) {
            log.error("유효성 검증 실패: 추가 요금은 0 이상이어야 합니다.");
            return false;
        }

        // 과금 단위는 1분 이상
        if (dto.getBillingUnitMinutes() < 1) {
            log.error("유효성 검증 실패: 과금 단위는 1분 이상이어야 합니다.");
            return false;
        }

        // 할인율은 0~100% 범위
        if (dto.getHelpDiscountRate() < 0 || dto.getHelpDiscountRate() > 100) {
            log.error("유효성 검증 실패: 장애인 할인율은 0~100% 범위여야 합니다.");
            return false;
        }

        if (dto.getCompactDiscountRate() < 0 || dto.getCompactDiscountRate() > 100) {
            log.error("유효성 검증 실패: 경차 할인율은 0~100% 범위여야 합니다.");
            return false;
        }

        // 회차 시간은 0 이상
        if (dto.getGracePeriodMinutes() < 0) {
            log.error("유효성 검증 실패: 회차 시간은 0 이상이어야 합니다.");
            return false;
        }

        // 최대 요금은 0 이상
        if (dto.getMaxCapAmount() < 0) {
            log.error("유효성 검증 실패: 최대 요금은 0 이상이어야 합니다.");
            return false;
        }

        log.info("유효성 검증 성공");
        return true;
    }

    /**
     * 요금 정책이 존재하는지 확인
     * @return 존재 여부
     */
    public boolean hasFeePolicy() {
        return getFeePolicyCount() > 0;
    }
}
