package com.example.parkinglot.controller;

import com.example.parkinglot.dto.AdminDTO;
import com.example.parkinglot.dto.AuthDTO;
import com.example.parkinglot.enums.AuthKind;
import com.example.parkinglot.service.AdminService;
import com.example.parkinglot.service.AuthService;
import com.example.parkinglot.service.MailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


@Log4j2
@WebServlet(name = "LoginController", value = {"/admin/*"})
public class AdminController extends HttpServlet {
    AdminService adminService = AdminService.getINSTANCE();
    MailService mailService = MailService.getINSTANCE();
    AuthService authService = AuthService.getINSTANCE();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI(); // 요청 URI
        String contextPath = req.getContextPath(); //컨택스트 경로
        String command = requestURI.substring(contextPath.length()); //요청 uri에서 컨택스트 경로를 제거한 명령어

        //command는 루트 경로 이후를 다루게 되는 상황.
        switch (command) {
            case "/admin/login" -> {
                log.info("로그인 페이지 이동2");
                req.getRequestDispatcher("/WEB-INF/web/admin/Login.jsp").forward(req, resp);
            }
            case "/admin/findinfo" -> {
                log.info("아이디, 비번 찾기로 이동");
                req.getRequestDispatcher("/WEB-INF/web/admin/FindInfo.jsp").forward(req, resp);
            }
            case "/admin/signup" -> {
                log.info("회원가입 들어감.");
                List<AdminDTO> adminDTOList = adminService.mastersInfo();
                req.setAttribute("masters", adminDTOList);
                req.getRequestDispatcher("/WEB-INF/web/admin/SignUp.jsp").forward(req, resp);
            }
            case "/admin/signupfin" -> {
                log.info("회원가입 완료됨.");
                req.getRequestDispatcher("/WEB-INF/web/admin/SignUpFin.jsp").forward(req, resp);
            }
            case "/admin/changepw" -> {
                log.info("비밀번호 변경을 위해 들어감.");
                req.getRequestDispatcher("/WEB-INF/web/admin/ChangePw.jsp").forward(req, resp);
            }
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8"); //글자 깨짐을 방지.
        String requestURI = req.getRequestURI(); // 요청 URI
        String contextPath = req.getContextPath(); //컨택스트 경로
        String command = requestURI.substring(contextPath.length()); //요청 uri에서 컨택스트 경로를 제거한 명령어

        //command는 루트 경로 이후를 다루게 되는 상황.
        switch (command) {
            case "/admin/signup" -> {
                log.info("잘 전달됌. ");
//                Enumeration<String> data = req.getParameterNames();
//                while (data.hasMoreElements()) {
//                    String attrName = data.nextElement();
//                    log.info(attrName);
//
//                }
                /*
                전달받은 값 : email, id, name, pw, master > to로 보낼꺼
                이걸로 dto구성해서 service에 던지기
                 */
                AdminDTO adminDTO = AdminDTO.builder()
                        .id(req.getParameter("id"))
                        .password(BCrypt.hashpw(req.getParameter("pw"), BCrypt.gensalt())) //암호화 하여 암호를 저장.
                        .name(req.getParameter("name"))
                        .email(req.getParameter("email"))
                        .authorization("user")
                        .authentication(false)
                        .build();

                adminService.signUp(adminDTO);

                log.info("인증을 위한  uuid 생성");
                String uuid = String.valueOf(UUID.randomUUID());

                AuthDTO authDTO = AuthDTO.builder()
                        .token(uuid)
                        .id(adminDTO.getId())
                        .use(AuthKind.SIGNUP.getAuthKind())
                        .build();
                log.info("생성된 인증 정보를 확인 : {}", authDTO);

                log.info("인증 토큰을 저장");
                authService.addAuthToken(authDTO);

                log.info("해당 내용을 정리해서 관리자(master)등급에게 보내고 인증을 받아야함.");
                mailService.sendAuthEmail(req.getParameter("master"), AuthKind.SIGNUP, adminDTO, uuid);

                log.info("서비스까지 전달 완료");
                req.getRequestDispatcher("/WEB-INF/web/admin/SignUpFin.jsp").forward(req, resp);
            }

            case "/admin/login" -> {
                log.info("로그인 승인을 위해서 이동.");
                //아이디랑 비밀번호 확인

                AdminDTO adminDTO = AdminDTO.builder()
                        .id(req.getParameter("id"))
                        .password(req.getParameter("pw"))
                        .build();
                log.info("받아온 파라미터 확인 : {}", adminDTO);

                log.info("아이디를 비교해서 옳은 값 가져오기.");
                adminDTO = authService.loginPass(adminDTO);

                log.info("아이디가 잘 채워졌나 확인 : {}", adminDTO);

                if (adminDTO != null) {
                    log.info("로그인 성공");
                    //이후로 로그인 되어 있음을 증명. >> 쿠키와 세션을 이용하기.

                    resp.sendRedirect("/dashboard");
                } else {
                    log.info("로그인 실패");
                    req.setAttribute("error", "1");

                    req.getRequestDispatcher("/WEB-INF/web/admin/Login.jsp").forward(req, resp);
                }

            }
            case "/admin/findid" -> {
                log.info("아이디 찾기를 위해 들어온 위치.");

                String email = req.getParameter("find_id_email");
                AdminDTO adminDTO = adminService.findAdminByEmail(email);
                String result = "?result=";

                log.info("이메일로 찾은 관리자 계정 : {}", adminDTO);
                if (adminDTO != null) {
                    log.info("이메일로 계정을 찾음. 특성 추가 후 메일 발송.");
                    result += "success";
                    mailService.sendFindEmail(email, AuthKind.FINDID, adminDTO.getId());

                } else {
                    log.info("찾은 값이 없으면 실패로 보내기.");
                    result += "fail";

                }
                log.info("findinfo로 돌아가서 alert 만들기");
                resp.sendRedirect("/WEB-INF/admin/findinfo" + result);
            }
            case "/admin/findpw" -> {
                log.info("비밀번호 찾기를 위해 들어온 위치.");

                String id = req.getParameter("find_pw_id");
                String email = req.getParameter("find_pw_email");

                AdminDTO adminDTO = adminService.findAdminByEmail(email);
                String result = "?result=";
                log.info("이메일로 찾은 관리자 계정 : {}", adminDTO);

                log.info("아이디가 일치하는 검증하기.");
                if (id.equals(adminDTO.getId())) {
                    log.info("아이디 일치 확인. 비밀번호 변경 사이트 보내기.");
                    String uuid = UUID.randomUUID().toString();

                    log.info("메일 보내기 전에 auth token을 등록");
                    AuthDTO authDTO = AuthDTO.builder()
                            .token(uuid)
                            .id(id)
                            .use(AuthKind.FINDPW.getAuthKind())
                            .build();
                    authService.addAuthToken(authDTO);

                    log.info("메일 발송.");
                    mailService.sendFindEmail(email, AuthKind.FINDPW, uuid);

                    //메일까지 보냈으면 성공
                    result += "success";
                } else {
                    log.info("찾은 값이 없으면 실패로 보내기.");
                    result += "fail";

                }
                log.info("findinfo로 돌아가서 alert 만들기");
                resp.sendRedirect("/WEB-INF/admin/findinfo" + result);
            }

            case "/admin/changepw" -> {
                log.info("uuid값을 확인하기");
                String uuid = req.getParameter("uuid");
                String changePw = req.getParameter("pw");
                log.info("uuid 값 확인 : {}",uuid);
                log.info("비번 확인 : {}" , changePw);

                log.info("해당하는 계정 정보 확인하기");
                AuthDTO authDTO = authService.findAuthInfo(uuid);
                log.info(authDTO);
                log.info("토큰 검증");
                if (authDTO == null){
                    req.getRequestDispatcher("/WEB-INF/web/admin/TokenCanNotUse.jsp").forward(req,resp);
                }

                log.info("비밀번호 변경을 실행함.");

                log.info("연관된 계정 확인");
                AdminDTO adminDTO = adminService.findAdminById(authDTO.getId());

                log.info("비밀번호 암호화 해서 변경 시키기");
                adminDTO.setPassword(BCrypt.hashpw(changePw,BCrypt.gensalt()));
                adminService.changeAdmin(adminDTO);

                log.info("토큰 만료시키기");
                authService.useUUID(uuid);

                log.info("로그인 창으로 이동");
                resp.sendRedirect(command + "?result=success");
            }
        }
    }
}
