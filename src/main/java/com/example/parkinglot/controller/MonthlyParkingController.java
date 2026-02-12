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
import java.util.List;

@WebServlet("/monthly/*")
public class MonthlyParkingController extends HttpServlet {

    private final MonthlyParkingService monthlyParkingService = MonthlyParkingService.INSTANCE;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();

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
                default:
                    list(req, resp);
                    break;
            }

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
    //수정 폼 표시
    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, SQLException{
        int id = Integer.parseInt(req.getParameter("id"));
        MonthlyParkingDTO monthlyParkingDTO = monthlyParkingService.getById(id);

        if (monthlyParkingDTO == null){
            resp.sendRedirect(req.getContextPath()+"/monthly/list?error=notfound"); //경로 수정
            return;
        }
        req.setAttribute("monthlyParkingDTO", monthlyParkingDTO);
        req.getRequestDispatcher("/WEB-INF/views/monthly/monthlyEdit.jsp").forward(req, resp);//경로수정
    }

    //등록 폼 표시
    private void showRegisterForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/monthly/monthlyRegister.jsp").forward(req, resp);
        //경로 수정해야함

    }

    //회원 목록 페이징
    private void list(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        String pageParam = req.getParameter("page");
        int page = 1;
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                //NumberFormatException > 숫자가 아닌 타입을 숫자로 변환할때 생기는 에러 예외처리 구문
                page = 1;
            }
        }
        List<MonthlyParkingDTO> memberList = monthlyParkingService.getPagedList(page);
        int totalPages = monthlyParkingService.getTotalPages();
        int totalCount = monthlyParkingService.getTotalCount();

        if (page > totalPages && totalPages > 0) {
            page = totalPages;
            memberList = monthlyParkingService.getPagedList(page);

        }
        req.setAttribute("memberList", memberList);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalCount", totalCount);

        String msg = req.getParameter("msg");
        if (msg != null) req.setAttribute("msg", msg);
        String error = req.getParameter("error");
        if (error != null) req.setAttribute("error", error);

        req.getRequestDispatcher("/WEB-INF/views/monthly/monthlyList.jsp").forward(req, resp);
        //경로 수정해야함


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        
        try{
            switch (pathInfo){
                case "/register":
                    register(req,resp);
                    break;
                case "/edit":
                    update(req,resp);
                    break;
                case "/delete":
                    delete(req,resp);
                    break;
                default:
                    resp.sendRedirect(req.getContextPath()+"/monthly/list");
                    break;
            }
        }catch (SQLException e){
            throw new ServletException(e);
        }
    }
    private void register(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, IOException {
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) {
    }

    private void update(HttpServletRequest req, HttpServletResponse resp) {
    }
}
