package com.example.parkinglot.controller;

import com.example.parkinglot.dto.SettingDTO;
import com.example.parkinglot.service.SettingService;
import com.example.parkinglot.vo.SettingVO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 요금 정책 설정 컨트롤러
 * MVC 패턴의 Controller - 클라이언트 요청 처리 및 응답 제어
 *
 * URL 매핑: /setting
 * - GET: 요금 정책 목록 조회 및 페이지 표시
 * - POST: 요금 정책 등록 처리
 */
@WebServlet("/setting")
public class SettingController extends HttpServlet {
    private SettingService settingService;

    /**
     * 서블릿 초기화
     */
    @Override
    public void init() throws ServletException {
        settingService = new SettingService();
        System.out.println("SettingController 초기화 완료");
    }

    /**
     * GET 요청 처리: 요금 정책 목록 조회 및 페이지 표시
     * Header의 "요금 설정" 링크 클릭 시 이 메서드가 호출됨
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 한글 인코딩 설정
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        System.out.println("Controller - GET 요청: 요금 정책 페이지 조회");

        // 리다이렉트로 전달된 메시지 처리
        String success = request.getParameter("success");
        String error = request.getParameter("error");

        if ("true".equals(success)) {
            request.setAttribute("successMessage", "요금 정책이 성공적으로 등록되었습니다.");
        } else if ("insert_failed".equals(error)) {
            request.setAttribute("errorMessage", "요금 정책 등록에 실패했습니다. 입력값을 확인해주세요.");
        } else if ("invalid_number".equals(error)) {
            request.setAttribute("errorMessage", "입력값이 올바르지 않습니다. 숫자를 입력해주세요.");
        } else if ("system_error".equals(error)) {
            request.setAttribute("errorMessage", "시스템 오류가 발생했습니다.");
        }

        try {
            // 요금 정책 목록 조회
            List<SettingVO> policyList = settingService.getAllFeePolicies();
            System.out.println("===== Controller GET - 조회 결과 =====");
            System.out.println("policyList null 여부: " + (policyList == null));
            if (policyList != null) {
                System.out.println("policyList 크기: " + policyList.size());
                for (int i = 0; i < policyList.size(); i++) {
                    SettingVO vo = policyList.get(i);
                    System.out.println("[" + i + "] ID: " + vo.getId() +
                            ", 기본요금: " + vo.getBaseFee() +
                            ", 날짜: " + vo.getUpdateDate());
                }
            }

            // 현재 적용 중인 정책도 함께 조회
            SettingVO currentPolicy = settingService.getCurrentFeePolicy();
            System.out.println("currentPolicy null 여부: " + (currentPolicy == null));
            if (currentPolicy != null) {
                System.out.println("현재 정책 ID: " + currentPolicy.getId());
            }

            // JSP에 데이터 전달
            request.setAttribute("policyList", policyList);
            request.setAttribute("currentPolicy", currentPolicy);

            // 정책 개수 전달
            int policyCount = settingService.getFeePolicyCount();
            request.setAttribute("policyCount", policyCount);
            System.out.println("policyCount: " + policyCount);
            System.out.println("======================================");

            System.out.println("Controller - 조회 완료: " +
                    (policyList != null ? policyList.size() : 0) + "건");

            // setting.jsp로 포워딩
            request.getRequestDispatcher("/WEB-INF/web/setting/setting.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            System.err.println("Controller - GET 요청 처리 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "페이지 로딩 중 오류가 발생했습니다.");
            request.getRequestDispatcher("/WEB-INF/web/setting/setting.jsp")
                    .forward(request, response);
        }
    }

    /**
     * POST 요청 처리: 요금 정책 등록
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 한글 인코딩 설정
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        System.out.println("Controller - POST 요청 받음");

        // action 파라미터로 처리할 작업 구분
        String action = request.getParameter("action");
        System.out.println("Controller - action: " + action);

        if ("insert".equals(action)) {
            handleInsert(request, response);
        } else {
            // action이 없거나 잘못된 경우 목록 페이지로 리다이렉트
            response.sendRedirect(request.getContextPath() + "/setting");
        }
    }

    /**
     * 요금 정책 등록 처리
     */
    private void handleInsert(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("Controller - 요금 정책 등록 처리 시작");

        try {
            // 파라미터 수집 및 변환
            int baseFee = Integer.parseInt(request.getParameter("baseFee"));
            int basicUnitMinute = Integer.parseInt(request.getParameter("basicUnitMinute"));
            int unitFee = Integer.parseInt(request.getParameter("unitFee"));
            int billingUnitMinutes = Integer.parseInt(request.getParameter("billingUnitMinutes"));
            int helpDiscountRate = Integer.parseInt(request.getParameter("helpDiscountRate"));
            int compactDiscountRate = Integer.parseInt(request.getParameter("compactDiscountRate"));
            int gracePeriodMinutes = Integer.parseInt(request.getParameter("gracePeriodMinutes"));
            int maxCapAmount = Integer.parseInt(request.getParameter("maxCapAmount"));

            System.out.println("Controller - 파라미터 수집 완료");
            System.out.println("  기본요금: " + baseFee + "원");
            System.out.println("  기본시간: " + basicUnitMinute + "분");
            System.out.println("  추가요금: " + unitFee + "원");
            System.out.println("  과금단위: " + billingUnitMinutes + "분");
            System.out.println("  장애인할인: " + helpDiscountRate + "%");
            System.out.println("  경차할인: " + compactDiscountRate + "%");
            System.out.println("  회차시간: " + gracePeriodMinutes + "분");
            System.out.println("  최대요금: " + maxCapAmount + "원");

            // DTO 생성
            SettingDTO dto = new SettingDTO();
            dto.setBaseFee(baseFee);
            dto.setBasicUnitMinute(basicUnitMinute);
            dto.setUnitFee(unitFee);
            dto.setBillingUnitMinutes(billingUnitMinutes);
            dto.setHelpDiscountRate(helpDiscountRate);
            dto.setCompactDiscountRate(compactDiscountRate);
            dto.setGracePeriodMinutes(gracePeriodMinutes);
            dto.setMaxCapAmount(maxCapAmount);

            // 서비스 호출하여 등록
            boolean success = settingService.registerFeePolicy(dto);

            if (success) {
                System.out.println("Controller - 요금 정책 등록 성공");
                // 성공 시 리다이렉트 (PRG 패턴)
                response.sendRedirect(request.getContextPath() + "/setting?success=true");
                return;
            } else {
                System.err.println("Controller - 요금 정책 등록 실패");
                // 실패 시 리다이렉트
                response.sendRedirect(request.getContextPath() + "/setting?error=insert_failed");
                return;
            }

        } catch (NumberFormatException e) {
            System.err.println("Controller - 숫자 형식 오류: " + e.getMessage());
            e.printStackTrace();
            // 오류 시 리다이렉트
            response.sendRedirect(request.getContextPath() + "/setting?error=invalid_number");
            return;
        } catch (Exception e) {
            System.err.println("Controller - 예외 발생: " + e.getMessage());
            e.printStackTrace();
            // 오류 시 리다이렉트
            response.sendRedirect(request.getContextPath() + "/setting?error=system_error");
            return;
        }
    }

    /**
     * 서블릿 종료
     */
    @Override
    public void destroy() {
        System.out.println("SettingController 종료");
        super.destroy();
    }
}