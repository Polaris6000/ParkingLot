package com.example.parkinglot.controller;

import com.exmple.parkinglot.service.StasticsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;


 //통계 관리 컨트롤러

@WebServlet(name = "stasticsController", urlPatterns = {"/stats/*"})
public class StasticsController extends HttpServlet {

    private final StasticsService stasticsService = new StasticsService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // 통계 메인 페이지
                showStatistics(request, response);
            } else if ("/daily".equals(pathInfo)) {
                // 일일 통계
                showDailyStats(request, response);
            } else if ("/sales".equals(pathInfo)) {
                // 매출 통계
                showSalesStats(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }


     //통계 메인 페이지

    private void showStatistics(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        LocalDate today = LocalDate.now();

        // 오늘 통계
        Map<String, Integer> todayStats = stasticsService.getDailyStats(today);

        // 현재 주차 차량 수
        int currentCount = stasticsService.getCurrentParkingCount();

        // 활성 회원 수
        int memberCount = stasticsService.getActiveMemberCount();

        request.setAttribute("todayStats", todayStats);
        request.setAttribute("currentCount", currentCount);
        request.setAttribute("memberCount", memberCount);

        request.getRequestDispatcher("/WEB-INF/views/statistics.jsp").forward(request, response);
    }


    //일일 통계 조회

    private void showDailyStats(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String dateParam = request.getParameter("date");
        LocalDate date = (dateParam != null) ? LocalDate.parse(dateParam) : LocalDate.now();

        Map<String, Integer> stats = stasticsService.getDailyStats(date);

        request.setAttribute("date", date);
        request.setAttribute("stats", stats);
        request.getRequestDispatcher("/WEB-INF/views/daily_stats.jsp").forward(request, response);
    }


    //매출 통계 조회

    private void showSalesStats(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String dateParam = request.getParameter("date");
        LocalDate date = (dateParam != null) ? LocalDate.parse(dateParam) : LocalDate.now();

        int sales = stasticsService.getDailySales(date);

        request.setAttribute("date", date);
        request.setAttribute("sales", sales);
        request.getRequestDispatcher("/WEB-INF/views/sales_stats.jsp").forward(request, response);
    }
}