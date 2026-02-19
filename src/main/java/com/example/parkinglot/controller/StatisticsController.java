package com.example.parkinglot.controller;

import com.example.parkinglot.dto.StatisticsDTO;
import com.example.parkinglot.service.StatisticsService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/**
 * 통계 관련 요청을 처리하는 컨트롤러
 * URL 매핑: /statistics
 *
 * 사용 예시:
 * - 기본 대시보드: /statistics
 * - 일별 조회: /statistics?searchType=daily&startDate=2024-01-01&endDate=2024-01-31
 * - 월별 조회: /statistics?searchType=monthly&yearMonth=2024-01
 */
@Log4j2
@WebServlet("/statistics")
public class StatisticsController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * GET 요청 처리 - 통계 페이지 조회
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        try {
            StatisticsService service = new StatisticsService();

            // 요청 파라미터 추출
            String searchType = request.getParameter("searchType");
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            String yearMonth = request.getParameter("yearMonth");

            // DTO 생성
            StatisticsDTO dto = new StatisticsDTO(searchType, startDate, endDate, yearMonth);

            // 통계 데이터 조회
            Map<String, Object> result = service.getStatistics(dto);

            // 결과를 request에 저장
            request.setAttribute("todaySummary", result.get("todaySummary"));
            request.setAttribute("monthSummary", result.get("monthSummary"));
            request.setAttribute("typeStats", result.get("typeStats"));

            if (result.containsKey("dailyStats")) {
                request.setAttribute("dailyStats", result.get("dailyStats"));
                request.setAttribute("searchType", "daily");
                request.setAttribute("startDate", startDate);
                request.setAttribute("endDate", endDate);
            }

            if (result.containsKey("monthlyStats")) {
                request.setAttribute("monthlyStats", result.get("monthlyStats"));
                request.setAttribute("searchType", "monthly");
                request.setAttribute("yearMonth", yearMonth);
            }

            // JSP 페이지로 포워딩
            // WEB-INF 내부에 있는 경우
            request.getRequestDispatcher("/WEB-INF/views/statistics.jsp").forward(request, response);

            // webapp 루트에 있는 경우
//            request.getRequestDispatcher("/statistics.jsp").forward(request, response);

        } catch (SQLException e) {
            log.error("통계 조회 중 데이터베이스 오류 발생: {}", e.getMessage(), e);
            request.setAttribute("errorMessage", "데이터베이스 오류가 발생했습니다: " + e.getMessage());

            // 에러 페이지로 포워딩
            // WEB-INF 내부에 있는 경우
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);

            // webapp 루트에 있는 경우
//            request.getRequestDispatcher("/error.jsp").forward(request, response);

        }
    }

    /**
     * POST 요청 처리 - GET과 동일하게 처리
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
