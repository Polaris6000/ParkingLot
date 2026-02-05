package com.exmple.parkinglot.controller;

import com.exmple.parkinglot.domain.DashboardStatsVO;
import com.exmple.parkinglot.domain.ParkingSpotVO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
class DashboardControllerTest {

    private DashboardController controller;
    private MiniRequest request;
    private MiniResponse response;

    @BeforeEach
    void setUp() {
        controller = new DashboardController();
        request = new MiniRequest();
        response = new MiniResponse();
        log.info("=== 순수 자바 인터페이스 구현체로 테스트 시작 ===");
    }

    @Test
    @DisplayName("GET 요청 시 데이터 바인딩 테스트")
    void testDoGet() throws Exception {
        // 실행
        controller.doGet(request, response);

        // 검증
        DashboardStatsVO stats = (DashboardStatsVO) request.getAttribute("stats");
        List<ParkingSpotVO> parkingSpots = (List<ParkingSpotVO>) request.getAttribute("parkingSpots");

        assertNotNull(stats, "통계 데이터가 담겨야 합니다.");
        assertNotNull(parkingSpots, "주차 구역 리스트가 담겨야 합니다.");
        log.info("테스트 성공! 현재 점유 차량 수: {}", stats.getCurrentParkedCount());
    }

    // --- 톰캣 없이 돌리기 위한 최소한의 가짜 클래스 (직접 구현) ---

    class MiniRequest implements HttpServletRequest {
        private final Map<String, Object> attributes = new HashMap<>();

        @Override public void setAttribute(String name, Object o) { attributes.put(name, o); }
        @Override public Object getAttribute(String name) { return attributes.get(name); }

        @Override
        public RequestDispatcher getRequestDispatcher(String path) {
            return new RequestDispatcher() {
                @Override public void forward(ServletRequest req, ServletResponse res) {}
                @Override public void include(ServletRequest req, ServletResponse res) {}
            };
        }

        // 아래는 인터페이스 때문에 억지로 만든 빈 메서드들 (무시해도 됨)
        @Override public String getParameter(String name) { return null; }
        @Override public Enumeration<String> getAttributeNames() { return Collections.enumeration(attributes.keySet()); }
        @Override public String getAuthType() { return null; }
        @Override public Cookie[] getCookies() { return new Cookie[0]; }
        @Override public long getDateHeader(String name) { return 0; }
        @Override public String getHeader(String name) { return null; }
        @Override public Enumeration<String> getHeaders(String name) { return null; }
        @Override public Enumeration<String> getHeaderNames() { return null; }
        @Override public int getIntHeader(String name) { return 0; }
        @Override public String getMethod() { return "GET"; }
        @Override public String getPathInfo() { return null; }
        @Override public String getPathTranslated() { return null; }
        @Override public String getContextPath() { return ""; }
        @Override public String getQueryString() { return null; }
        @Override public String getRemoteUser() { return null; }
        @Override public boolean isUserInRole(String role) { return false; }
        @Override public java.security.Principal getUserPrincipal() { return null; }
        @Override public String getRequestedSessionId() { return null; }
        @Override public String getRequestURI() { return "/dashboard"; }
        @Override public StringBuffer getRequestURL() { return null; }
        @Override public String getServletPath() { return "/dashboard"; }
        @Override public HttpSession getSession(boolean create) { return null; }
        @Override public HttpSession getSession() { return null; }
        @Override public String changeSessionId() { return null; }
        @Override public boolean authenticate(HttpServletResponse response) { return false; }
        @Override public void login(String username, String password) {}
        @Override public void logout() {}
        @Override public Collection<Part> getParts() { return null; }
        @Override public Part getPart(String name) { return null; }
        @Override public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) { return null; }
        @Override public Object getAttribute(String name, int scope) { return null; }
        @Override public void removeAttribute(String name) {}
        @Override public String getCharacterEncoding() { return null; }
        @Override public void setCharacterEncoding(String env) {}
        @Override public int getContentLength() { return 0; }
        @Override public long getContentLengthLong() { return 0; }
        @Override public String getContentType() { return null; }
        @Override public ServletInputStream getInputStream() { return null; }
        @Override public Map<String, String[]> getParameterMap() { return null; }
        @Override public Enumeration<String> getParameterNames() { return null; }
        @Override public String[] getParameterValues(String name) { return new String[0]; }
        @Override public String getProtocol() { return null; }
        @Override public String getScheme() { return null; }
        @Override public String getServerName() { return null; }
        @Override public int getServerPort() { return 0; }
        @Override public java.io.BufferedReader getReader() { return null; }
        @Override public String getRemoteAddr() { return null; }
        @Override public String getRemoteHost() { return null; }
        @Override public void setAttribute(String name, Object o, int scope) {}
        @Override public void removeAttribute(String name, int scope) {}
        @Override public Locale getLocale() { return null; }
        @Override public Enumeration<Locale> getLocales() { return null; }
        @Override public boolean isSecure() { return false; }
        @Override public int getRemotePort() { return 0; }
        @Override public String getLocalName() { return null; }
        @Override public String getLocalAddr() { return null; }
        @Override public int getLocalPort() { return 0; }
        @Override public ServletContext getServletContext() { return null; }
        @Override public AsyncContext startAsync() { return null; }
        @Override public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) { return null; }
        @Override public boolean isAsyncStarted() { return false; }
        @Override public boolean isAsyncSupported() { return false; }
        @Override public AsyncContext getAsyncContext() { return null; }
        @Override public DispatcherType getDispatcherType() { return null; }
    }

    class MiniResponse implements HttpServletResponse {
        // Response도 인터페이스라 모든 메서드를 빈 통으로 만들어야 함 (생략...)
        // 테스트에 필요한 로직이 없으면 그냥 깡통으로 놔둬도 됨
        @Override public void addCookie(Cookie cookie) {}
        @Override public boolean containsHeader(String name) { return false; }
        @Override public String encodeURL(String url) { return null; }
        @Override public String encodeRedirectURL(String url) { return null; }
        @Override public void sendError(int sc, String msg) {}
        @Override public void sendError(int sc) {}
        @Override public void sendRedirect(String location) {}
        @Override public void setDateHeader(String name, long date) {}
        @Override public void addDateHeader(String name, long date) {}
        @Override public void setHeader(String name, String value) {}
        @Override public void addHeader(String name, String value) {}
        @Override public void setIntHeader(String name, int value) {}
        @Override public void addIntHeader(String name, int value) {}
        @Override public void setStatus(int sc) {}
        @Override public int getStatus() { return 200; }
        @Override public String getHeader(String name) { return null; }
        @Override public Collection<String> getHeaders(String name) { return null; }
        @Override public Collection<String> getHeaderNames() { return null; }
        @Override public String getCharacterEncoding() { return null; }
        @Override public String getContentType() { return null; }
        @Override public ServletOutputStream getOutputStream() { return null; }
        @Override public java.io.PrintWriter getWriter() { return null; }
        @Override public void setCharacterEncoding(String charset) {}
        @Override public void setContentLength(int len) {}
        @Override public void setContentLengthLong(long len) {}
        @Override public void setContentType(String type) {}
        @Override public void setBufferSize(int size) {}
        @Override public int getBufferSize() { return 0; }
        @Override public void flushBuffer() {}
        @Override public void resetBuffer() {}
        @Override public boolean isCommitted() { return false; }
        @Override public void reset() {}
        @Override public void setLocale(Locale loc) {}
        @Override public Locale getLocale() { return null; }
    }
}