package com.example.parkinglot.controller;

import com.example.parkinglot.dao.*;
import com.example.parkinglot.dto.*;
import com.example.parkinglot.service.StasticsService;
import com.example.parkinglot.vo.DashboardStatsVO;
import com.example.parkinglot.vo.ParkingSpotVO;
import com.example.parkinglot.service.DashboardService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


 //대시보드 관리 컨트롤러
@Log4j2
@WebServlet(name = "dashboardController", urlPatterns = {"/dashboard/*"})
public class DashBoardController extends HttpServlet {

    private final ParkingTimesDAO parkingTimesDAO = new ParkingTimesDAOImpl();
    private final CarInfoDAO carInfoDAO = new CarInfoDAOImpl();
    private final MonthlyParkingDAO monthlyParkingDAO = new MonthlyParkingDAOImpl();
    private final StasticsService stasticsService = new StasticsService();
    private DashboardService service = DashboardService.INSTANCE;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // URL에서 서브 경로 추출 (예: /api/dashboard/stats -> /stats)
        String pathInfo = request.getPathInfo();
        log.info("API 요청 경로: {}", pathInfo);

        // 응답 헤더 설정 (모든 응답은 JSON)
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // 경로별 처리 분기
            if (pathInfo == null || pathInfo.equals("/")) {
                // 기본 경로: 에러 응답
                sendErrorResponse(response, 400, "잘못된 API 경로입니다.");

            } else if (pathInfo.equals("/stats")) {
                // 통계 정보만 조회
                handleGetStats(request, response);

            } else if (pathInfo.equals("/spots")) {
                // 주차 구역 정보만 조회
                handleGetSpots(request, response);

            } else if (pathInfo.equals("/all")) {
                // 통계 + 주차 구역 통합 조회
                handleGetAll(request, response);

            } else if (pathInfo.equals("/search")) {
                // 차량 번호 검색
                handleSearch(request, response);

            } else {
                // 존재하지 않는 경로
                sendErrorResponse(response, 404, "존재하지 않는 API입니다.");
            }

        } catch (Exception e) {
            log.error("API 처리 중 오류 발생", e);
            sendErrorResponse(response, 500, "서버 오류가 발생했습니다: " + e.getMessage());
        }


//        String pathInfo = request.getPathInfo();
//
//        try {
//            if (pathInfo == null || pathInfo.equals("/")) {
//                // 메인 대시보드
//                showMainDashboard(request, response);
//            } else if ("/cars".equals(pathInfo)) {
//                // 현재 주차 차량 목록
//                showCurrentCars(request, response);
//            } else if ("/members".equals(pathInfo)) {
//                // 월주차 회원 목록
//                showMembers(request, response);
//            }
//        } catch (Exception e) {
//            request.setAttribute("error", e.getMessage());
//            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
//        }
    }



     /**
      * 통계 정보 조회 API
      *
      * 요청: GET /api/dashboard/stats
      * 응답 예시:
      * {
      *   "success": true,
      *   "data": {
      *     "currentParkedCount": 5,
      *     "totalSpots": 20,
      *     "todayVisitorCount": 23,
      *     "availableSpots": 15
      *   }
      * }
      */
     private void handleGetStats(HttpServletRequest request, HttpServletResponse response)
             throws Exception {

         log.info("통계 정보 조회 API 호출");

         // 서비스에서 통계 데이터 조회
         DashboardStatsVO stats = service.getStats();

         // JSON 응답 생성
         StringBuilder json = new StringBuilder();
         json.append("{");
         json.append("\"success\":true,");
         json.append("\"data\":{");
         json.append("\"currentParkedCount\":").append(stats.getCurrentParkedCount()).append(",");
         json.append("\"totalSpots\":").append(stats.getTotalSpots()).append(",");
         json.append("\"todayVisitorCount\":").append(stats.getTodayVisitorCount()).append(",");
         json.append("\"availableSpots\":").append(stats.getTotalSpots() - stats.getCurrentParkedCount());
         json.append("}");
         json.append("}");

         // 응답 전송
         sendJsonResponse(response, json.toString());
         log.info("통계 정보 응답 완료");
     }

     /**
      * 주차 구역 정보 조회 API
      *
      * 요청: GET /api/dashboard/spots
      * 응답 예시:
      * {
      *   "success": true,
      *   "data": [
      *     {
      *       "spotNumber": "A01",
      *       "occupied": true,
      *       "carId": 123,
      *       "plateNumber": "12가3456",
      *       "entryTime": "2026-02-11 09:30:00"
      *     },
      *     ...
      *   ],
      *   "count": 20
      * }
      */
     private void handleGetSpots(HttpServletRequest request, HttpServletResponse response)
             throws Exception {

         log.info("주차 구역 정보 조회 API 호출");

         // 서비스에서 전체 주차 구역 데이터 조회
         List<ParkingSpotVO> parkingSpots = service.getAllParkingSpots();

         // JSON 응답 생성
         StringBuilder json = new StringBuilder();
         json.append("{");
         json.append("\"success\":true,");
         json.append("\"count\":").append(parkingSpots.size()).append(",");
         json.append("\"data\":");
         json.append(parkingSpotsToJson(parkingSpots));
         json.append("}");

         // 응답 전송
         sendJsonResponse(response, json.toString());
         log.info("주차 구역 정보 응답 완료 (총 {} 개)", parkingSpots.size());
     }

     /**
      * 통합 데이터 조회 API (통계 + 주차 구역)
      *
      * 요청: GET /api/dashboard/all
      * 응답: 통계 정보와 주차 구역 정보를 한 번에 반환
      *
      * 용도: 페이지 최초 로딩 시 한 번의 요청으로 모든 데이터 가져오기
      */
     private void handleGetAll(HttpServletRequest request, HttpServletResponse response)
             throws Exception {

         log.info("통합 데이터 조회 API 호출");

         // 통계 및 주차 구역 데이터 조회
         DashboardStatsVO stats = service.getStats();
         List<ParkingSpotVO> parkingSpots = service.getAllParkingSpots();

         // JSON 응답 생성
         StringBuilder json = new StringBuilder();
         json.append("{");
         json.append("\"success\":true,");

         // 통계 정보
         json.append("\"stats\":{");
         json.append("\"currentParkedCount\":").append(stats.getCurrentParkedCount()).append(",");
         json.append("\"totalSpots\":").append(stats.getTotalSpots()).append(",");
         json.append("\"todayVisitorCount\":").append(stats.getTodayVisitorCount()).append(",");
         json.append("\"availableSpots\":").append(stats.getTotalSpots() - stats.getCurrentParkedCount());
         json.append("},");

         // 주차 구역 정보
         json.append("\"parkingSpots\":");
         json.append(parkingSpotsToJson(parkingSpots));

         json.append("}");

         // 응답 전송
         sendJsonResponse(response, json.toString());
         log.info("통합 데이터 응답 완료");
     }

     /**
      * 차량 번호 검색 API
      *
      * 요청: GET /api/dashboard/search?keyword=1234
      *
      * 응답 예시 (검색 성공):
      * {
      *   "success": true,
      *   "data": {
      *     "spotNumber": "A05",
      *     "plateNumber": "12가1234",
      *     "entryTime": "2026-02-11 10:00:00",
      *     ...
      *   }
      * }
      *
      * 응답 예시 (검색 실패):
      * {
      *   "success": false,
      *   "message": "해당 차량을 찾을 수 없습니다."
      * }
      */
     private void handleSearch(HttpServletRequest request, HttpServletResponse response)
             throws Exception {

         // 검색 키워드 추출
         String keyword = request.getParameter("keyword");
         log.info("차량 검색 API 호출 - 검색어: {}", keyword);

         // 검색어 유효성 검사
         if (keyword == null || keyword.trim().isEmpty()) {
             sendErrorResponse(response, 400, "검색어를 입력해주세요.");
             return;
         }

         // 서비스에서 검색 수행
         ParkingSpotVO searchResult = service.searchByPlateNumber(keyword.trim());

         StringBuilder json = new StringBuilder();
         json.append("{");

         if (searchResult != null) {
             // 검색 성공
             json.append("\"success\":true,");
             json.append("\"data\":");
             json.append(parkingSpotToJson(searchResult));
             log.info("차량 검색 성공: {} - {}",
                     searchResult.getPlateNumber(),
                     searchResult.getSpotNumber());
         } else {
             // 검색 실패
             json.append("\"success\":false,");
             json.append("\"message\":\"해당 차량을 찾을 수 없습니다.\"");
             log.info("차량 검색 실패 - 검색어: {}", keyword);
         }

         json.append("}");

         // 응답 전송
         sendJsonResponse(response, json.toString());
     }

     // ========== 유틸리티 메서드 ==========

     /**
      * ParkingSpotVO 리스트를 JSON 배열 문자열로 변환
      *
      * @param spots 주차 구역 리스트
      * @return JSON 배열 문자열
      */
     private String parkingSpotsToJson(List<ParkingSpotVO> spots) {
         StringBuilder json = new StringBuilder();
         json.append("[");

         for (int i = 0; i < spots.size(); i++) {
             json.append(parkingSpotToJson(spots.get(i)));

             // 마지막 요소가 아니면 쉼표 추가
             if (i < spots.size() - 1) {
                 json.append(",");
             }
         }

         json.append("]");
         return json.toString();
     }

     /**
      * ParkingSpotVO 객체를 JSON 문자열로 변환
      *
      * @param spot 주차 구역 객체
      * @return JSON 객체 문자열
      */
     private String parkingSpotToJson(ParkingSpotVO spot) {
         StringBuilder json = new StringBuilder();
         json.append("{");

         // spotNumber (필수)
         json.append("\"spotNumber\":\"").append(escapeJson(spot.getSpotNumber())).append("\",");

         // occupied (필수)
         json.append("\"occupied\":").append(spot.isOccupied()).append(",");

         // carId (nullable)
         json.append("\"carId\":");
         if (spot.getCarId() != null) {
             json.append(spot.getCarId());
         } else {
             json.append("null");
         }
         json.append(",");

         // plateNumber (nullable)
         json.append("\"plateNumber\":");
         if (spot.getPlateNumber() != null) {
             json.append("\"").append(escapeJson(spot.getPlateNumber())).append("\"");
         } else {
             json.append("null");
         }
         json.append(",");

         // entryTime (nullable)
         json.append("\"entryTime\":");
         if (spot.getEntryTime() != null) {
             json.append("\"").append(escapeJson(spot.getEntryTime().toString())).append("\"");
         } else {
             json.append("null");
         }

         json.append("}");
         return json.toString();
     }

     /**
      * JSON 문자열 이스케이프 처리
      * 특수 문자(", \, 개행 등)를 안전하게 처리
      *
      * @param str 원본 문자열
      * @return 이스케이프 처리된 문자열
      */
     private String escapeJson(String str) {
         if (str == null) {
             return "";
         }

         return str.replace("\\", "\\\\")  // 백슬래시
                 .replace("\"", "\\\"")  // 쌍따옴표
                 .replace("\n", "\\n")   // 개행
                 .replace("\r", "\\r")   // 캐리지 리턴
                 .replace("\t", "\\t");  // 탭
     }

     /**
      * JSON 응답 전송
      *
      * @param response HttpServletResponse 객체
      * @param jsonString JSON 문자열
      */
     private void sendJsonResponse(HttpServletResponse response, String jsonString)
             throws IOException {

         PrintWriter out = response.getWriter();
         out.print(jsonString);
         out.flush();
     }

     /**
      * 에러 응답 전송
      *
      * @param response HttpServletResponse 객체
      * @param statusCode HTTP 상태 코드 (400, 404, 500 등)
      * @param message 에러 메시지
      */
     private void sendErrorResponse(HttpServletResponse response, int statusCode, String message)
             throws IOException {

         response.setStatus(statusCode);

         StringBuilder json = new StringBuilder();
         json.append("{");
         json.append("\"success\":false,");
         json.append("\"error\":\"").append(escapeJson(message)).append("\"");
         json.append("}");

         sendJsonResponse(response, json.toString());
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

        List<MonthlyParkingDTO> members = monthlyParkingDAO.selectAll();

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

        MonthlyParkingDTO member = MonthlyParkingDTO.builder()
                .plateNumber(plateNumber)
                .name(name)
                .phoneNumber(phoneNumber)
                .beginDate(LocalDate.parse(beginDate))
                .expiryDate(LocalDate.parse(expiryDate))
                .build();

        monthlyParkingDAO.insert(member);

        response.sendRedirect(request.getContextPath() + "/dashboard/members");
    }
}