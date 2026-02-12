package com.example.parkinglot.controller;

import com.example.parkinglot.dao.*;
import com.example.parkinglot.dto.*;
import com.example.parkinglot.service.StasticsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


 //대시보드 관리 컨트롤러
@WebServlet(name = "dashboardController", urlPatterns = {"/dashboard/*"})
public class DashBoardController extends HttpServlet {

    private final ParkingTimesDAO parkingTimesDAO = new ParkingTimesDAOImpl();
    private final CarInfoDAO carInfoDAO = new CarInfoDAOImpl();
    private final MembersDAO membersDAO = new MembersDAOImpl();
    private final StasticsService stasticsService = new StasticsService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // 메인 대시보드
                showMainDashboard(request, response);
            } else if ("/cars".equals(pathInfo)) {
                // 현재 주차 차량 목록
                showCurrentCars(request, response);
            } else if ("/members".equals(pathInfo)) {
                // 월주차 회원 목록
                showMembers(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }


    // 메인 대시보드 표시

    private void showMainDashboard(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        // 현재 주차 차량 수
        int currentCount = stasticsService.getCurrentParkingCount();

        // 오늘 입출차 통계
        Map<String, Integer> todayStats = stasticsService.getDailyStats(LocalDate.now());

        // 월주차 회원 수
        int memberCount = stasticsService.getActiveMemberCount();

        request.setAttribute("currentCount", currentCount);
        request.setAttribute("todayStats", todayStats);
        request.setAttribute("memberCount", memberCount);

        request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
    }


     // 현재 주차 차량 목록 표시

    private void showCurrentCars(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        List<ParkingTimesDTO> currentParking = parkingTimesDAO.selectCurrentParking();

        request.setAttribute("parkingList", currentParking);
        request.getRequestDispatcher("/WEB-INF/views/current_cars.jsp").forward(request, response);
    }


     //월주차 회원 목록 표시

    private void showMembers(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        List<MembersDTO> members = membersDAO.selectAll();

        request.setAttribute("members", members);
        request.getRequestDispatcher("/WEB-INF/views/members.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();

        try {
            if ("/member/add".equals(pathInfo)) {
                // 월주차 회원 등록
                addMember(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }


     //월주차 회원 등록

    private void addMember(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String plateNumber = request.getParameter("plateNumber");
        String name = request.getParameter("name");
        String phoneNumber = request.getParameter("phoneNumber");
        String beginDate = request.getParameter("beginDate");
        String expiryDate = request.getParameter("expiryDate");

        MembersDTO member = MembersDTO.builder()
                .plateNumber(plateNumber)
                .name(name)
                .phoneNumber(phoneNumber)
                .beginDate(LocalDate.parse(beginDate))
                .expiryDate(LocalDate.parse(expiryDate))
                .build();

        membersDAO.insert(member);

        response.sendRedirect(request.getContextPath() + "/dashboard/members");
    }
}