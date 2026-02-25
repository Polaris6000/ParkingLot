package com.example.parkinglot.controller;

import com.example.parkinglot.dto.MonthlyParkingDTO;
import com.example.parkinglot.service.MonthlyParkingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 월정액 회원 관리 컨트롤러
 * GET  - list(회원목록-페이징), register(등록 폼), edit(수정 폼), getNextBeginDate(연장 시작일 조회)
 * POST - register(등록), edit(수정), delete(삭제)
 */
@WebServlet("/monthly/*")
public class MonthlyParkingController extends HttpServlet {

    private final MonthlyParkingService monthlyParkingService = MonthlyParkingService.INSTANCE;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();

        // pathInfo가 null이면 기본으로 목록 페이지
        if (pathInfo == null) pathInfo = "/list";

        try {
            switch (pathInfo) {
                case "/list":
                    list(req, resp);
                    break;
                case "/register":
                    showRegisterForm(req, resp);
                    break;
                case "/edit":
                    showEditForm(req, resp);
                    break;
                case "/getNextBeginDate":
                    // 차량번호로 다음 시작일 + 기존 회원 정보 조회 (등록 폼 자동 입력용 AJAX)
                    getNextBeginDate(req, resp);
                    break;
                default:
                    list(req, resp);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    // 수정 폼 표시
    private void showEditForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, SQLException {
        int id = Integer.parseInt(req.getParameter("id"));
        MonthlyParkingDTO monthlyParkingDTO = monthlyParkingService.getById(id);

        if (monthlyParkingDTO == null) {
            resp.sendRedirect(req.getContextPath() + "/monthly/list?error=notfound");
            return;
        }
        req.setAttribute("monthlyParkingDTO", monthlyParkingDTO);
        req.getRequestDispatcher("/WEB-INF/web/monthly/monthly-edit.jsp").forward(req, resp);
    }

    // 등록 폼 표시
    private void showRegisterForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/web/monthly/monthly-register.jsp").forward(req, resp);
    }

    // 차량번호로 다음 시작일 + 기존 회원 정보 JSON 응답 (등록 폼 AJAX용)
    // 신규: nextBeginDate = 오늘 / 연장: nextBeginDate = 기존 만료일 + 1일
    private void getNextBeginDate(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String plateNumber = req.getParameter("plateNumber");

        LocalDate nextBeginDate = monthlyParkingService.getNextBeginDate(plateNumber);
        MonthlyParkingDTO latest = monthlyParkingService.getLatestByPlate(plateNumber);

        String name        = latest != null ? latest.getName()        : "";
        String phoneNumber = latest != null ? latest.getPhoneNumber() : "";

        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(
                "{\"nextBeginDate\":\"" + nextBeginDate + "\"," +
                "\"name\":\""         + name           + "\"," +
                "\"phoneNumber\":\""  + phoneNumber    + "\"}"
        );
    }

    // 회원 목록 페이징 (검색 조건 + 정렬 + pageSize 포함)
    private void list(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        // 페이지 번호 파싱
        String pageParam = req.getParameter("page");
        int page = 1;
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                // NumberFormatException: 숫자가 아닌 타입을 숫자로 변환할 때 생기는 에러 예외처리 구문
                page = 1;
            }
        }

        // 페이지당 표시 건수 파싱 (허용값: 10, 20, 30 / 그 외는 기본값 사용)
        String pageSizeParam = req.getParameter("pageSize");
        int pageSize = MonthlyParkingService.DEFAULT_PAGE_SIZE;
        if (pageSizeParam != null) {
            try {
                int parsed = Integer.parseInt(pageSizeParam);
                if (parsed == 10 || parsed == 20 || parsed == 30) pageSize = parsed;
            } catch (NumberFormatException ignored) { }
        }

        // 정렬 컬럼/방향 파싱 (기본: begin_date DESC)
        String sortColumn = req.getParameter("sort");
        String sortOrder  = req.getParameter("order");
        if (sortColumn == null || sortColumn.isBlank()) sortColumn = "begin_date";
        if (sortOrder  == null || sortOrder.isBlank())  sortOrder  = "DESC";

        // 검색 키워드 파싱 (없거나 공백이면 null → 전체 조회)
        String keyword = req.getParameter("keyword");
        if (keyword != null && keyword.isBlank()) keyword = null;

        // 검색 여부에 따라 Service 메서드 분기
        List<MonthlyParkingDTO> memberList;
        int totalPages;
        int totalCount;

        if (keyword != null) {
            // 검색 조건 포함 조회
            memberList = monthlyParkingService.getPagedListBySearch(page, pageSize, keyword, sortColumn, sortOrder);
            totalPages = monthlyParkingService.getTotalPagesBySearch(keyword, pageSize);
            totalCount = monthlyParkingService.getTotalCountBySearch(keyword);
        } else {
            // 전체 조회
            memberList = monthlyParkingService.getPagedList(page, pageSize, sortColumn, sortOrder);
            totalPages = monthlyParkingService.getTotalPages(pageSize);
            totalCount = monthlyParkingService.getTotalCount();
        }

        // 현재 페이지가 전체 페이지 수 초과 시 마지막 페이지로 보정
        if (page > totalPages && totalPages > 0) {
            page = totalPages;
            memberList = (keyword != null)
                    ? monthlyParkingService.getPagedListBySearch(page, pageSize, keyword, sortColumn, sortOrder)
                    : monthlyParkingService.getPagedList(page, pageSize, sortColumn, sortOrder);
        }

        req.setAttribute("memberList",   memberList);
        req.setAttribute("currentPage",  page);
        req.setAttribute("totalPages",   totalPages);
        req.setAttribute("totalCount",   totalCount);
        req.setAttribute("keyword",      keyword);    // JSP에서 검색어 유지에 사용
        req.setAttribute("pageSize",     pageSize);   // JSP에서 페이지당 건수 유지에 사용
        req.setAttribute("sort",         sortColumn); // JSP에서 정렬 상태 표시에 사용
        req.setAttribute("order",        sortOrder);  // JSP에서 정렬 방향 표시에 사용

        // message/error 파라미터 전달 (등록/수정/삭제 후 피드백)
        String message = req.getParameter("message");
        if (message != null) req.setAttribute("message", message);
        String error = req.getParameter("error");
        if (error != null) req.setAttribute("error", error);

        req.getRequestDispatcher("/WEB-INF/web/monthly/monthly-list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();

        // POST /monthly 로 요청 시 NullPointerException 방지
        if (pathInfo == null) {
            resp.sendRedirect(req.getContextPath() + "/monthly/list");
            return;
        }

        try {
            switch (pathInfo) {
                case "/register":
                    register(req, resp);
                    break;
                case "/edit":
                    update(req, resp);
                    break;
                case "/delete":
                    delete(req, resp);
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/monthly/list");
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    // 월정액 등록 처리
    // expiryDate 직접 입력 방식 → months(개월 수) 입력 방식으로 변경
    // Service에서 개월 수만큼 행을 분리해서 INSERT
    private void register(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, SQLException {
        String plateNumber = req.getParameter("plateNumber");
        String name        = req.getParameter("name");
        String phoneNumber = req.getParameter("phoneNumber");
        String beginDate   = req.getParameter("beginDate");
        String monthsParam = req.getParameter("months");

        // 필수값 빈 문자열 체크
        if (plateNumber == null || plateNumber.isBlank() ||
            name        == null || name.isBlank()        ||
            phoneNumber == null || phoneNumber.isBlank() ||
            beginDate   == null || beginDate.isBlank()   ||
            monthsParam == null || monthsParam.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/monthly/register?error=empty");
            return;
        }

        LocalDate parsedBeginDate;
        int months;
        try {
            parsedBeginDate = LocalDate.parse(beginDate);
            months = Integer.parseInt(monthsParam);
            if (months < 1 || months > 12) throw new NumberFormatException();
        } catch (DateTimeParseException e) {
            resp.sendRedirect(req.getContextPath() + "/monthly/register?error=invaliddate");
            return;
        } catch (NumberFormatException e) {
            // months가 숫자가 아니거나 범위를 벗어난 경우
            resp.sendRedirect(req.getContextPath() + "/monthly/register?error=invalidmonths");
            return;
        }

        MonthlyParkingDTO monthlyParkingDTO = MonthlyParkingDTO.builder()
                .plateNumber(plateNumber)
                .name(name)
                .phoneNumber(phoneNumber)
                .beginDate(parsedBeginDate)
                .build();

        boolean success = monthlyParkingService.register(monthlyParkingDTO, months);

        if (success) {
            resp.sendRedirect(req.getContextPath() + "/monthly/list?message=registered");
        } else {
            // 날짜 겹침 중복 시
            resp.sendRedirect(req.getContextPath() + "/monthly/register?error=duplicate");
        }
    }

    // 회원 정보 수정 처리 (개별 행 단위 수정 - 개월 수 분리 없음)
    private void update(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        String name        = req.getParameter("name");
        String phoneNumber = req.getParameter("phoneNumber");
        String plateNumber = req.getParameter("plateNumber");
        String beginDate   = req.getParameter("beginDate");
        String expiryDate  = req.getParameter("expiryDate");

        // 필수값 빈 문자열 체크
        if (name        == null || name.isBlank()        ||
            phoneNumber == null || phoneNumber.isBlank() ||
            beginDate   == null || beginDate.isBlank()   ||
            expiryDate  == null || expiryDate.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/monthly/edit?id=" + id + "&error=empty");
            return;
        }

        LocalDate parsedBeginDate;
        LocalDate parsedExpiryDate;
        try {
            parsedBeginDate  = LocalDate.parse(beginDate);
            parsedExpiryDate = LocalDate.parse(expiryDate);
        } catch (DateTimeParseException e) {
            resp.sendRedirect(req.getContextPath() + "/monthly/edit?id=" + id + "&error=invaliddate");
            return;
        }

        // 시작일 > 만료일 유효성 체크
        if (parsedBeginDate.isAfter(parsedExpiryDate)) {
            resp.sendRedirect(req.getContextPath() + "/monthly/edit?id=" + id + "&error=daterange");
            return;
        }

        MonthlyParkingDTO monthlyParkingDTO = MonthlyParkingDTO.builder()
                .id(id)
                .plateNumber(plateNumber)
                .name(name)
                .phoneNumber(phoneNumber)
                .beginDate(parsedBeginDate)
                .expiryDate(parsedExpiryDate)
                .build();

        boolean updated = monthlyParkingService.update(monthlyParkingDTO);
        if (updated) {
            resp.sendRedirect(req.getContextPath() + "/monthly/list?message=updated");
        } else {
            resp.sendRedirect(req.getContextPath() + "/monthly/edit?id=" + id + "&error=fail");
        }
    }

    // 회원 삭제 처리
    private void delete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        monthlyParkingService.delete(id);
        resp.sendRedirect(req.getContextPath() + "/monthly/list?message=deleted");
    }
}