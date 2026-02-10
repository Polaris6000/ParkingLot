package com.exmple.parkinglot.controller;

import com.exmple.parkinglot.dto.StasticsDTO;
import com.exmple.parkinglot.service.StasticsService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 관리자 Controller (Servlet)
 * URL 패턴: /stastics/*
 * 클라이언트 요청 처리 및 응답 제어
 */
@WebServlet("/stastics/*")
public class StasticsController extends HttpServlet {
    private StasticsService stasticsService;
    
    @Override
    public void init() throws ServletException {
        stasticsService = new StasticsService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        // 세션 체크 (관리자 권한 확인)
        HttpSession session = request.getSession();
        String authorization = (String) session.getAttribute("authorization");
        
        if (authorization == null || !authorization.equals("master")) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/dashboard")) {
                showDashboard(request, response);
            } else if (pathInfo.equals("/settings")) {
                showSettings(request, response);
            } else if (pathInfo.equals("/sales")) {
                showSales(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            handleError(request, response, "데이터베이스 오류가 발생했습니다.", e);
        } catch (Exception e) {
            handleError(request, response, "서버 오류가 발생했습니다.", e);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        // 세션 체크
        HttpSession session = request.getSession();
        String authorization = (String) session.getAttribute("authorization");
        
        if (authorization == null || !authorization.equals("master")) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo.equals("/updateSettings")) {
                updateSettings(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            handleError(request, response, "데이터베이스 오류가 발생했습니다.", e);
        } catch (Exception e) {
            handleError(request, response, "서버 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 대시보드 메인 페이지
     */
    private void showDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        // 요약 통계
        StasticsDTO summary = stasticsService.getDashboardSummary();
        request.setAttribute("summary", summary);
        
        // 차종별 통계
        Map<String, Integer> vehicleStats = stasticsService.getVehicleStats();
        request.setAttribute("vehicleStats", vehicleStats);
        
        // 최근 7일 매출
        List<StasticsDTO.SalesData> recentSales = stasticsService.getRecentSales(7);
        request.setAttribute("recentSales", recentSales);
        
        request.getRequestDispatcher("/WEB-INF/views/stastics/dashboard.jsp").forward(request, response);
    }
    
    /**
     * 설정 관리 페이지
     */
    private void showSettings(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        StasticsDTO policy = stasticsService.getFeePolicy();
        request.setAttribute("policy", policy);
        
        request.getRequestDispatcher("/WEB-INF/views/stastics/settings.jsp").forward(request, response);
    }
    
    /**
     * 매출 통계 페이지
     */
    private void showSales(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        String type = request.getParameter("type");
        if (type == null) type = "daily";
        
        List<StasticsDTO.SalesData> salesData;
        if (type.equals("monthly")) {
            salesData = stasticsService.getMonthlySales();
        } else {
            salesData = stasticsService.getDailySales();
        }
        
        request.setAttribute("salesData", salesData);
        request.setAttribute("type", type);
        
        // 차종별 통계
        Map<String, Integer> vehicleStats = stasticsService.getVehicleStats();
        request.setAttribute("vehicleStats", vehicleStats);
        
        request.getRequestDispatcher("/WEB-INF/views/stastics/sales.jsp").forward(request, response);
    }
    
    /**
     * 요금 정책 수정
     */
    private void updateSettings(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        try {
            // 현재 정책 조회
            StasticsDTO currentPolicy = stasticsService.getFeePolicy();
            
            // 파라미터 추출
            StasticsDTO dto = new StasticsDTO();
            dto.setPolicyId(currentPolicy.getPolicyId());
            dto.setBaseFee(Integer.parseInt(request.getParameter("baseFee")));
            dto.setBasicUnitMinute(Integer.parseInt(request.getParameter("basicUnitMinute")));
            dto.setUnitFee(Integer.parseInt(request.getParameter("unitFee")));
            dto.setBillingUnitMinutes(Integer.parseInt(request.getParameter("billingUnitMinutes")));
            dto.setHelpDiscountRate(Integer.parseInt(request.getParameter("helpDiscountRate")));
            dto.setCompactDiscountRate(Integer.parseInt(request.getParameter("compactDiscountRate")));
            dto.setGracePeriodMinutes(Integer.parseInt(request.getParameter("gracePeriodMinutes")));
            dto.setMaxCapAmount(Integer.parseInt(request.getParameter("maxCapAmount")));
            
            // 정책 수정
            boolean success = stasticsService.updateFeePolicy(dto);
            
            if (success) {
                request.setAttribute("message", "요금 정책이 성공적으로 수정되었습니다.");
                request.setAttribute("messageType", "success");
            } else {
                request.setAttribute("message", "요금 정책 수정에 실패했습니다.");
                request.setAttribute("messageType", "error");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("message", "입력값이 올바르지 않습니다.");
            request.setAttribute("messageType", "error");
        } catch (IllegalArgumentException e) {
            request.setAttribute("message", e.getMessage());
            request.setAttribute("messageType", "error");
        }
        
        // 설정 페이지로 다시 이동
        showSettings(request, response);
    }
    
    /**
     * 오류 처리
     */
    private void handleError(HttpServletRequest request, HttpServletResponse response, 
                            String message, Exception e) throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.setAttribute("errorDetail", e.getMessage());
        e.printStackTrace(); // 로그 출력
        
        request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
    }
}
