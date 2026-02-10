package com.exmple.parkinglot.service;


import com.exmple.parkinglot.dao.StasticsDAO;
import com.exmple.parkinglot.dto.StasticsDTO;
import com.exmple.parkinglot.vo.StasticsVO;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 관리자 Service (Business Logic Layer)
 * Controller와 DAO 사이의 비즈니스 로직 처리
 */

//통계에 관련된 내용을 처리하는 서비스
public class StasticsService {
    private StasticsDAO stasticsDAO;

    public StasticsService() {
        this.stasticsDAO = new StasticsDAO();
    }

    /**
     * 현재 요금 정책 조회 (VO → DTO 변환)
     */
    public StasticsDTO getFeePolicy() throws SQLException {
        StasticsVO vo = stasticsDAO.getFeePolicy();

        if (vo == null) {
            return null;
        }

        StasticsDTO dto = new StasticsDTO();
        dto.setPolicyId(vo.getPolicyId());
        dto.setBaseFee(vo.getBaseFee());
        dto.setBasicUnitMinute(vo.getBasicUnitMinute());
        dto.setUnitFee(vo.getUnitFee());
        dto.setBillingUnitMinutes(vo.getBillingUnitMinutes());
        dto.setHelpDiscountRate(vo.getHelpDiscountRate());
        dto.setCompactDiscountRate(vo.getCompactDiscountRate());
        dto.setGracePeriodMinutes(vo.getGracePeriodMinutes());
        dto.setMaxCapAmount(vo.getMaxCapAmount());

        if (vo.getPolicyUpdateDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            dto.setUpdateDate(vo.getPolicyUpdateDate().format(formatter));
        }

        return dto;
    }

    /**
     * 요금 정책 수정 (DTO → VO 변환)
     */
    public boolean updateFeePolicy(StasticsDTO dto) throws SQLException {
        // 입력값 검증
        validateFeePolicy(dto);

        StasticsVO vo = new StasticsVO();
        vo.setPolicyId(dto.getPolicyId());
        vo.setBaseFee(dto.getBaseFee());
        vo.setBasicUnitMinute(dto.getBasicUnitMinute());
        vo.setUnitFee(dto.getUnitFee());
        vo.setBillingUnitMinutes(dto.getBillingUnitMinutes());
        vo.setHelpDiscountRate(dto.getHelpDiscountRate());
        vo.setCompactDiscountRate(dto.getCompactDiscountRate());
        vo.setGracePeriodMinutes(dto.getGracePeriodMinutes());
        vo.setMaxCapAmount(dto.getMaxCapAmount());

        int result = StasticsDAO.updateFeePolicy(vo);
        return result > 0;
    }

    /**
     * 일별 매출 통계 (VO → DTO 변환)
     */
    public List<StasticsDTO.SalesData> getDailySales() throws SQLException {
        List<StasticsVO> voList = stasticsDAO.getDailySales();
        List<StasticsDTO.SalesData> dtoList = new ArrayList<>();

        for (StasticsVO vo : voList) {
            StasticsDTO.SalesData data = new StasticsDTO.SalesData(
                    vo.getPeriod(),
                    vo.getTotalAmount(),
                    vo.getTotalCount()
            );
            dtoList.add(data);
        }

        return dtoList;
    }

    /**
     * 월별 매출 통계 (VO → DTO 변환)
     */
    public List<StasticsDTO.SalesData> getMonthlySales() throws SQLException {
        List<StasticsVO> voList = stasticsDAO.getMonthlySales();
        List<StasticsDTO.SalesData> dtoList = new ArrayList<>();

        for (StasticsVO vo : voList) {
            StasticsDTO.SalesData data = new StasticsDTO.SalesData(
                    vo.getPeriod(),
                    vo.getTotalAmount(),
                    vo.getTotalCount()
            );
            dtoList.add(data);
        }

        return dtoList;
    }

    /**
     * 차종별 통계 조회
     */
    public Map<String, Integer> getVehicleStats() throws SQLException {
        return stasticsDAO.getVehicleStats();
    }

    /**
     * 대시보드 요약 데이터 조회
     */
    public StasticsDTO getDashboardSummary() throws SQLException {
        Map<String, Object> summaryMap = stasticsDAO.getSummary();

        StasticsDTO dto = new StasticsDTO();
        dto.setTodayAmount((Integer) summaryMap.getOrDefault("todayAmount", 0));
        dto.setTodayCount((Integer) summaryMap.getOrDefault("todayCount", 0));
        dto.setMonthAmount((Integer) summaryMap.getOrDefault("monthAmount", 0));
        dto.setMonthCount((Integer) summaryMap.getOrDefault("monthCount", 0));
        dto.setCurrentParking((Integer) summaryMap.getOrDefault("currentParking", 0));

        return dto;
    }
}
