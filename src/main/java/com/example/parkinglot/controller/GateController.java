package com.example.parkinglot.controller;

import com.example.parkinglot.service.ParkingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;


 // 차량 출입 처리 컨트롤러

@WebServlet(name = "gateController", urlPatterns = {"/gate/*"})
public class GateController extends HttpServlet {

    private final ParkingService parkingService = new ParkingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // 출입 관리 페이지로 이동
            request.getRequestDispatcher("/WEB-INF/views/gate.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();

        try {
            if ("/entry".equals(pathInfo)) {
                // 입차 처리
                handleEntry(request, response);
            } else if ("/exit".equals(pathInfo)) {
                // 출차 처리
                handleExit(request, response);
            } else if ("/discount".equals(pathInfo)) {
                // 할인 설정
                handleDiscount(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }


     // 입차 처리

    private void handleEntry(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String plateNumber = request.getParameter("plateNumber");
        String parkingSpot = request.getParameter("parkingSpot");

        int carId = parkingService.entryVehicle(plateNumber, parkingSpot);

        request.setAttribute("message", "입차 완료: " + plateNumber + " (ID: " + carId + ")");
        request.setAttribute("carId", carId);
        request.getRequestDispatcher("/WEB-INF/views/entry_success.jsp").forward(request, response);
    }


     // 출차 처리

    private void handleExit(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String plateNumber = request.getParameter("plateNumber");

        int fee = parkingService.exitVehicle(plateNumber);

        request.setAttribute("plateNumber", plateNumber);
        request.setAttribute("fee", fee);
        request.getRequestDispatcher("/WEB-INF/views/exit_result.jsp").forward(request, response);
    }


    // 할인 정보 설정

    private void handleDiscount(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        int carId = Integer.parseInt(request.getParameter("carId"));
        boolean isDisability = "Y".equals(request.getParameter("isDisability"));
        boolean isCompact = "Y".equals(request.getParameter("isCompact"));

        parkingService.setDiscount(carId, isDisability, isCompact);

        request.setAttribute("message", "할인 정보가 설정되었습니다.");
        request.getRequestDispatcher("/WEB-INF/views/discount_success.jsp").forward(request, response);
    }
}