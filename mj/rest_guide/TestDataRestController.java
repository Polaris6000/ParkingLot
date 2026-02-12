package com.exmple.parkinglot.controller;

import com.exmple.parkinglot.service.TestDataService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * REST API 방식의 테스트 데이터 컨트롤러
 * 
 * REST의 핵심 원칙:
 * 1. 자원(Resource)을 URI로 표현
 * 2. HTTP 메서드로 행위를 표현 (GET: 조회, POST: 생성, PUT: 수정, DELETE: 삭제)
 * 3. JSON/XML 같은 표준 포맷으로 데이터 전송
 * 4. 상태코드로 응답 결과 표현 (200: 성공, 400: 클라이언트 오류, 500: 서버 오류)
 * 5. Stateless: 각 요청은 독립적
 */
@Log4j2
@WebServlet(name = "TestDataRestController", value = "/api/test/data/*")  // /api 접두사로 REST API임을 명시, /*는 하위 경로 처리
public class TestDataRestController extends HttpServlet {

    private final TestDataService service = TestDataService.INSTANCE;
    
    // JSON 변환을 위한 Gson 객체 (Google의 JSON 라이브러리)
    // Gson은 Java 객체 ↔ JSON 문자열 간 변환을 담당
    private final Gson gson = new Gson();

    /**
     * GET 요청 처리: 통계 정보 조회
     * REST에서 GET은 '조회'를 의미
     * 
     * 예시 요청: GET /api/test/data/statistics
     * 
     * @param request  클라이언트 요청 객체
     * @param response 서버 응답 객체
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // pathInfo: /api/test/data 이후의 경로를 반환
        // 예: /api/test/data/statistics → pathInfo = "/statistics"
        String pathInfo = request.getPathInfo();
        
        try {
            // URI 분석: /statistics 경로인 경우 통계 조회
            if (pathInfo != null && pathInfo.equals("/statistics")) {
                handleGetStatistics(request, response);
            } else {
                // 잘못된 경로로 요청한 경우
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, 
                    "요청한 리소스를 찾을 수 없습니다.");
            }
            
        } catch (Exception e) {
            log.error("GET 요청 처리 중 오류 발생", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "서버 내부 오류가 발생했습니다.");
        }
    }

    /**
     * POST 요청 처리: 테스트 데이터 생성/실행
     * REST에서 POST는 '생성' 또는 '실행'을 의미
     * 
     * 예시 요청들:
     * - POST /api/test/data/entry (입차 데이터 생성)
     * - POST /api/test/data/exit (출차 실행)
     * - POST /api/test/data/monthly (월정액 회원 생성)
     * 
     * @param request  클라이언트 요청 객체
     * @param response 서버 응답 객체
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 한글 깨짐 방지: 요청 본문의 문자 인코딩 설정
        request.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        try {
            // URI 기반 라우팅: 경로에 따라 다른 처리 메서드 호출
            if (pathInfo == null) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "작업 유형이 지정되지 않았습니다.");
                return;
            }

            // switch expression을 사용한 라우팅
            // 각 경로는 특정 리소스/작업을 나타냄
            switch (pathInfo) {
                case "/entry":      // 입차 데이터 대량 생성
                    handleBulkEntry(request, response);
                    break;
                    
                case "/exit":       // 출차 대량 처리
                    handleBulkExit(request, response);
                    break;
                    
                case "/monthly":    // 월정액 회원 대량 등록
                    handleBulkMonthly(request, response);
                    break;
                    
                case "/fee-policy": // 요금 정책 대량 등록
                    handleBulkFeePolicy(request, response);
                    break;
                    
                default:
                    // 정의되지 않은 경로
                    sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                        "알 수 없는 작업입니다: " + pathInfo);
            }
            
        } catch (Exception e) {
            log.error("POST 요청 처리 중 오류 발생", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "서버 내부 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * DELETE 요청 처리: 데이터 삭제
     * REST에서 DELETE는 '삭제'를 의미
     * 
     * 예시 요청들:
     * - DELETE /api/test/data/monthly?count=5 (월정액 회원 5명 삭제)
     * - DELETE /api/test/data/fee-policy?count=3 (요금 정책 3건 삭제)
     * - DELETE /api/test/data/all (전체 데이터 삭제)
     * 
     * @param request  클라이언트 요청 객체
     * @param response 서버 응답 객체
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "삭제 대상이 지정되지 않았습니다.");
                return;
            }

            switch (pathInfo) {
                case "/monthly":    // 월정액 회원 랜덤 삭제
                    handleDeleteMonthly(request, response);
                    break;
                    
                case "/fee-policy": // 요금 정책 랜덤 삭제
                    handleDeleteFeePolicy(request, response);
                    break;
                    
                case "/all":        // 전체 주차 데이터 초기화
                    handleClearAll(request, response);
                    break;
                    
                default:
                    sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                        "알 수 없는 삭제 대상입니다: " + pathInfo);
            }
            
        } catch (Exception e) {
            log.error("DELETE 요청 처리 중 오류 발생", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "서버 내부 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // ==================== GET 핸들러 메서드 ====================

    /**
     * 통계 정보 조회 처리
     * 
     * 응답 예시:
     * {
     *   "success": true,
     *   "data": {
     *     "statistics": "현재 주차: 15대\n금일 매출: 50,000원"
     *   }
     * }
     */
    private void handleGetStatistics(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            // 서비스 레이어에서 통계 데이터 가져오기
            String statistics = service.getStatistics();
            
            // 응답 데이터 구성
            Map<String, Object> data = new HashMap<>();
            data.put("statistics", statistics != null ? statistics : "통계 없음");
            
            // 성공 응답 전송 (HTTP 200)
            sendSuccessResponse(response, data);
            
        } catch (Exception e) {
            log.error("통계 조회 중 오류", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "통계 조회 실패");
        }
    }

    // ==================== POST 핸들러 메서드 ====================

    /**
     * 입차 데이터 대량 생성 처리
     * 
     * 요청 파라미터:
     * - count: 생성할 입차 데이터 개수
     * 
     * 응답 예시:
     * {
     *   "success": true,
     *   "message": "10대 입차 완료",
     *   "data": {
     *     "inserted": 10
     *   }
     * }
     */
    private void handleBulkEntry(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            // 쿼리 파라미터에서 count 값 읽기
            // REST에서는 쿼리 파라미터로 필터/옵션을 전달
            int count = getIntParameter(request, "count", 1);
            
            // 비즈니스 로직 실행
            int inserted = service.bulkInsertParking(count);
            
            // 응답 데이터 구성
            Map<String, Object> data = new HashMap<>();
            data.put("inserted", inserted);
            
            // 성공 응답 (HTTP 201: Created)
            // 201은 리소스가 성공적으로 생성되었음을 의미
            sendSuccessResponse(response, HttpServletResponse.SC_CREATED,
                inserted + "대 입차 완료", data);
            
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "잘못된 count 값입니다.");
        } catch (Exception e) {
            log.error("입차 데이터 생성 중 오류", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "입차 처리 실패");
        }
    }

    /**
     * 출차 데이터 대량 처리
     */
    private void handleBulkExit(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            int count = getIntParameter(request, "count", 1);
            int exited = service.bulkExitParking(count);
            
            Map<String, Object> data = new HashMap<>();
            data.put("exited", exited);
            
            sendSuccessResponse(response, exited + "대 출차 완료", data);
            
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "잘못된 count 값입니다.");
        } catch (Exception e) {
            log.error("출차 데이터 처리 중 오류", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "출차 처리 실패");
        }
    }

    /**
     * 월정액 회원 대량 등록 처리
     */
    private void handleBulkMonthly(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            int count = getIntParameter(request, "count", 1);
            int inserted = service.bulkInsertMonthlyMembers(count);
            
            Map<String, Object> data = new HashMap<>();
            data.put("inserted", inserted);
            
            sendSuccessResponse(response, HttpServletResponse.SC_CREATED,
                inserted + "명 월정액 회원 등록 완료", data);
            
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "잘못된 count 값입니다.");
        } catch (Exception e) {
            log.error("월정액 회원 등록 중 오류", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "월정액 회원 등록 실패");
        }
    }

    /**
     * 요금 정책 대량 등록 처리
     */
    private void handleBulkFeePolicy(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            int count = getIntParameter(request, "count", 1);
            int inserted = service.bulkInsertFeePolicies(count);
            
            Map<String, Object> data = new HashMap<>();
            data.put("inserted", inserted);
            
            sendSuccessResponse(response, HttpServletResponse.SC_CREATED,
                count + "건 요금 정책 등록 완료", data);
            
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "잘못된 count 값입니다.");
        } catch (Exception e) {
            log.error("요금 정책 등록 중 오류", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "요금 정책 등록 실패");
        }
    }

    // ==================== DELETE 핸들러 메서드 ====================

    /**
     * 월정액 회원 랜덤 삭제 처리
     * 
     * 요청 파라미터:
     * - count: 삭제할 회원 수
     */
    private void handleDeleteMonthly(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            int count = getIntParameter(request, "count", 1);
            int deleted = service.randomDeleteData("monthly_parking", count);
            
            Map<String, Object> data = new HashMap<>();
            data.put("deleted", deleted);
            
            sendSuccessResponse(response, deleted + "명 월정액 회원 삭제 완료", data);
            
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "잘못된 count 값입니다.");
        } catch (Exception e) {
            log.error("월정액 회원 삭제 중 오류", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "월정액 회원 삭제 실패");
        }
    }

    /**
     * 요금 정책 랜덤 삭제 처리
     */
    private void handleDeleteFeePolicy(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            int count = getIntParameter(request, "count", 1);
            int deleted = service.randomDeleteData("fee_policy", count);
            
            Map<String, Object> data = new HashMap<>();
            data.put("deleted", deleted);
            
            sendSuccessResponse(response, deleted + "건 요금 정책 삭제 완료", data);
            
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "잘못된 count 값입니다.");
        } catch (Exception e) {
            log.error("요금 정책 삭제 중 오류", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "요금 정책 삭제 실패");
        }
    }

    /**
     * 전체 주차 데이터 초기화 처리
     * 
     * 쿼리 파라미터 없음
     */
    private void handleClearAll(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            service.clearAllParkingData();
            
            sendSuccessResponse(response, "전체 주차 데이터 초기화 완료", null);
            
        } catch (Exception e) {
            log.error("데이터 초기화 중 오류", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "데이터 초기화 실패");
        }
    }

    // ==================== 응답 헬퍼 메서드 ====================

    /**
     * 성공 응답 전송 (HTTP 200)
     * 
     * JSON 응답 구조:
     * {
     *   "success": true,
     *   "message": "성공 메시지",
     *   "data": { ... }
     * }
     * 
     * @param response 응답 객체
     * @param data     응답에 포함할 데이터 (Map, List, DTO 등)
     */
    private void sendSuccessResponse(HttpServletResponse response, Map<String, Object> data)
            throws IOException {
        sendSuccessResponse(response, HttpServletResponse.SC_OK, null, data);
    }

    /**
     * 성공 응답 전송 (메시지 포함)
     */
    private void sendSuccessResponse(HttpServletResponse response, String message, Map<String, Object> data)
            throws IOException {
        sendSuccessResponse(response, HttpServletResponse.SC_OK, message, data);
    }

    /**
     * 성공 응답 전송 (상태코드, 메시지, 데이터 모두 지정)
     * 
     * @param response   응답 객체
     * @param statusCode HTTP 상태 코드 (200, 201 등)
     * @param message    성공 메시지
     * @param data       응답 데이터
     */
    private void sendSuccessResponse(HttpServletResponse response, int statusCode,
                                     String message, Map<String, Object> data)
            throws IOException {
        
        // HTTP 상태 코드 설정
        response.setStatus(statusCode);
        
        // 응답 본문 구성
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", true);  // 성공 여부
        
        if (message != null) {
            responseBody.put("message", message);  // 메시지
        }
        
        if (data != null) {
            responseBody.put("data", data);  // 실제 데이터
        }
        
        // JSON 응답 전송
        sendJsonResponse(response, responseBody);
    }

    /**
     * 에러 응답 전송
     * 
     * JSON 응답 구조:
     * {
     *   "success": false,
     *   "error": {
     *     "message": "오류 메시지"
     *   }
     * }
     * 
     * @param response   응답 객체
     * @param statusCode HTTP 상태 코드 (400, 404, 500 등)
     * @param message    에러 메시지
     */
    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message)
            throws IOException {
        
        // HTTP 상태 코드 설정
        response.setStatus(statusCode);
        
        // 응답 본문 구성
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", false);  // 실패 표시
        
        Map<String, String> error = new HashMap<>();
        error.put("message", message);
        responseBody.put("error", error);
        
        // JSON 응답 전송
        sendJsonResponse(response, responseBody);
    }

    /**
     * JSON 형식으로 응답 전송하는 공통 메서드
     * 
     * 핵심 과정:
     * 1. Content-Type을 application/json으로 설정 (클라이언트에게 JSON임을 알림)
     * 2. 문자 인코딩을 UTF-8로 설정 (한글 깨짐 방지)
     * 3. Java 객체를 JSON 문자열로 변환 (Gson 사용)
     * 4. 응답 본문에 작성
     * 
     * @param response 응답 객체
     * @param data     JSON으로 변환할 데이터 (Map, List, DTO 등)
     */
    private void sendJsonResponse(HttpServletResponse response, Object data)
            throws IOException {
        
        // 응답 헤더 설정
        response.setContentType("application/json");  // MIME 타입: JSON
        response.setCharacterEncoding("UTF-8");       // 문자 인코딩: UTF-8
        
        // CORS 설정 (필요시)
        // 프론트엔드가 다른 도메인에서 실행될 때 필요
        // response.setHeader("Access-Control-Allow-Origin", "*");
        // response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        
        // Java 객체 → JSON 문자열 변환
        String jsonResponse = gson.toJson(data);
        
        // 응답 본문에 작성
        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
        
        log.debug("JSON 응답 전송: {}", jsonResponse);
    }

    // ==================== 유틸리티 메서드 ====================

    /**
     * 요청 파라미터에서 정수 값을 안전하게 추출
     * 
     * @param request      요청 객체
     * @param paramName    파라미터 이름
     * @param defaultValue 파라미터가 없을 때 기본값
     * @return 파라미터 값 또는 기본값
     * @throws NumberFormatException 숫자 형식이 잘못된 경우
     */
    private int getIntParameter(HttpServletRequest request, String paramName, int defaultValue)
            throws NumberFormatException {
        
        String value = request.getParameter(paramName);
        
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        
        return Integer.parseInt(value);
    }
}
