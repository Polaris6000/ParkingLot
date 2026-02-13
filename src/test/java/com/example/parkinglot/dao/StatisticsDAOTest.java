package com.example.parkinglot.dao;

import com.example.parkinglot.vo.StatisticsVO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatisticsDAOTest {

    private Connection conn;
    private StatisticsDAO statisticsDAO;

    // 테스트 실행 전 DB 연결 및 DAO 초기화
    @BeforeEach
    public void setup() throws Exception {
        // 실제 DB 정보로
        String url = "jdbc:mariadb://localhost:3306/parking_lot";
        String user = "admin";
        String password = "8282";

        Class.forName("org.mariadb.jdbc.Driver");
        this.conn = DriverManager.getConnection(url, user, password);

        // 생성자 주입
        this.statisticsDAO = new StatisticsDAO(conn);
    }

    // 테스트 종료 후 DB 연결 닫기
    @AfterEach
    public void tearDown() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    @Test
    @DisplayName("오늘 매출 요약 정보 조회 테스트")
    public void getTodaySummaryTest() throws SQLException {
        // 실행
        StatisticsVO summary = statisticsDAO.getTodaySummary();

        // 검증 (데이터가 있을 수도, 없을 수도 있으므로 null 체크 위주)
        // 오늘 날짜 데이터가 있다면 객체가 생성되어 반환됨
        System.out.println("오늘 요약: " + summary);

        // 데이터가 실제 DB에 있다면 assertNotNull로 검증 가능
        // assertNotNull(summary);
    }

    @Test
    @DisplayName("일별 매출 통계 리스트 조회 테스트")
    public void getDailySalesTest() throws SQLException {
        // 조건: 최근 일주일 데이터 조회
        String startDate = "2024-01-01";
        String endDate = "2024-12-31";

        // 실행
        List<StatisticsVO> list = statisticsDAO.getDailySales(startDate, endDate);

        // 검증
        assertNotNull(list);
        System.out.println("조회된 일별 데이터 개수: " + list.size());

        for (StatisticsVO vo : list) {
            System.out.println("날짜: " + vo.getDate() + ", 매출: " + vo.getTotalAmount());
        }
    }

    @Test
    @DisplayName("차종별 이용 비율 통계 조회 테스트")
    public void getTypeStatisticsTest() throws SQLException {
        // 실행
        List<StatisticsVO> list = statisticsDAO.getTypeStatistics();

        // 검증
        assertNotNull(list);
        if(!list.isEmpty()) {
            // 첫 번째 데이터의 비율이 0보다 큰지 확인
            assertTrue(list.get(0).getTypePercentage() >= 0);
        }
    }

    @Test
    @DisplayName("차종 코드 한글 변환 정적 메서드 테스트")
    public void getKindOfDiscountNameTest() {
        // 실행 및 검증
        assertEquals("경차", StatisticsDAO.getKindOfDiscountName("light"));
        assertEquals("일반", StatisticsDAO.getKindOfDiscountName("normal"));
        assertEquals("미분류", StatisticsDAO.getKindOfDiscountName(null));
        assertEquals("unknown", StatisticsDAO.getKindOfDiscountName("unknown"));
    }
}