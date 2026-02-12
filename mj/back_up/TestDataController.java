package com.exmple.parkinglot.controller;

import com.exmple.parkinglot.service.TestDataService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Log4j2
@WebServlet(name = "TestDataController", value = "/test/data")
public class TestDataController extends HttpServlet {

    private final TestDataService service = TestDataService.INSTANCE;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 현재 통계 조회 (null 체크 추가)
            String statistics = service.getStatistics();
            request.setAttribute("statistics", statistics != null ? statistics : "통계 없음");

            request.getRequestDispatcher("/web/test_data.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            log.error("테스트 페이지 로딩 중 오류", e);
            request.setAttribute("error", "페이지 로딩 중 오류가 발생했습니다.");
            request.setAttribute("statistics", "통계 조회 실패");
            request.getRequestDispatcher("/web/test_data.jsp")
                   .forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String message = "";
        boolean success = true;

        try {
            // action이 null인지 먼저 체크
            if (action == null || action.trim().isEmpty()) {
                message = "작업 유형이 지정되지 않았습니다.";
                success = false;
            } else {
                switch (action) {
                    case "bulkEntry":
                        int entryCount = Integer.parseInt(request.getParameter("count"));
                        int inserted = service.bulkInsertParking(entryCount);
                        message = inserted + "대 입차 완료";
                        break;

                    case "bulkExit":
                        int exitCount = Integer.parseInt(request.getParameter("count"));
                        int exited = service.bulkExitParking(exitCount);
                        message = exited + "대 출차 완료";
                        break;

                    case "bulkMonthly":
                        int monthlyCount = Integer.parseInt(request.getParameter("count"));
                        int monthlyInserted = service.bulkInsertMonthlyMembers(monthlyCount);
                        message = monthlyInserted + "명 월정액 회원 등록 완료";
                        break;

                    case "bulkFeePolicy":
                        int policyCount = Integer.parseInt(request.getParameter("count"));
                        int policyInserted = service.bulkInsertFeePolicies(policyCount);
                        message = policyCount + "건 요금 정책 등록 완료";
                        break;

                    case "randomDeleteMonthly":
                        int deleteMonthlyCount = Integer.parseInt(request.getParameter("count"));
                        int deletedMonthly = service.randomDeleteData("monthly_parking", deleteMonthlyCount);
                        message = deletedMonthly + "명 월정액 회원 삭제 완료";
                        break;

                    case "randomDeletePolicy":
                        int deletePolicyCount = Integer.parseInt(request.getParameter("count"));
                        int deletedPolicy = service.randomDeleteData("fee_policy", deletePolicyCount);
                        message = deletedPolicy + "건 요금 정책 삭제 완료";
                        break;

                    case "clearAll":
                        service.clearAllParkingData();
                        message = "전체 주차 데이터 초기화 완료";
                        break;

                    default:
                        message = "알 수 없는 작업입니다: " + action;
                        success = false;
                }
            }

            if (success) {
                log.info("테스트 작업 완료: {} - {}", action, message);
            }

        } catch (NumberFormatException e) {
            log.error("잘못된 숫자 형식: {}", action, e);
            message = "오류 발생: 숫자 형식이 올바르지 않습니다.";
            success = false;
        } catch (Exception e) {
            log.error("테스트 작업 중 오류 발생: {}", action, e);
            message = "오류 발생: " + (e.getMessage() != null ? e.getMessage() : "알 수 없는 오류");
            success = false;
        }

        // 결과 메시지와 통계 다시 조회 (null 안전 처리)
        request.setAttribute("message", message);

        try {
            String statistics = service.getStatistics();
            request.setAttribute("statistics", statistics != null ? statistics : "통계 없음");
        } catch (Exception e) {
            log.error("통계 조회 중 오류", e);
            request.setAttribute("statistics", "통계 조회 실패");
        }

        request.getRequestDispatcher("/web/test_data.jsp")
               .forward(request, response);


        // url을 레스트 방식으로
    }
}