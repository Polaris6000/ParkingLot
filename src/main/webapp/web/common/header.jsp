<%--
header 위치에 대시보드랑 통계로 가는 링크 설정
nav 구성
logout 구성

login 해야 들어올 수 있으니 logout만 구성하면 됌.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="../static/css/common/header.css">

<%--기본적인 내용 구성하기.--%>
<nav>
    <ul>
        <li><a href="${pageContext.request.contextPath}/web/dashboard.jsp">대시보드</a></li>
        <li><a href="${pageContext.request.contextPath}/web/common/CarInfo.jsp">자동차 정보</a></li>
        <li><a href="${pageContext.request.contextPath}/web/Stastics.jsp">통계</a></li>
        <li><a href="${pageContext.request.contextPath}/web/test_data.jsp">테스트 센터</a></li>
        <li><a href="${pageContext.request.contextPath}/web/admin/Login.jsp">로그아웃</a></li>
    </ul>
</nav>
