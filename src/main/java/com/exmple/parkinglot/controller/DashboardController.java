package com.exmple.parkinglot.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

/**
 * 대시보드 페이지 컨트롤러
 *
 * 역할: 대시보드 JSP 페이지를 렌더링
 * - 실제 데이터는 클라이언트에서 Ajax로 API를 호출하여 가져옴
 * - 서버 측에서는 초기 페이지만 제공
 *
 * 설계 패턴:
 * - View 컨트롤러: JSP 페이지 제공만 담당
 * - API 컨트롤러: DashboardApiController가 데이터 제공 담당
 *
 * @author 팀 프로젝트
 * @version 2.0 (REST API 적용)
 */
@Log4j2
@WebServlet(name = "DashboardController", value = "/dashboard")
public class DashboardController extends HttpServlet {

    /**
     * GET 요청 처리: 대시보드 페이지 렌더링
     *
     * 흐름:
     * 1. 사용자가 /dashboard 접근
     * 2. dashboard.jsp 페이지 반환
     * 3. JSP 내의 JavaScript가 Ajax로 데이터 요청
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        log.info("대시보드 페이지 요청");

        try {
            // JSP 페이지로 포워딩 (데이터는 Ajax로 가져옴)
            request.getRequestDispatcher("/web/dashboard.jsp")
                   .forward(request, response);

            log.info("dashboard.jsp 페이지 포워딩 완료");

        } catch (Exception e) {
            log.error("대시보드 페이지 로딩 중 오류 발생", e);
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }

    /**
     * POST 요청은 지원하지 않음
     * (검색 기능은 API를 통해 GET /api/dashboard/search로 처리)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        log.warn("대시보드 POST 요청은 지원하지 않습니다. API를 사용하세요.");
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                          "POST 메서드는 지원하지 않습니다. API를 사용하세요.");
    }
}