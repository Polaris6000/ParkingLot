package com.example.parkinglot.filter;

import com.example.parkinglot.dto.AdminDTO;
import com.example.parkinglot.service.AdminService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Log4j2
@WebFilter(urlPatterns = {"/dashboard/*","/monthly/*","/setting/*","/statistics/*"})
public class LoginCheckFilter implements Filter {

    //필터의 설정
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    //필터의 작업
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("LoginCheckFilter doFIlter() called");

        //session에서 로그인 정보를 확인
        //있으면 다음 필터 동작. 없으면 로그인 페이지로.
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession();
        HttpServletResponse resp = (HttpServletResponse) response;
        Cookie[] cookies= req.getCookies();

        //session에 로그인 된 사람이지 확인.
//        if (session.getAttribute("loginInfo") == null) {
//            log.info("로그인한 정보가 없는 사용자");
//            resp.sendRedirect("/member/login");
//            return;
//        }

        //쿠키 값에서 remember-me 쿠키가 있는지 확인. 있으면 로그인 시키기
        //1. 로그인한 사용자인 경우
        if (session.getAttribute("loginInfo") != null) {
            log.info("로그인한 사용자");
            chain.doFilter(request, response);
            return;
        }
        //2. 로그인하지 않은 사용자
        Cookie cookie = findCookie(cookies, "remember-me");
        log.info("cookie값 확인 : {}" , cookie);
        //1) 쿠키 없음.
        if (cookie == null){
            log.info("자동로그인 쿠키가 없는 경우");
            resp.sendRedirect("/admin/login");
            return;
        }

        //2) 쿠키는 있고
        //
        // DB에 없음  or 있음.
        String uuid = cookie.getValue();
        log.info("uuid 정보좀 보자 : {}",uuid);
        AdminService adminService = AdminService.getINSTANCE();
        AdminDTO adminDTO = adminService.getAdminByUUID(uuid);
        log.info("그럼 멤버 dto가 어떻게 된겨? : {}", adminDTO);

        if (adminDTO == null){
            log.info("쿠키는 있고, db정보가 없음");
            resp.sendRedirect("/admin/login");
            return;
        }else {
            //여기에 login 해주면 되잖아?
            session.setAttribute("loginInfo", adminDTO);
            chain.doFilter(request, response);
            return;
        }

        /*
        doFilter 의 마지막에는 다음 필터나 목적지(서블릿, jsp)로 갈 수 있도록 FilterChain의 doFilter()를 실행.
        만일 문제가 생겨서 더이상 진행할 수 없다면 다음 단계로 진행하지 않고 다른 방식으로 리다이렉트 처리.
         */
    }

    //필터의 종료
    @Override
    public void destroy() {
        Filter.super.destroy();
    }


    private Cookie findCookie(Cookie[] cookies, String cookieName) {
        /*
        매배변수로 받은 cookies 배열에서 cookiename과 일치하는 쿠키를 찾아 반환.
        없으면 cookieNmae으로 이름이 지엉된 새로운 쿠키를 생성해서 반환
         */
        log.info("쿠키 찾기 확인");
        Cookie targetCookie = null;

        if (cookies != null) { //매개변수로 받은 쿠기가 존재함.
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    log.info("있네? : {}", cookie);
                    targetCookie = cookie; //쿠키의 이름이 일치하는 경우
                    break;
                }
            }
        }
        return targetCookie;
    }
}
