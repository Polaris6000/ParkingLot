package com.example.parkinglot.controller;

import com.example.parkinglot.dto.FeePolicyDTO;
import com.example.parkinglot.dto.ParkingCarDTO;
import com.example.parkinglot.service.DashboardService;
import com.google.gson.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Log4j2
@WebServlet(name = "DashboardController", value = "/dashboard/*")
public class DashboardController extends HttpServlet {

    private final DashboardService dashboardService = DashboardService.INSTANCE;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI(); // 요청 URI
        String contextPath = req.getContextPath(); //컨택스트 경로
        String command = requestURI.substring(contextPath.length()); //요청 uri에서 컨택스트 경로를 제거한 명령어

        log.info(command);

        switch (command) {
            case "/dashboard/main" -> {
                log.info("대시보드 메인화면으로 이동.");
                //여기서 현재 주차 대수, 금일 방문자 수를 세어서 넘겨야함.
                log.info("정산 미리보기 하기 위해서 값을 가져오기");
                FeePolicyDTO feePolicyDTO = dashboardService.getFeePolicy();

                log.info("현재 차량관련 정보를 가져오기");
                List<ParkingCarDTO> parkings = dashboardService.getCurrentParking();
//                List<ParkingCarDTO> parkings = new ArrayList<>();
//                List<String> kind = List.of("normal", "disabled", "light", "monthly");
//                for (int i = 1; i < 10; i++) {
//                    parkings.add(ParkingCarDTO.builder()
//                            .id(i)
//                            .parkingSpot("A0" + i)
//                            .plateNumber("test" + i)
//                            .entryTime(LocalDateTime.now())
//                            .kindOfDiscount(kind.get(i % 4))
//                            .build());
//                }

                int currentParking = parkings.size();
                int todayVisitor = dashboardService.getTodayVisitor();

                log.info("json 처리를 위해 gson 객체 생성. localdatetime 처리를 위해서 builder로 인자 추가");
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> {
                            // LocalDateTime을 문자열로 변환 (예: "2026-02-13 14:30:00")
                            return new JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        })
                        .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> {
                            // 문자열을 다시 LocalDateTime으로 변환
                            return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        })
                        .create();


                String parkingsJson = gson.toJson(parkings);
                String feeJson = gson.toJson(feePolicyDTO);

                log.info("각 속성을 request에 지정");
                req.setAttribute("currentParking", currentParking);
                req.setAttribute("todayVisitor", todayVisitor);

                req.setAttribute("carData", parkingsJson);
                req.setAttribute("feeData", feeJson);


                log.info("대쉬보드로 정보를 전송");
                req.getRequestDispatcher("/WEB-INF/web/dashboard.jsp").forward(req, resp);
            }
            default -> resp.sendRedirect("/dashboard/main");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String requestURI = req.getRequestURI(); // 요청 URI
        String contextPath = req.getContextPath(); //컨택스트 경로
        String command = requestURI.substring(contextPath.length()); //요청 uri에서 컨택스트 경로를 제거한 명령어

        log.info(command);

        switch (command) {
            case "/dashboard/enter" -> {
                log.info("대시보드 입차 처리로 이동.");

//                Enumeration<String> data = req.getParameterNames();
//                while (data.hasMoreElements()) {
//                    String attrName = data.nextElement();
//                    log.info(attrName + ":" +  req.getParameter(attrName));
//                }
                /*
                전달받은 값 : carNumber, stat, discount, cost
                이걸로 dto구성해서 service에 던지기
                 */

                //차량의 상태
                String stat = req.getParameter("stat").substring(0,2);
                log.info("이 차량은 어떻게 오셨나? : {}", stat);
                //주차구역
                String spot = req.getParameter("stat").substring(req.getParameter("stat").indexOf("(") + 1, req.getParameter("stat").indexOf(")"));
                log.info("주차한 위치 : {}",spot);

                ParkingCarDTO parkingCarDTO = ParkingCarDTO.builder()
                        .plateNumber(req.getParameter("carNumber"))
                        .parkingSpot(spot)
                        .kindOfDiscount(req.getParameter("discount"))
                        .build();

                log.info("이 정보들 등록 해 '줘'");
                dashboardService.addParkingLog(parkingCarDTO);

                log.info("등록 끝났으니까 이제 원래 페이지로 다시 돌아가.");
                resp.sendRedirect("/dashboard/main");
            }
            case "/dashboard/exit" -> {
                log.info("대시보드 출차 처리로 이동.");

                Enumeration<String> data = req.getParameterNames();
                while (data.hasMoreElements()) {
                    String attrName = data.nextElement();
                    log.info(attrName + ":" +  req.getParameter(attrName));
                }
                /*
                전달받은 값 : id, enterTimeBackUp, carNumber, stat, discount, cost
                이걸로 dto구성해서 service에 던지기
                 */
                //차량의 상태
                String stat = req.getParameter("stat").substring(0,2);
                log.info("이 차량은 어떻게 오셨나? : {}", stat);
                //주차구역
//                String spot = req.getParameter("stat").substring(req.getParameter("stat").indexOf("(") + 1, req.getParameter("stat").indexOf(")"));
//                log.info("주차한 위치 : {}",spot);

                ParkingCarDTO parkingCarDTO = ParkingCarDTO.builder()
                        .id(Integer.valueOf(req.getParameter("id")))
//                        .plateNumber(req.getParameter("carNumber"))
//                        .parkingSpot(spot)
                        .kindOfDiscount(req.getParameter("discount"))
                        .entryTime(LocalDateTime.parse(req.getParameter("enterTimeBackUp").replace(" ","T")))
                        .exitTime(LocalDateTime.parse(req.getParameter("payTime")))
                        .cost(Integer.valueOf(req.getParameter("cost").replace("원","")))
//                        .payTime(Integer.valueOf(req.getParameter("payTime")))
                        .build();

                log.info("이 정보들 처리 해 '줘'");
                dashboardService.payParkingLog(parkingCarDTO);

                log.info("등록 끝났으니까 이제 원래 페이지로 다시 돌아가.");
                resp.sendRedirect("/dashboard/main");
            }
        }


    }
}