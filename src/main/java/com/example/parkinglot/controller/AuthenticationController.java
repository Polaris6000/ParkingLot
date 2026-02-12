package com.example.parkinglot.controller;

import com.example.parkinglot.dto.AdminDTO;
import com.example.parkinglot.dto.AuthDTO;
import com.example.parkinglot.service.AdminService;
import com.example.parkinglot.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Log4j2
@WebServlet(name = "authenticationController", value="/authentication/*")
public class AuthenticationController extends HttpServlet {
    AdminService adminService = AdminService.getINSTANCE();
    AuthService authService = AuthService.getINSTANCE();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI(); // 요청 URI
        String contextPath = req.getContextPath(); //컨택스트 경로
        String command = requestURI.substring(contextPath.length()); //요청 uri에서 컨택스트 경로를 제거한 명령어

        //command는 루트 경로 이후를 다루게 되는 상황.
        switch (command){
            case "/authentication/signup" -> {
                //여기는 인증에 대한 부분을 작성하면 됌. uuid가 req로 값이 와야 정상이.
                log.info("회원가입 인증하러 들어왓다.");
                String uuid = req.getParameter("uuid");
                AuthDTO authDTO = authService.findAuthInfo(uuid);

                //찾아온 authDTO 확인
                log.info("찾은 인증 정보 : {}" + authDTO);
                //dto 찾았으면 이제 이걸로 확인하고나서 리다리엑트

                if (authDTO == null){
                    req.getRequestDispatcher("/web/admin/TokenCanNotUse.jsp").forward(req,resp);
                }

                //update하는거지
                AdminDTO adminDTO = adminService.findAdminById(authDTO.getId());
                log.info("찾은 계정 정보 : {}" , adminDTO);
                adminDTO.setAuthentication(true);
                adminService.changeAdmin(adminDTO);
                log.info("로그 내용 사용 처리.");
                authService.useUUID(uuid);
                log.info("인증정보 업데이트 완료");
                req.setAttribute("admin",adminDTO);


                req.getRequestDispatcher("/web/admin/SignUpAccept.jsp").forward(req,resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
