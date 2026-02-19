<%--
header 위치에 대시보드랑 통계로 가는 링크 설정
nav 구성
logout 구성

login 해야 들어올 수 있으니 logout만 구성하면 됌.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<header class="top-nav">
    <div class="header-container">
        <div class="logo" style="corsor:pointer" onclick="location.href='/'">
            <i class="fas fa-parking"></i> 스마트주차 반월당점
        </div>
        <nav>
            <ul>
                <li><a href="/dashboard">대시보드</a></li>
                <li>
                    <a href="/monthly/list">회원 관리</a>
                    <ul class="dropdown">
                        <li><a href="/monthly/list">회원 목록</a></li>
                        <li><a href="/monthly/register">회원 등록</a></li>
                    </ul>
                </li>
                <li><a href="/setting">요금 정책</a></li>
                <li><a href="/statistics">통계</a></li>
                <li><a href="/test">테스트 센터</a></li>
                <li><a href="/admin/logout">로그아웃</a></li>
            </ul>
        </nav>
    </div>
</header>

