package com.exmple.parkinglot.controller;

import com.exmple.parkinglot.domain.DashboardStatsVO;
import com.exmple.parkinglot.domain.ParkingSpotVO;
import com.exmple.parkinglot.service.DashboardService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * 대시보드 화면 및 주차 현황을 처리하는 컨트롤러
 */
@Log4j2
@WebServlet(name = "DashboardController", value = "/dashboard")
public class DashboardController extends HttpServlet {

    private DashboardService service = DashboardService.INSTANCE;

    /**
     * GET 요청 처리: 대시보드 화면 표시 또는 Ajax 응답
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        log.info("Dashboard GET 요청 수신");

        // Ajax 요청 여부 확인
        String ajaxParam = request.getParameter("ajax");
        boolean isAjax = "true".equals(ajaxParam);

        try {
            // 대시보드 통계 정보 조회
            DashboardStatsVO stats = service.getStats();

            // 전체 주차 구역 상태 조회 (A01 ~ A20)
            List<ParkingSpotVO> parkingSpots = service.getAllParkingSpots();

            log.info("현재 주차 수: {}, 오늘 방문자: {}",
                    stats.getCurrentParkedCount(), stats.getTodayVisitorCount());
            log.info("주차 구역 데이터 개수: {}", parkingSpots.size());

            // Ajax 요청이면 JSON으로 응답
            if (isAjax) {
                sendJsonResponse(response, stats, parkingSpots);
            } else {
                // 일반 요청이면 JSP로 포워딩
                request.setAttribute("stats", stats);
                request.setAttribute("parkingSpots", parkingSpots);
                request.getRequestDispatcher("/web/dashboard.jsp")
                       .forward(request, response);
            }

        } catch (Exception e) {
            log.error("Dashboard 처리 중 오류 발생", e);
            handleError(request, response, isAjax, "데이터를 불러오는 중 오류가 발생했습니다.");
        }
    }

    /**
     * POST 요청 처리: 차량 번호 검색
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String searchKeyword = request.getParameter("searchKeyword");
        log.info("차량 번호 검색 요청: {}", searchKeyword);

        try {
            // 검색 결과 변수
            ParkingSpotVO searchResult = null;
            String searchMessage = null;

            // 검색어가 있으면 검색 실행
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                searchResult = service.searchByPlateNumber(searchKeyword.trim());

                if (searchResult != null) {
                    log.info("검색 결과: {} - {}", searchResult.getPlateNumber(), searchResult.getSpotNumber());
                } else {
                    log.info("검색 결과 없음: {}", searchKeyword);
                    searchMessage = "해당 차량번호를 찾을 수 없습니다.";
                }
            }

            // 검색 후에도 대시보드 정보는 함께 표시
            DashboardStatsVO stats = service.getStats();
            List<ParkingSpotVO> parkingSpots = service.getAllParkingSpots();

            request.setAttribute("stats", stats);
            request.setAttribute("parkingSpots", parkingSpots);
            request.setAttribute("searchResult", searchResult);
            request.setAttribute("searchMessage", searchMessage);

            request.getRequestDispatcher("/web/dashboard.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            log.error("검색 처리 중 오류 발생", e);
            handleError(request, response, false, "검색 중 오류가 발생했습니다.");
        }
    }

    /**
     * Ajax 요청에 대한 JSON 응답 전송 (수동 생성)
     */
    private void sendJsonResponse(HttpServletResponse response, DashboardStatsVO stats,
                                   List<ParkingSpotVO> parkingSpots) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // JSON 문자열 수동 생성
        StringBuilder json = new StringBuilder();
        json.append("{");

        // stats 객체
        json.append("\"stats\":{");
        json.append("\"currentParkedCount\":").append(stats.getCurrentParkedCount()).append(",");
        json.append("\"totalSpots\":").append(stats.getTotalSpots()).append(",");
        json.append("\"todayVisitorCount\":").append(stats.getTodayVisitorCount());
        json.append("},");

        // parkingSpots 배열
        json.append("\"parkingSpots\":[");
        for (int i = 0; i < parkingSpots.size(); i++) {
            ParkingSpotVO spot = parkingSpots.get(i);
            json.append("{");
            json.append("\"spotNumber\":\"").append(spot.getSpotNumber()).append("\",");
            json.append("\"occupied\":").append(spot.isOccupied()).append(",");
            json.append("\"carId\":").append(spot.getCarId() != null ? spot.getCarId() : "null").append(",");
            json.append("\"plateNumber\":").append(spot.getPlateNumber() != null ? "\"" + spot.getPlateNumber() + "\"" : "null").append(",");
            json.append("\"entryTime\":").append(spot.getEntryTime() != null ? "\"" + spot.getEntryTime() + "\"" : "null");
            json.append("}");
            if (i < parkingSpots.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");

        json.append("}");

        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();
    }

    /**
     * 에러 처리
     */
    private void handleError(HttpServletRequest request, HttpServletResponse response,
                             boolean isAjax, String message) throws IOException, ServletException {
        if (isAjax) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print("{\"error\": \"" + message + "\"}");
            out.flush();
        } else {
            request.setAttribute("error", message);
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}