package com.exmple.parkinglot.dao;

import com.example.parkinglot.vo.FeePolicyVO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Log4j2
class TestDataDAOTest {
    private TestDataDAO testDataDAO;

    @BeforeEach
    public void ready() {
        testDataDAO = new TestDataDAO();
    }

    @Test
    void insertFeePolicy() {
        FeePolicyVO feePolicyVO = FeePolicyVO.builder()
                .baseFee(1500)
                .basicUnitMinute(50)
                .unitFee(800)
                .billingUnitMinutes(20)
                .helpDiscountRate(40)
                .compactDiscountRate(20)
                .gracePeriodMinutes(10)
                .maxCapAmount(10000).build();
        testDataDAO.insertFeePolicy(feePolicyVO);
        log.info(feePolicyVO);

    }
}